package me.amfero.blmclient.module.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import me.amfero.blmclient.setting.Setting;
import me.amfero.blmclient.util.BlockUtil;
import me.amfero.blmclient.util.BurrowUtil;
import me.amfero.blmclient.util.ItemUtil;
import me.amfero.blmclient.util.RenderUtil;
import me.amfero.blmclient.util.Timer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Surround extends Module
{
	Setting center = new Setting("Center", this, true);
	Setting render = new Setting("Render", this, false);
	Setting rotate = new Setting("Rotate", this, true);
	Setting delay = new Setting("Delay", this, 2, 0, 20);
	
	Timer timer = new Timer();
	double startPosY;
	Vec3d playerPos;
	BlockPos rendere;
	
	public Surround()
	{
		super("Surround", "", Category.COMBAT);
		addSetting(center);
		addSetting(render);
		addSetting(rotate);
		addSetting(delay);
	}
	
	public void onEnable()
	{
		autoCenter();
		startPosY = mc.player.posY;
	}

	@SubscribeEvent
	public void onUpdate(TickEvent.ClientTickEvent event)
	{
		if(nullCheck()) 
		{
			disable();
			return;
		}
		if(startPosY < mc.player.posY || !mc.player.onGround) disable();
		for(BlockPos blockPos : blocks(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)))
		{
			try
			{
				if(BlockUtil.isPositionPlaceable(blockPos, true) == 0 || !timer.hasTimeElapsed(delay.getIntegerValue() * 50, true) || !(ItemUtil.getHotbarItemSlot(Item.getItemFromBlock(Blocks.OBSIDIAN)) != -1)) continue;
				int oldSlot = mc.player.inventory.currentItem;
				int slot = ItemUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
				mc.getConnection().sendPacket(new CPacketHeldItemChange(slot));
				EnumFacing side = getFirstFacing(blockPos);
				BlockPos neighbour = blockPos.offset(side);
				EnumFacing opposite = side.getOpposite();
				Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
				if(rotate.getBooleanValue()) faceVector(hitVec, false);
				BurrowUtil.placeBlock(blockPos, EnumHand.MAIN_HAND, false, false, false);
				mc.getConnection().sendPacket(new CPacketHeldItemChange(oldSlot));	
			}
			catch (Exception e) { }
		}
	}
	
	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event)
	{
		if(nullCheck()) 
		{
			disable();
			return;
		}
    	if(render.getBooleanValue())
		{
    		for (BlockPos renderPos : blocks(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ))) 
        	{
    			if(BlockUtil.isPositionPlaceable(renderPos, true) == 0 || !BlockUtil.canSeeBlock(renderPos)) continue;
        		Color r = new Color(RenderUtil.getRGB(6, 1, 0.6f));
        		AxisAlignedBB bb;
        		bb = RenderUtil.generateBB(renderPos.getX(), renderPos.getY(), renderPos.getZ());
        		RenderUtil.drawBoxOutline(bb, r.getRed() / 255f, r.getGreen() / 255f, r.getBlue() / 255f, 0.5f, 0.15f);
        	}
		}
    }
	
	private static List<BlockPos> blocks(BlockPos blockPos)
	{
		Minecraft mc = Minecraft.getMinecraft();
		List<BlockPos> blocks = new ArrayList<>();

		BlockPos v1 = blockPos.add(1, -1, 0);
        BlockPos v2 = blockPos.add(-1, -1, 0);
        BlockPos v3 = blockPos.add(0, -1, 1);
        BlockPos v4 = blockPos.add(0, -1, -1);
		BlockPos n1 = blockPos.add(1, 0, 0);
        BlockPos n2 = blockPos.add(-1, 0, 0);
        BlockPos n3 = blockPos.add(0, 0, 1);
        BlockPos n4 = blockPos.add(0, 0, -1);
		if(mc.player.posY < Math.round(mc.player.posY))
		{
			v1 = blockPos.add(1, 0, 0);
	        v2 = blockPos.add(-1, 0, 0);
	        v3 = blockPos.add(0, 0, 1);
	        v4 = blockPos.add(0, 0, -1);
			n1 = blockPos.add(1, 1, 0);
			n2 = blockPos.add(-1, 1, 0);
	        n3 = blockPos.add(0, 1, 1);
	        n4 = blockPos.add(0, 1, -1);
	        if(BlockUtil.isPositionPlaceable(n1, true) == 0) blocks.add(v1);
			if(BlockUtil.isPositionPlaceable(n2, true) == 0) blocks.add(v2);
			if(BlockUtil.isPositionPlaceable(n3, true) == 0) blocks.add(v3);
			if(BlockUtil.isPositionPlaceable(n4, true) == 0) blocks.add(v4);
		}
		if(BlockUtil.isPositionPlaceable(n1, true) != 0) blocks.add(v1);
		if(BlockUtil.isPositionPlaceable(n2, true) != 0) blocks.add(v2);
		if(BlockUtil.isPositionPlaceable(n3, true) != 0) blocks.add(v3);
		if(BlockUtil.isPositionPlaceable(n4, true) != 0) blocks.add(v4);
        blocks.add(n1);
        blocks.add(n2);
        blocks.add(n3);
        blocks.add(n4);
		return blocks;
	}
	
	double getDst(Vec3d vec) 
	{
        return playerPos.distanceTo(vec) ;
    }
	
	private void centerPlayer(double x, double y, double z)
	{
		if(!center.getBooleanValue()) return;
		Minecraft mc = Minecraft.getMinecraft();
		mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, true));
		mc.player.setPosition(x, y, z);
    }
	
	private void autoCenter()
	{
		BlockPos centerPos = Minecraft.getMinecraft().player.getPosition();
        playerPos = Minecraft.getMinecraft().player.getPositionVector();
        double x = centerPos.getX();
        double y = mc.player.posY;
        double z = centerPos.getZ();
        final Vec3d plusPlus = new Vec3d(x + 0.5, y, z + 0.5);
        final Vec3d plusMinus = new Vec3d(x + 0.5, y, z - 0.5);
        final Vec3d minusMinus = new Vec3d(x - 0.5, y, z - 0.5);
        final Vec3d minusPlus = new Vec3d(x - 0.5, y, z + 0.5);
		if (getDst(plusPlus) < getDst(plusMinus) && getDst(plusPlus) < getDst(minusMinus) && getDst(plusPlus) < getDst(minusPlus)) 
        {
            x = centerPos.getX() + 0.5;
            z = centerPos.getZ() + 0.5;
            centerPlayer(x, y, z);
        }
        if (getDst(plusMinus) < getDst(plusPlus) && getDst(plusMinus) < getDst(minusMinus) && getDst(plusMinus) < getDst(minusPlus))
        {
            x = centerPos.getX() + 0.5;
            z = centerPos.getZ() - 0.5;
            centerPlayer(x, y, z);
        }
        if (getDst(minusMinus) < getDst(plusPlus) && getDst(minusMinus) < getDst(plusMinus) && getDst(minusMinus) < getDst(minusPlus))
        {
            x = centerPos.getX() - 0.5;
            z = centerPos.getZ() - 0.5;
            centerPlayer(x, y, z);
        }
        if (getDst(minusPlus) < getDst(plusPlus) && getDst(minusPlus) < getDst(plusMinus) && getDst(minusPlus) < getDst(minusMinus))
        {
            x = centerPos.getX() - 0.5;
            z = centerPos.getZ() + 0.5;
            centerPlayer(x, y, z);
        }
	}
	
	private static void faceVector(Vec3d vec, boolean normalizeAngle) 
	{
		Minecraft mc = Minecraft.getMinecraft();
		float[] rotations = getLegitRotations(vec);
		mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], normalizeAngle ? MathHelper.normalizeAngle((int) rotations[1], 360) : rotations[1], mc.player.onGround));
	}
	
	public static Vec3d getEyesPos() 
	{
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
