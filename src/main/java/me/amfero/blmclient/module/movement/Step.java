package me.amfero.blmclient.module.movement;

import me.amfero.blmclient.event.events.MoveEvent;
import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import me.amfero.blmclient.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Step extends Module
{
	Setting stepHeight = new Setting("Height", this, 2, 1, 4);
	
	public Step()
	{
		super("Step", "", Category.MOVEMENT);
		addSetting(stepHeight);
	}
	
	@SubscribeEvent
    public void onMove(MoveEvent event)
	{
		if(nullCheck()) return;
		mc.player.stepHeight = stepHeight.getIntegerValue();
    }
	
	public void onDisable() {
        mc.player.stepHeight = 0.5f;
    }
}
