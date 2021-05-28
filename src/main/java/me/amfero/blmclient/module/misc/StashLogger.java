package me.amfero.blmclient.module.misc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.amfero.blmclient.event.events.PacketReceiveEvent;
import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import me.amfero.blmclient.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class StashLogger extends Module
{
	private final Setting chestsToImportantNotify = new Setting("Chestsõó", this, 15, 0, 30);
	Setting chests = new Setting("Chests", this, true);
	Setting Shulkers = new Setting("Shulkers", this, true);
	Setting donkeys = new Setting("Donkeys", this, false);
	Setting writeToFile = new Setting("WriteToFile", this, true);
	
	File mainFolder = new File(Minecraft.getMinecraft().gameDir + File.separator + "blmclient");
	SPacketChunkData l_Packet;
    int l_ChestsCount;
    int shulkers;
    final Iterator<NBTTagCompound> iterator;
    NBTTagCompound l_Tag;
    String l_Id;
    SimpleDateFormat formatter;
    Date date;
	
	public StashLogger() 
	{
		super("StashLogger", "", Category.MISC);
		addSetting(chests);
		addSetting(Shulkers);
		addSetting(writeToFile);
		addSetting(chestsToImportantNotify);
		this.iterator = null;
	}
	
	@SubscribeEvent
	public void onPacket(PacketReceiveEvent event) 
	{
		if(nullCheck()) return;
		if (event.getPacket() instanceof SPacketChunkData) 
		{
			final SPacketChunkData l_Packet = (SPacketChunkData) event.getPacket();
            int l_ChestsCount = 0;
            int shulkers = 0;
            for (NBTTagCompound l_Tag : l_Packet.getTileEntityTags())
            {
                String l_Id = l_Tag.getString("id");
                if (l_Id.equals("minecraft:chest") && chests.getBooleanValue())
                    ++l_ChestsCount;
                else if (l_Id.equals("minecraft:shulker_box") && Shulkers.getBooleanValue())
                    ++shulkers;
            }
            if (l_ChestsCount >= chestsToImportantNotify.getIntegerValue())
                SendMessage(String.format("%s chests located at X: %s, Z: %s", l_ChestsCount, l_Packet.getChunkX()*16, l_Packet.getChunkZ()*16), true);
            if (shulkers > 0)
                SendMessage(String.format("%s shulker boxes at X: %s, Z: %s", shulkers, l_Packet.getChunkX()*16, l_Packet.getChunkZ()*16), true);
		}
	}
	
	private void SendMessage(final String message, final boolean save) 
	{
		String server = Minecraft.getMinecraft().isSingleplayer() ? "singleplayer".toUpperCase() : mc.getCurrentServerData().serverIP;
        if (writeToFile.getBooleanValue() && save) 
        {
            try
            {
                final FileWriter writer = new FileWriter(mainFolder + "/stashes.txt", true);
                writer.write("[" + server + "]: " + message + "\n");
                writer.close();
            }
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
        mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f));
        mc.player.sendMessage(new TextComponentString(ChatFormatting.DARK_GRAY + "[BlmClient] " + ChatFormatting.GOLD + message));
    }		
}
