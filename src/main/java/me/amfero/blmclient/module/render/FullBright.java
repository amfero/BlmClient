package me.amfero.blmclient.module.render;

import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class FullBright extends Module
{
	public FullBright() 
	{
		super("FullBright", "", Category.RENDER);
	}

	@SubscribeEvent
	public void onUpdate(final TickEvent.ClientTickEvent event) 
	{
		if(nullCheck()) return;
        mc.gameSettings.gammaSetting = 666f;
	}
}
