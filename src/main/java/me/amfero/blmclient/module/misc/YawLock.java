package me.amfero.blmclient.module.misc;

import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import me.amfero.blmclient.setting.Setting;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class YawLock extends Module
{
	Setting slice = new Setting("Slice", this, 8, 0, 8);
	
	public YawLock() 
	{
		super("YawLock", "", Category.MISC);
		addSetting(slice);
	}
	
	@SubscribeEvent
	public void onUpdate(final TickEvent.ClientTickEvent event) 
	{
		if(nullCheck()) return;
		if(slice.getIntegerValue() == 0) return;
		int angle = 360 / slice.getIntegerValue();
		float yaw = mc.player.rotationYaw;
		mc.player.rotationYaw = yaw = (float)(Math.round(yaw / (float)angle) * angle);
		if (mc.player.isRiding()) 
		{
			mc.player.getRidingEntity().rotationYaw = yaw;
		}
	}
}
