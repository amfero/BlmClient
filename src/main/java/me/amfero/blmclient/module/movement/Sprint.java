package me.amfero.blmclient.module.movement;

import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Sprint extends Module
{
	public Sprint()
	{
		super("Sprint", "", Category.MOVEMENT);
	}

	@SubscribeEvent
	public void onUpdate(TickEvent.ClientTickEvent event)
	{
		if(nullCheck()) return;
    	if (!mc.player.isSprinting())
    	{
    		mc.player.setSprinting(true);
    	}
	}
	
	public void onDisable()
	{
		mc.player.setSprinting(false);
	}
}