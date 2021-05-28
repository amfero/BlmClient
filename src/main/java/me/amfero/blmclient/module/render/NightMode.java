package me.amfero.blmclient.module.render;

import me.amfero.blmclient.event.events.PacketReceiveEvent;
import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import me.amfero.blmclient.setting.Setting;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class NightMode extends Module 
{
	Setting time = new Setting("Time", this, 18000, 0, 23000);
	
	public NightMode() 
	{
		super("NightMode", "", Category.RENDER);
		addSetting(time);
	}
	
	@SubscribeEvent
    public void onUpdate(final TickEvent.ClientTickEvent event) 
	{
		if(nullCheck()) return;
        mc.world.setWorldTime(time.getIntegerValue());
	}
	
	@SubscribeEvent
    public void onPacket(final PacketReceiveEvent event) 
	{
		if(event.getPacket() instanceof SPacketTimeUpdate) 
		{
			event.setCanceled(true);
		}
	}
}