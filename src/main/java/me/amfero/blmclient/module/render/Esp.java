package me.amfero.blmclient.module.render;

import java.awt.Color;

import me.amfero.blmclient.mixin.accessor.IRenderManager;
import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import me.amfero.blmclient.setting.Setting;
import me.amfero.blmclient.util.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Esp extends Module
{
	Setting players = new Setting("Players", this, true);
	Setting chests = new Setting("Chests", this, true);
	
	public Esp() 
	{
		super("Esp", "", Category.RENDER);
		addSetting(players);
		addSetting(chests);
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event)
	{
        for(Entity player : mc.world.getLoadedEntityList())
        {
        	if(player.getName() == mc.player.getName() || !(player instanceof EntityPlayer) || !(players.getBooleanValue())) continue;
        	Vec3d vec = interpolateEntity(player, mc.getRenderPartialTicks());
            double posX = vec.x - ((IRenderManager)mc.getRenderManager()).getRenderPosX();
            double posY = vec.y - ((IRenderManager)mc.getRenderManager()).getRenderPosY() + 0.13;
            double posZ = vec.z - ((IRenderManager)mc.getRenderManager()).getRenderPosZ();
            AxisAlignedBB bb = new AxisAlignedBB(0.0, 0.0, 0.0, (double)player.width, (double)player.height - 0.15, (double)player.width).offset(posX - (double)(player.width / 2.0f), posY, posZ - (double)(player.width / 2.0f)).grow(0.125);
            Color color = new Color(RenderUtil.getRGB(6, 1, 0.6f));
            RenderUtil.drawBoundingBox(bb, color, 1.1f);
        }
        for(TileEntity tileEntity : mc.world.loadedTileEntityList)
        {
        	if(!(tileEntity instanceof TileEntityChest) || !(chests.getBooleanValue())) continue;
        	Color r = new Color(RenderUtil.getRGB(6, 1, 0.6f));
            BlockPos pos = tileEntity.getPos();
            AxisAlignedBB bb;
            bb = RenderUtil.generateBB(pos.getX(), pos.getY(), pos.getZ());
            RenderUtil.drawBoxOutline(bb, r.getRed() / 255f, r.getGreen() / 255f, r.getBlue() / 255f, 1.1f, 0.102f);
        }
	}
	
	private Vec3d interpolateEntity(Entity entity, float time) 
	{
		return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)time);
	}
}
