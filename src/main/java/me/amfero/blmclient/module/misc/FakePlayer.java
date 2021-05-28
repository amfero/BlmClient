package me.amfero.blmclient.module.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class FakePlayer extends Module 
{	
	private final List<Integer> fakePlayerIdList = new ArrayList<>();
	
	public FakePlayer()
	{
        super("FakePlayer", "", Category.MISC);
	}
     
	public void onEnable() 
	{
		if(nullCheck()) this.disable();
		GameProfile profile = new GameProfile(UUID.fromString("b4e468dc-f67f-494c-8214-23e248cf1706"), "AmferohackMP4");
		EntityOtherPlayerMP fakePlayer = new EntityOtherPlayerMP(mc.world, profile);
		fakePlayer.copyLocationAndAnglesFrom(mc.player);
		fakePlayer.setHealth(mc.player.getHealth() + mc.player.getAbsorptionAmount());
		mc.world.addEntityToWorld(-69, fakePlayer);
		fakePlayerIdList.add(-69);
	}
	
	@SubscribeEvent
	public void onUpdate(final TickEvent.ClientTickEvent event) 
	{
		if(nullCheck()) this.disable();
	}
	
	public void onDisable() 
	{
		if(nullCheck()) return;
		for (int id : fakePlayerIdList)
		{
			mc.world.removeEntityFromWorld(id);
		}
	}
}
