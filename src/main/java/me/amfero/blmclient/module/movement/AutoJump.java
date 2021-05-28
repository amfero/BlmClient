package me.amfero.blmclient.module.movement;

import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoJump extends Module
{
	public AutoJump()
	{
		super("AutoJump", "", Category.MOVEMENT);
	}
	
	@SubscribeEvent
	public void onInputUpdate(InputUpdateEvent event) {
		if(nullCheck()) return;
		event.getMovementInput().jump = true;
	}
}
