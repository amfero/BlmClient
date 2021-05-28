package me.amfero.blmclient.module.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.amfero.blmclient.Client;
import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import me.amfero.blmclient.setting.Setting;
import me.amfero.blmclient.util.BlockUtil;
import me.amfero.blmclient.util.ItemUtil;
import me.amfero.blmclient.util.RenderUtil;
import me.amfero.blmclient.util.Timer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AntiEndCrystal extends Module 
{
	Setting rotate = new Setting("Rotate", this, true);
	Setting render = new Setting("Render", this, true);
	Setting predict = new Setting("Predict", this, true);
	Setting delay = new Setting("Delay", this, 0, 0, 20);
	Setting range = new Setting("Range", this, 55, 10, 80);
	
	int index = 0;
	Timer timer = new Timer();
	
	public AntiEndCrystal() 
	{
		super("AntiEndCrystal", "", Category.COMBAT);
		addSetting(rotate);
		addSetting(predict);
		addSetting(render);
		addSetting(delay);
		addSetting(range);
	}

	@SubscribeEvent
	public void onUpdate(TickEvent.ClientTickEvent event)
	{
		if(nullCheck()) return;
		if(predict.getBooleanValue()) 
		{
			for (EntityPlayer player : mc.world.playerEntities) 
			{
				if(!(player.getName() == mc.player.getName())) 
				{
					Item mainHandItem = player.getHeldItemMainhand().getItem();	
					Item offHandItem = player.getHeldItemOffhand().getItem();
					if(mainHandItem == Items.END_CRYSTAL || offHandItem == Items.END_CRYSTAL)
					{
						for(BlockPos blockPos : possiblePlacePositions((float)(range.getIntegerValue() / 10), true))
						{
							if (!timer.hasTimeElapsed(delay.getIntegerValue() * 50, true)) continue;
							EnumFacing side = getFirstFacing(blockPos);
			            	BlockPos neighbour = blockPos.offset(side);
			            	EnumFacing opposite = side.getOpposite();
			            	Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
			            	if(rotate.getBooleanValue()) faceVector(hitVec, false);
							int oldSlot = mc.player.inventory.currentItem;
							if(!(ItemUtil.getHotbarItemSlot(Items.END_CRYSTAL) != -1)) return;
							int slot = ItemUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.WOODEN_PRESSURE_PLATE));
							mc.getConnection().sendPacket(new CPacketHeldItemChange(slot));
							BlockUtil.placeBlock(new BlockPos(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ()));
							mc.getConnection().sendPacket(new CPacketHeldItemChange(oldSlot));
						}	
					}				
				}
			}
		}
		for (final EntityEnderCrystal cry : getNonPlaced()) 
		{
            if (timer.hasTimeElapsed(delay.getIntegerValue() * 50, true)) 
            {
            	EnumFacing side = getFirstFacing(cry.getPosition());
            	BlockPos neighbour = cry.getPosition().offset(side);
            	EnumFacing opposite = side.getOpposite();
            	Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
            	if(rotate.getBooleanValue()) faceVector(hitVec, false);
            	int oldSlot = mc.player.inventory.currentItem;
            	if(!(ItemUtil.getHotbarItemSlot(Item.getItemFromBlock(Blocks.WOODEN_PRESSURE_PLATE)) != -1)) return;
                int slot = ItemUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.WOODEN_PRESSURE_PLATE));
                mc.getConnection().sendPacket(new CPacketHeldItemChange(slot));
                BlockUtil.placeBlock(cry.getPosition());
                mc.getConnection().sendPacket(new CPacketHeldItemChange(oldSlot));
                continue;
            }
        }
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event)
	{
    	if(nullCheck()) return;
    	for (final EntityEnderCrystal cry : getInRange()) 
		{
    		if(render.getBooleanValue())
    		{
    			Color r = new Color(RenderUtil.getRGB(6, 1, 0.6f));
                BlockPos pos = cry.getPosition().add(0, -1, 0);
                if (!(mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK)) return;
                AxisAlignedBB bb;
                bb = RenderUtil.generateBBTop(pos.getX(), pos.getY(), pos.getZ());
                RenderUtil.drawBoxOutline(bb, r.getRed() / 255f, r.getGreen() / 255f, r.getBlue() / 255f, 0.5f, 0.15f);
    		}
		}
    }
	
	public ArrayList<EntityEnderCrystal> getCrystals() {
        final ArrayList<EntityEnderCrystal> crystals = new ArrayList<EntityEnderCrystal>();
        for (final Entity ent : mc.world.getLoadedEntityList()) {
            if (ent instanceof EntityEnderCrystal) {
                crystals.add((EntityEnderCrystal)ent);
            }
        }
        return crystals;
    }
	
	public static List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plusY) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy - h); y < (sphere ? cy + r : cy + h); y++) {
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
	
	public static BlockPos getPlayerPos() {
		Minecraft mc = Minecraft.getMinecraft();
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }
	
	public static List<BlockPos> possiblePlacePositions(float placeRange, boolean specialEntityCheck) {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(getSphere(getPlayerPos(), placeRange, (int) placeRange, false, true, 0).stream().filter(pos -> canPlaceCrystal(pos, specialEntityCheck)).collect(Collectors.toList()));
        return positions;
    }
	
	public static boolean canPlaceCrystal(BlockPos blockPos, boolean specialEntityCheck) {
		Minecraft mc = Minecraft.getMinecraft();
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        try {
        	if (mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
        		return false;
        	}
        	if (mc.world.getBlockState(boost).getBlock() != Blocks.AIR || mc.world.getBlockState(boost2).getBlock() != Blocks.AIR) {
        		return false;
        	}
        	if (!specialEntityCheck) {
        		return mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
        	}
        	for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
        		if (entity instanceof EntityEnderCrystal) continue;
        		return false;
        	}
        	for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
        		if (entity instanceof EntityEnderCrystal) continue;
        		return false;
        	}
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    
    public ArrayList<EntityEnderCrystal> getInRange() {
        final ArrayList<EntityEnderCrystal> inRange = new ArrayList<EntityEnderCrystal>();
        for (final EntityEnderCrystal crystal : getCrystals()) {
            if (mc.player.getDistance((Entity)crystal) <= (double)(range.getIntegerValue() / 10)) {
                inRange.add(crystal);
            }
        }
        return inRange;
    }
    
    public ArrayList<EntityEnderCrystal> getNonPlaced() {
        final ArrayList<EntityEnderCrystal> returnOutput = new ArrayList<EntityEnderCrystal>();
        for (final EntityEnderCrystal crystal : getInRange()) {
            if (mc.world.getBlockState(crystal.getPosition()).getBlock() == Blocks.WOODEN_PRESSURE_PLATE) {
                continue;
            }
            returnOutput.add(crystal);
        }
        return returnOutput;
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
