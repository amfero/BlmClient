package me.amfero.blmclient.module.combat;

import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import me.amfero.blmclient.setting.Setting;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoLog extends Module 
{
	Setting health = new Setting("Health", this, 8, 0, 36);
	Setting autoToggle = new Setting("AutoToggle", this, true);
	
	public AutoLog() 
	{
		super("AutoLog", "", Category.COMBAT);
		addSetting(health);
		addSetting(autoToggle);
	}

	@SubscribeEvent
	public void onUpdate(TickEvent.ClientTickEvent event) {
		if (nullCheck()) return;	
		if (mc.player.getHealth() < health.getIntegerValue()) 
		{
			if(mc.player.isDead) return;
			mc.world.sendQuittingDisconnectingPacket();
			mc.displayGuiScreen(new GuiMainMenu());
			if (autoToggle.getBooleanValue()) this.disable();
		}
	}

}
