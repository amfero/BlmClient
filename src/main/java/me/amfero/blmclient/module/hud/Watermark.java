package me.amfero.blmclient.module.hud;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import me.amfero.blmclient.Client;
import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import me.amfero.blmclient.setting.Setting;
import me.amfero.blmclient.util.MathUtil;
import me.amfero.blmclient.util.RenderUtil;
import me.amfero.blmclient.util.Timer;
import me.amfero.blmclient.util.font.FontUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Watermark extends Module
{
	Setting statiC = new Setting("Static", this, true);
	Setting rainbow = new Setting("Rainbow", this, false);
	int X = 5;
	int Y = 7;
	int W = 67;
	int H = 16;
	
	SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
	Date date = new Date();
	Timer timer = new Timer();
	
	public Watermark()
	{
		super("Watermark", "", Category.HUD);
		addSetting(statiC);
		addSetting(rainbow);
	}
	
	@SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) 
	{
		if(nullCheck()) return;
		if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) 
		{ 
			String ping = getPing(mc.player) + "MS";
			String server = Minecraft.getMinecraft().isSingleplayer() ? "singleplayer".toUpperCase() : mc.getCurrentServerData().serverIP.toUpperCase();
			String time = formatter.format(date);
			String wate = " BLM CLIENT | ";
			if(!statiC.getBooleanValue()) 
			{
				if(timer.hasTimeElapsed(0, false))
				{
					wate = " B | ";
				}
				if(timer.hasTimeElapsed(500, false))
				{
					wate = " BL | ";
				}
				if(timer.hasTimeElapsed(1000, false))
				{
					wate = " BLM | ";
				}
				if(timer.hasTimeElapsed(1500, false))
				{
					wate = " BLM C | ";
				}
				if(timer.hasTimeElapsed(2000, false))
				{
					wate = " BLM CL | ";
				}
				if(timer.hasTimeElapsed(2500, false))
				{
					wate = " BLM CLI | ";
				}
				if(timer.hasTimeElapsed(3000, false))
				{
					wate = " BLM CLIE | ";
				}
				if(timer.hasTimeElapsed(3500, false))
				{
					wate = " BLM CLIEN | ";
				}
				if(timer.hasTimeElapsed(4000, false))
				{
					wate = " BLM CLIENT | ";
				}
				if(timer.hasTimeElapsed(5000, false))
				{
					wate = " BLM CLIE | ";
				}
				if(timer.hasTimeElapsed(5500, false))
				{
					wate = " BLM CLI | ";
				}
				if(timer.hasTimeElapsed(6000, false))
				{
					wate = " BLM CL | ";
				}
				if(timer.hasTimeElapsed(6500, false))
				{
					wate = " BLM C | ";
				}
				if(timer.hasTimeElapsed(7000, false))
				{
					wate = " BLM | ";
				}
				if(timer.hasTimeElapsed(7500, false))
				{
					wate = " BL | ";
				}
				if(timer.hasTimeElapsed(8000, false))
				{
					wate = " B | ";
					timer.reset();
				}
			}
			else
			{
				timer.reset();
			}
			String waterma =  wate + time + " | " + server + " | " + ping;
			Gui.drawRect(X, Y - 2, X + Client.customFontRenderer.getStringWidth(waterma) + 8, Y + H - 3, new Color(24,14,60, 185).getRGB());
			RenderUtil.drawGradientSideways(X, Y - 2, X + Client.customFontRenderer.getStringWidth(waterma) + 8, Y, rainbow.getBooleanValue() ? rainbow(0) : new Color(64,41,213,255).getRGB(), rainbow.getBooleanValue() ? rainbow(0) : new Color(124,9,77, 255).getRGB());
			FontUtil.drawStringWithShadow(waterma, X + 2, Y + 4, new Color(255,255,255,255).getRGB());
		}
	}
	
	private static int rainbow(int delay) {
		double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 20.0);
		rainbowState %= 360;
		return Color.getHSBColor((float) (rainbowState / 360.0f), 0.70f, 1f).getRGB();
	}
	
	private int getPing(final EntityPlayer player) {
        int ping = 0;
        try 
        {
            ping = (int) MathUtil.clamp((float) Objects.requireNonNull(mc.getConnection()).getPlayerInfo(player.getUniqueID()).getResponseTime(), 1, 300.0f);
        }
        catch (NullPointerException ignored) { }
        return ping;
	}
}