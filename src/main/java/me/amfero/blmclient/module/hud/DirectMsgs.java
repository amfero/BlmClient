package me.amfero.blmclient.module.hud;


import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Splitter;
import com.mojang.realmsclient.gui.ChatFormatting;

import me.amfero.blmclient.event.events.PacketReceiveEvent;
import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import me.amfero.blmclient.setting.Setting;
import me.amfero.blmclient.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class DirectMsgs extends Module
{
	private final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
	
	Setting clear = new Setting("ClearOnDisconnect", this, false);
	Setting max = new Setting("MaxMsgs", this, 5, 1, 15);
	Setting X = new Setting("X", this, 1, 1, sr.getScaledWidth() * 2);
	Setting Y = new Setting("Y", this, 1, 1, sr.getScaledHeight() * 2);
	
	public DirectMsgs() 
	{
		super("DirectMsgs", Category.HUD);
		addSetting(clear);
		addSetting(max);
		addSetting(X);
		addSetting(Y);
	}
	
	List<String> msgs = new ArrayList<>();
	
	@SubscribeEvent
	public void onUpdate(TickEvent.ClientTickEvent event)
	{
		if(nullCheck() && clear.getBooleanValue() && msgs.size() > 0) {
			msgs.removeAll(msgs);
			return;
		}
		if(nullCheck() && !clear.getBooleanValue()) return;
	}

    @SubscribeEvent
    public void onPacket(PacketReceiveEvent event)
    {
        if (event.getPacket() instanceof SPacketChat) 
        {
            final SPacketChat packet = (SPacketChat) event.getPacket();
            String text = packet.getChatComponent().getFormattedText();
            if (text.toLowerCase().contains("whispers:") && text.toLowerCase().contains(ChatFormatting.LIGHT_PURPLE + "") || text.toLowerCase().contains(ChatFormatting.LIGHT_PURPLE + "from")) 
            {
                for(String split : Splitter.fixedLength(54).split(text)) msgs.add(split);
            }
        }
    }
	
	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent.Post event)
	{
		int x = X.getIntegerValue();
		int y = Y.getIntegerValue();
		int ey = y + 3;
		if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) 
		{
			for(String msg : msgs)
			{
				if(msgs == null) continue;
				RenderUtil.drawGradientSideways(x, ey - 2, x + mc.fontRenderer.getStringWidth(msg) + 5, ey + 10, new Color(1,1,1,150).getRGB(), new Color(1,1,1,170).getRGB());
				mc.fontRenderer.drawString(ChatFormatting.LIGHT_PURPLE + msg, x + 3, ey, 0xffffff);
				ey += 12;
			}
			if(msgs.size() > max.getIntegerValue()) msgs.remove(0);
		}
	}
}
