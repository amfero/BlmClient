package me.amfero.blmclient.module.render;

import java.awt.Color;

import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import me.amfero.blmclient.util.RenderUtil;
import me.amfero.blmclient.util.font.FontUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Crosshair extends Module 
{
	public Crosshair() {
		super("Crosshair", "", Category.RENDER);
	}
	
	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent.Post event)
	{
		if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR && mc.gameSettings.thirdPersonView == 0) 
		{
			ScaledResolution sr = new ScaledResolution(mc);
			GlStateManager.pushMatrix();
			GlStateManager.scale(1.5, 1.5, 1.5);
			//mc.fontRenderer.drawString("+", sr.getScaledWidth() / 3 - 1, sr.getScaledHeight() / 3 - 1, new Color(1,1,1,50).getRGB());
			//mc.fontRenderer.drawString("+", sr.getScaledWidth() / 3 + 1, sr.getScaledHeight() / 3 + 1, new Color(1,1,1,50).getRGB());
			//mc.fontRenderer.drawString("+", sr.getScaledWidth() / 3 - 1, sr.getScaledHeight() / 3 + 1, new Color(1,1,1,50).getRGB());
			//mc.fontRenderer.drawString("+", sr.getScaledWidth() / 3 + 1, sr.getScaledHeight() / 3 - 1, new Color(1,1,1,50).getRGB());
			//FontUtil.drawStringWithShadow("+", sr.getScaledWidth() / 3 - 3, sr.getScaledHeight() / 3 - 3, new Color(RenderUtil.getRGB(5, 1, 0.8f)).getRGB());
			Gui.drawRect(sr.getScaledWidth() / 3 - 4, sr.getScaledHeight() / 3 + 6, sr.getScaledWidth() / 3 + 5, sr.getScaledHeight() / 3 + 7, new Color(1,1,1,50).getRGB());
			Gui.drawRect(sr.getScaledWidth() / 3, sr.getScaledHeight() / 3 - 3, sr.getScaledWidth() / 3 + 1, sr.getScaledHeight() / 3 + 4, new Color(RenderUtil.getRGB(5, 1, 0.8f)).getRGB());
			Gui.drawRect(sr.getScaledWidth() / 3 - 3, sr.getScaledHeight() / 3, sr.getScaledWidth() / 3 + 4, sr.getScaledHeight() / 3 + 1, new Color(RenderUtil.getRGB(5, 1, 0.8f)).getRGB());
			//FontUtil.drawStringWithShadow("_", sr.getScaledWidth() / 3, sr.getScaledHeight() / 3, new Color(RenderUtil.getRGB(3, 1, 0.6f)).getRGB());
			GlStateManager.popMatrix();
		}
	}
}
