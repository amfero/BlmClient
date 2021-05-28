package me.amfero.blmclient.module.render;

import java.awt.Color;

import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import me.amfero.blmclient.setting.Setting;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SkyColor extends Module
{
	Setting rainbow = new Setting("Rainbow", this, false);
	Setting red = new Setting("Red", this, 120, 0, 255);
	Setting green = new Setting("Green", this, 120, 0, 255);
	Setting blue = new Setting("Blue", this, 210, 0, 255);

	public SkyColor() 
	{
		super("SkyColor", "", Category.RENDER);
		addSetting(rainbow);
		addSetting(red);
		addSetting(green);
		addSetting(blue);
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
    public void onFogColorRender(final EntityViewRenderEvent.FogColors event)
	{
        final float[] hue = { System.currentTimeMillis() % 11520L / 11520.0f };
        final int rgb = Color.HSBtoRGB(hue[0], 1.0f, 1.0f);
        final int r = rgb >> 16 & 0xFF;
        final int g = rgb >> 8 & 0xFF;
        final int b = rgb & 0xFF;
        if (this.rainbow.getBooleanValue()) 
        {
            event.setRed(r / 255.0f);
            event.setGreen(g / 255.0f);
            event.setBlue(b / 255.0f);
        }
        else
        {
            event.setRed(this.red.getIntegerValue() / 255.0f);
            event.setGreen(this.green.getIntegerValue() / 255.0f);
            event.setBlue(this.blue.getIntegerValue() / 255.0f);
        }
    }
	
	@SubscribeEvent
	public void fog(EntityViewRenderEvent.FogDensity event) 
	{
		event.setDensity(0);
		event.setCanceled(true);
	}
}