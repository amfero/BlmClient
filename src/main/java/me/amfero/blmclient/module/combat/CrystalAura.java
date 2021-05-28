package me.amfero.blmclient.module.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import me.amfero.blmclient.event.events.PacketReceiveEvent;
import me.amfero.blmclient.mixin.accessor.ICPacketUseEntity;
import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import me.amfero.blmclient.setting.Setting;
import me.amfero.blmclient.util.BlockUtil;
import me.amfero.blmclient.util.ItemUtil;
import me.amfero.blmclient.util.PlayerUtil;
import me.amfero.blmclient.util.RenderUtil;
import me.amfero.blmclient.util.Timer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CrystalAura extends Module 
{
	Setting renderMode = new Setting("RenderMode", this, Arrays.asList("Top", "Full"));
	Setting render = new Setting("Render", this, true);
	Setting rotate = new Setting("Rotate", this, true);
	Setting predick = new Setting("Predick", this, true);
	Setting place = new Setting("Place", this, true);
	Setting placeDelay = new Setting("PlaceDelay", this, 0, 0, 20);
	Setting breakDelay = new Setting("BreakDelay", this, 1, 0, 20);
	Setting minDmg = new Setting("MinDmg", this, 8, 0, 36);
	Setting maxSelf = new Setting("MaxSelfDmg", this, 8, 0, 36);
	Setting range = new Setting("Range", this, 55, 10, 80);
	
	EntityEnderCrystal nearestCrystal;
	BlockPos pos = null;
	Timer placeTimer = new Timer();
	Timer breakTimer = new Timer();
	
	public CrystalAura()
	{
		super("CrystalAura", "", Category.COMBAT);
		addSetting(renderMode);
		addSetting(render);
		addSetting(rotate);
		addSetting(predick);
		addSetting(place);
		addSetting(placeDelay);
		addSetting(breakDelay);
		addSetting(minDmg);
		addSetting(maxSelf);
		addSetting(range);
	}

	@SubscribeEvent
	public void onUpdate(TickEvent.ClientTickEvent event)
	{
		if(nullCheck()) 
		{
			disable();
			return;
		}
		nearestCrystal = mc.world.loadedEntityList.stream()
				.filter(entity -> entity instanceof EntityEnderCrystal)
				.filter(entity -> mc.player.getDistance(entity) <= (double)(range.getIntegerValue() / 10))
				.map(entity -> (EntityEnderCrystal) entity)
				.min(Comparator.comparing(crystal -> mc.player.getDistance(crystal)))
				.orElse(null);
		doCrystalAura();
	}
	
	@SubscribeEvent
    public void onPacketReceive(PacketReceiveEvent event) 
    {
        if(!predick.getBooleanValue()) return;
        if (event.getPacket() instanceof SPacketSpawnObject) 
        {
        	SPacketSpawnObject packet2 = (SPacketSpawnObject)event.getPacket();
        	if (packet2.getType() == 51) 
        	{
        		CPacketUseEntity useEntity = new CPacketUseEntity();
        		((ICPacketUseEntity)useEntity).setEntityId(packet2.getEntityID());
        		((ICPacketUseEntity)useEntity).setAction(Action.ATTACK);
        		mc.getConnection().sendPacket(useEntity);
            }	
        }
    }
	
	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event)
	{
		if(nullCheck()) return;
        if (render.getBooleanValue() && nearestCrystal != null)
        {
        	Color r = new Color(RenderUtil.getRGB(6, 1, 0.6f));
            BlockPos pos = nearestCrystal.getPosition().add(0, -1, 0);
            if (!(mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK)) return;
            AxisAlignedBB bb;
            if(renderMode.getEnumValue().equals("Full"))
            {
            	bb = RenderUtil.generateBB(pos.getX(), pos.getY(), pos.getZ());
            }
            else 
            {
            	bb = RenderUtil.generateBBTop(pos.getX(), pos.getY(), pos.getZ());
            }
            RenderUtil.drawBoxOutline(bb, r.getRed() / 255f, r.getGreen() / 255f, r.getBlue() / 255f, 0.5f, 0.15f);
        }
    }
	
	private void doCrystalAura()
	{
		double dmg = .5;
		final List<EntityPlayer> entities = mc.world.playerEntities.stream()
				.filter(entityPlayer -> entityPlayer != mc.player)
				.collect(Collectors.toList());
		for (EntityPlayer entity2 : entities) 
        {
            if (entity2.getHealth() <= 0.0f || mc.player.getDistance(entity2) > (double)(range.getIntegerValue() / 10)) continue;
            for (final BlockPos blockPos : possiblePlacePositions((float) (range.getIntegerValue() / 10), true))
            {
                final double d = PlayerUtil.calcDmg(blockPos, entity2);
                if (d < minDmg.getIntegerValue() && entity2.getHealth() + entity2.getAbsorptionAmount() > 4 || d <= dmg || PlayerUtil.calcDmg(blockPos, mc.player) > maxSelf.getIntegerValue()) continue;
                dmg = d;
                pos = blockPos;
            }
        }
		if (dmg == .5) return;
		if (nearestCrystal != null && breakTimer.hasTimeElapsed(breakDelay.getIntegerValue() * 50, true)) 
		{
			EnumFacing side = getFirstFacing(nearestCrystal.getPosition());
        	BlockPos neighbour = nearestCrystal.getPosition().offset(side);
        	EnumFacing opposite = side.getOpposite();
        	Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        	if(rotate.getBooleanValue()) faceVector(hitVec, false);
            mc.player.swingArm(EnumHand.OFF_HAND);
            mc.playerController.attackEntity(mc.player, nearestCrystal);
        }
		if (place.getBooleanValue())
        {
        	if(!(ItemUtil.getHotbarItemSlot(Items.END_CRYSTAL) != -1)) return;
        	ItemUtil.switchToSlot(ItemUtil.getHotbarItemSlot(Items.END_CRYSTAL));
        	boolean mainhand = (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL);
            boolean offhand = (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL);
        	mc.playerController.updateController();
            if (offhand || mainhand) 
        	{
            	EnumFacing side = getFirstFacing(pos);
            	BlockPos neighbour = pos.offset(side);
            	EnumFacing opposite = side.getOpposite();
            	Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
            	if(rotate.getBooleanValue()) faceVector(hitVec, false);
            	if (placeTimer.hasTimeElapsed(placeDelay.getIntegerValue() * 50, true)) BlockUtil.placeCrystalOnBlock(pos, EnumHand.MAIN_HAND);
        	}
        }
	}
	
	private static List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plusY) 
	{
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) 
        {
            for (int z = cz - (int) r; z <= cz + r; z++) 
            {
                for (int y = (sphere ? cy - (int) r : cy - h); y < (sphere ? cy + r : cy + h); y++)
                {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plusY, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }
	
	private static BlockPos getPlayerPos() 
	{
		Minecraft mc = Minecraft.getMinecraft();
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }
	
	private static List<BlockPos> possiblePlacePositions(float placeRange, boolean specialEntityCheck) 
	{
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(getSphere(getPlayerPos(), placeRange, (int) placeRange, false, true, 0).stream().filter(pos -> canPlaceCrystal(pos, specialEntityCheck)).collect(Collectors.toList()));
        return positions;
    }
	
	private static boolean canPlaceCrystal(BlockPos blockPos, boolean specialEntityCheck) 
	{
		Minecraft mc = Minecraft.getMinecraft();
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        try
        {
        	if (mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) 
        	{
        		return false;
        	}
        	if (mc.world.getBlockState(boost).getBlock() != Blocks.AIR || mc.world.getBlockState(boost2).getBlock() != Blocks.AIR)
        	{
        		return false;
        	}
        	if (!specialEntityCheck) 
        	{
        		return mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
        	}
        	for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) 
        	{
        		if (entity instanceof EntityEnderCrystal) continue;
        		return false;
        	}
        	for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)))
        	{
        		if (entity instanceof EntityEnderCrystal) continue;
        		return false;
        	}
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
	
	public static Vec3d getEyesPos() {
		Minecraft mc = Minecraft.getMinecraft();
        return new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
    }
	
	private static float[] getLegitRotations(Vec3d vec)
	{
		Minecraft mc = Minecraft.getMinecraft();
		Vec3d eyesPos = getEyesPos();
		double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));
        return new float[]
        		{
        				mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw),
        				mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch)
        		};
	}
	
	private static void faceVector(Vec3d vec, boolean normalizeAngle) 
	{
		Minecraft mc = Minecraft.getMinecraft();
		float[] rotations = getLegitRotations(vec);
		mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], normalizeAngle ? MathHelper.normalizeAngle((int) rotations[1], 360) : rotations[1], mc.player.onGround));
	}
	
	private static EnumFacing getFirstFacing(BlockPos pos)
	{
		for (EnumFacing facing : getPossibleSides(pos))
		{
			return facing;
		}
		return null;
	}
	
	private static List<EnumFacing> getPossibleSides(BlockPos pos) 
	{
		Minecraft mc = Minecraft.getMinecraft();
		List<EnumFacing> facings = new ArrayList<>();
		for (EnumFacing side : EnumFacing.values()) 
		{
			BlockPos neighbour = pos.offset(side);
			if (mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false)) 
			{
				IBlockState blockState = mc.world.getBlockState(neighbour);
				if (!blockState.getMaterial().isReplaceable())
				{
					facings.add(side);
				}
			}
		}
		return facings;
	}
}