package me.amfero.blmclient.module.render;

import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import me.amfero.blmclient.setting.Setting;
import me.amfero.blmclient.util.RenderUtil;
import me.amfero.blmclient.mixin.accessor.IMinecraft;
import me.amfero.blmclient.mixin.accessor.IRenderManager;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.Color;
import java.util.Objects;

import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;

public class Nametags extends Module
{
	Setting health = new Setting("Health", this, true);
	Setting ping = new Setting("Ping", this, true);
	Setting rainbowOutline = new Setting("RainbowOutline", this, true);
	Setting red = new Setting("Red", this, 0, 0, 255);
	Setting green = new Setting("Green", this, 0, 0, 255);
	Setting blue = new Setting("Blue", this, 0, 0, 255);
	Setting alpha = new Setting("Alpha", this, 255, 0, 255);
	Setting scale = new Setting("Scale", this, 36, 1, 100);
	
	public Nametags() 
	{
		super("Nametags", "", Category.RENDER);
		addSetting(health);
		addSetting(ping);
		addSetting(rainbowOutline);
		addSetting(red);
		addSetting(green);
		addSetting(blue);
		addSetting(alpha);
		addSetting(scale);
	}
	
	@SubscribeEvent
	public void onWorldRender(RenderWorldLastEvent event) 
	{ 	
		if(nullCheck()) return;
		for (EntityPlayer p : mc.world.playerEntities) 
		{
			if ((p != mc.getRenderViewEntity()) && (p.isEntityAlive())) 
			{
				double pX = p.lastTickPosX + (p.posX - p.lastTickPosX) * ((IMinecraft) mc).getTimer().renderPartialTicks - ((IRenderManager) mc.getRenderManager()).getRenderPosX();
				double pY = p.lastTickPosY + (p.posY - p.lastTickPosY) * ((IMinecraft) mc).getTimer().renderPartialTicks - ((IRenderManager) mc.getRenderManager()).getRenderPosY();
				double pZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * ((IMinecraft) mc).getTimer().renderPartialTicks - ((IRenderManager) mc.getRenderManager()).getRenderPosZ();
				if (!p.getName().startsWith("Body #")) 
				{
					renderNametag(p, pX, pY, pZ);
				}
			}
		}

	}

	private void renderNametag(EntityPlayer player, double x, double y, double z) 
	{
		GL11.glPushMatrix();
		String nameS;
		nameS = ChatFormatting.WHITE + player.getName();
		String pingS = "";
		if (ping.getBooleanValue()) 
		{
			pingS += " " + ChatFormatting.WHITE + getPing(player) + "ms";
		}

		String healthS = " ";

		float var14 = 0.016666668F * getNametagSize(player);
		GL11.glTranslated((float) x, (float) y + 2.5D, (float) z);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(-var14, -var14, var14);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(false);
		GL11.glDisable(2929);

		ChatFormatting healthColor = null;

		if (health.getBooleanValue()) 
		{
			final float health = totalHealth(player);
			if (health > 18.0f) {
				healthColor = ChatFormatting.GREEN;
			} else if (health > 16.0f) {
				healthColor = ChatFormatting.DARK_GREEN;
			} else if (health > 12.0f) {
				healthColor = ChatFormatting.YELLOW;
			} else if (health > 8.0f) {
				healthColor = ChatFormatting.GOLD;
			} else if (health > 5.0f) {
				healthColor = ChatFormatting.RED;
			} else {
				healthColor = ChatFormatting.DARK_RED;
			}
			healthS += (MathHelper.ceil(player.getHealth() + player.getAbsorptionAmount()));
		}

		int width = mc.fontRenderer.getStringWidth(nameS + pingS + healthS) / 2;
		RenderUtil.drawBorderedRect(-width - 4, 6, width + 4, 22, 1.2, 0x75000000, rainbowOutline.getBooleanValue() ? rainbow(0) : new Color(red.getIntegerValue(),green.getIntegerValue(),blue.getIntegerValue(), alpha.getIntegerValue()).getRGB());
		mc.fontRenderer.drawStringWithShadow(healthColor + nameS + pingS + healthS, -width, 10, new Color(24,14,60, 255).getRGB());
		int xOffset = 0;
		for (ItemStack armourStack : player.inventory.armorInventory) {
			if (armourStack != null) {
				xOffset -= 8;
			}
		}

		ItemStack renderStack;
		player.getHeldItemMainhand();
		xOffset -= 8;
		renderStack = player.getHeldItemMainhand().copy();
		renderItem(renderStack, xOffset, -10);
		xOffset += 16;
		for (int index = 3; index >= 0; --index) {
			ItemStack armourStack = player.inventory.armorInventory.get(index);
			ItemStack renderStack1 = armourStack.copy();

			renderItem(renderStack1, xOffset, -10);
			xOffset += 16;
		}

		ItemStack renderOffhand;
		player.getHeldItemOffhand();
		renderOffhand = player.getHeldItemOffhand().copy();
		renderItem(renderOffhand, xOffset, -10);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(true);
		GL11.glPopMatrix();
		GL11.glColor4f(1f, 1f, 1f, 1f);
	}

