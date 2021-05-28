package me.amfero.blmclient.module.render;

import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class NoWeather extends Module 
{
	public NoWeather() 
	{
		super("NoWeather", "", Category.RENDER);
	}
	
	@SubscribeEvent
	public void onUpdate(final TickEvent.ClientTickEvent event) 
	{
		if(nullCheck()) return;
		mc.world.setRainStrength(0);
	}
}