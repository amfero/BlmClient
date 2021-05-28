package me.amfero.blmclient.module.movement;

import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoWalk extends Module
{
	public AutoWalk()
	{
		super("AutoWalk", "", Category.MOVEMENT);
	}
	
	@SubscribeEvent
	public void onInputUpdate(InputUpdateEvent event) {
		if(nullCheck()) return;
		event.getMovementInput().moveForward = 1.0f;
	}
}