	private float getNametagSize(EntityLivingBase player) {
		ScaledResolution scaledRes = new ScaledResolution(mc);
		double twoDscale = scaledRes.getScaleFactor() / Math.pow(scaledRes.getScaleFactor(), 0.0D + scale.getIntegerValue() / 10);
		return (float) ((float) twoDscale + (mc.player.getDistance(player) / (0.8f * scale.getIntegerValue() / 5)));
	}

	private void renderItem(ItemStack stack, int x, int y) {
		GL11.glPushMatrix();
		GL11.glDepthMask(true);
		GlStateManager.clear(256);
		GlStateManager.disableDepth();
		GlStateManager.enableDepth();
		net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
		mc.getRenderItem().zLevel = -100.0F;
		GlStateManager.scale(1, 1, 0.01f);
		mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, (y / 2) - 12);
		mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x, (y / 2) - 12);
		mc.getRenderItem().zLevel = 0.0F;
		GlStateManager.scale(1, 1, 1);
		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
		GlStateManager.enableAlpha();
		GlStateManager.disableBlend();
		GlStateManager.disableLighting();
		GlStateManager.scale(0.5D, 0.5D, 0.5D);
		GlStateManager.disableDepth();
		GlStateManager.enableDepth();
		GlStateManager.scale(2.0F, 2.0F, 2.0F);
		GL11.glPopMatrix();
	}

	public int getPing(final EntityPlayer player) {
		int ping = 0;
		try {
			ping = (int) clamp((float) Objects.requireNonNull(mc.getConnection()).getPlayerInfo(player.getUniqueID())
					.getResponseTime(), 1, 300.0f);
		} catch (NullPointerException ignored) {
		}
		return ping;
	}

	public static int totalHealth(EntityPlayer entity) {
		return (int) (entity.getHealth() + entity.getAbsorptionAmount());
	}

	public static int rainbow(int delay) {
		double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 20.0);
		rainbowState %= 360;
		return Color.getHSBColor((float) (rainbowState / 360.0f), 0.70f, 1f).getRGB();
	}

	public static Color rainbowColor(int delay) {
		double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 20.0);
		rainbowState %= 360;
		return Color.getHSBColor((float) (rainbowState / 360.0f), 0.8f, 0.7f);
	}

	public int color(int index, int count) {
		float[] hsb = new float[3];

		Color.RGBtoHSB(255, 255, 255, hsb);

		float brightness = Math.abs(((getOffset() + (index / (float) count) * 2) % 2) - 1);
		brightness = 0.5f + (0.4f * brightness);

		hsb[2] = brightness % 1f;
		return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
	}

	private float getOffset() {
		return (System.currentTimeMillis() % 2000) / 1000f;
	}

	public static float clamp(float val, float min, float max) {
		if (val <= min) {
			val = min;
		}
		if (val >= max) {
			val = max;
		}
		return val;
	}
}
