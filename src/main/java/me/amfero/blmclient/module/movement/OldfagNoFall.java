package me.amfero.blmclient.module.movement;

import java.util.Arrays;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import me.amfero.blmclient.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class OldfagNoFall extends Module
{
	Setting mode = new Setting("Mode", this, Arrays.asList("Predict", "Old"));
	Setting disconnect = new Setting("Disconnect", this, false);
	Setting fallDist = new Setting("FallDistance", this, 4, 3, 20);
	
	BlockPos n1;
	
	public OldfagNoFall() 
	{
		super("OldfagNoFall", "", Category.MOVEMENT);
		addSetting(mode);
		addSetting(disconnect);
		addSetting(fallDist);
	}
	
	@SubscribeEvent
	public void onUpdate(TickEvent.ClientTickEvent event) 
	{
		if(nullCheck()) return;
		if(mode.getEnumValue().equals("Predict")) 
		{
			if(mc.player.fallDistance > fallDist.getIntegerValue() && predict(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ))) 
			{
				mc.player.motionY = 0;
				mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, n1.getY(), mc.player.posZ, false));
				mc.player.fallDistance = 0;
				if(disconnect.getBooleanValue()) mc.player.connection.getNetworkManager().closeChannel(new TextComponentString(ChatFormatting.GOLD + "NoFall"));
			}
		}
		if(mode.getEnumValue().equals("Old")) 
		{
			if(mc.player.fallDistance > fallDist.getIntegerValue()) 
			{
				mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, 0, mc.player.posZ, false));
				mc.player.fallDistance = 0;
			}
		}
	}
	
	private boolean predict(BlockPos blockPos)
	{
		Minecraft mc = Minecraft.getMinecraft();
        n1 = blockPos.add(0, -fallDist.getIntegerValue(), 0);
        if (mc.world.getBlockState(n1).getBlock() != Blocks.AIR)
        {
        	return true;
        }
        return false;
	}
}