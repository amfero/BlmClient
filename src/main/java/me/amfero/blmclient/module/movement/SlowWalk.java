package me.amfero.blmclient.module.movement;

import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import me.amfero.blmclient.setting.Setting;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SlowWalk extends Module 
{
	Setting sneakOnly = new Setting("SneakOnly", this, false);
	
	public SlowWalk() 
	{
		super("SlowWalk", "", Category.MOVEMENT);
		addSetting(sneakOnly);
	}
	
	@SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent event) 
	{
		if(nullCheck()) return; 
		if(sneakOnly.getBooleanValue()) 
		{
			if(mc.player.isSneaking()) 
			{
				mc.player.motionX = 0.001;
				mc.player.motionZ = 0.001;	
			}		
		} 
		else 
		{
			mc.player.motionX = 0.001;
			mc.player.motionZ = 0.001;	
		}
	}
}