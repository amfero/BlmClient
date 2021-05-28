package me.amfero.blmclient.module.misc;

import me.amfero.blmclient.mixin.accessor.IMinecraft;
import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class FastXp extends Module 
{
	public FastXp() {
		super("FastXp", "", Category.MISC);
	}
	
	@SubscribeEvent
	public void onUpdate(TickEvent.ClientTickEvent event) 
	{
		if(nullCheck()) return;
		if(mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE || mc.player.getHeldItemOffhand().getItem() == Items.EXPERIENCE_BOTTLE) 
		{
			((IMinecraft) mc).setRightClickDelayTimer(0);
		}
	}
}