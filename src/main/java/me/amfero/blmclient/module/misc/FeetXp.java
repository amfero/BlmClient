package me.amfero.blmclient.module.misc;

import me.amfero.blmclient.event.events.PacketSendEvent;
import me.amfero.blmclient.mixin.accessor.ICPacketPlayer;
import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;

import net.minecraft.item.ItemExpBottle;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FeetXp extends Module 
{
	public FeetXp() 
	{
		super("FeetXp", "", Category.MISC);
	}

	@SubscribeEvent
	public void onPacket(final PacketSendEvent event)
	{
		if(nullCheck()) return;
		if (event.getPacket() instanceof CPacketPlayer && mc.player.getHeldItemMainhand().getItem() instanceof ItemExpBottle) 
		{
			CPacketPlayer packet = (CPacketPlayer) event.getPacket();
			((ICPacketPlayer) packet).setPitch(90.0F);
		}
	}
	
}
