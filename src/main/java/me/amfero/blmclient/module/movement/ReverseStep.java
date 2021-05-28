package me.amfero.blmclient.module.movement;

import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ReverseStep extends Module
{
	public ReverseStep() 
	{
		super("ReverseStep", "", Category.MOVEMENT);
	}

	@SubscribeEvent
	public void onUpdate(TickEvent.ClientTickEvent event) 
	{
		if(nullCheck() || mc.player.isDead || mc.player.collidedHorizontally || !mc.player.onGround || mc.player.isOnLadder() || mc.player.isInWater() || mc.player.isInLava() || mc.player.noClip) return;
		if (mc.player.onGround) 
		{
			mc.player.motionY -= 1.0;
        }
	}
}
