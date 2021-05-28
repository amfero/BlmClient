package me.amfero.blmclient.module.combat;

import me.amfero.blmclient.Client;
import me.amfero.blmclient.event.events.PacketSendEvent;
import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import me.amfero.blmclient.setting.Setting;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class RightClickGap extends Module
{
	Setting swordOrAxeOnly = new Setting("swordOrAxeOnly", this, true);
	Setting disableHealth = new Setting("DisableHealth", this, 6, 0, 20);
	
	int gaps = -1;
	boolean autoTotemWasEnabled = false;
	boolean cancelled = false;
	boolean isGuiOpened = false;
	Item usedItem;
	
	public RightClickGap()
	{
		super("RightClickGap", "", Category.COMBAT);
		addSetting(swordOrAxeOnly);
		addSetting(disableHealth);
	}
	
	@SubscribeEvent
	public void onPacket(PacketSendEvent event) 
	{
		if (event.getPacket() instanceof CPacketPlayerTryUseItem) 
		{
			if (cancelled) 
			{
				disableGaps();
				return;
			}
			if (mc.player.getHeldItemMainhand().getItem() instanceof ItemSword || mc.player.getHeldItemMainhand().getItem() instanceof ItemAxe || passItemCheck()) 
			{
				if (Client.moduleManager.getModule("AutoTotem").isEnabled()) 
				{
					autoTotemWasEnabled = true;
					Client.moduleManager.getModule("AutoTotem").disable();;
				}
				enableGaps(gaps);
			}
		}
		try 
		{
			if (!mc.gameSettings.keyBindUseItem.isKeyDown() && mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE) disableGaps();
			else if (mc.player.getHealth() + mc.player.getAbsorptionAmount() <= disableHealth.getIntegerValue())
			{
				disableGaps();
			}
		} catch (NullPointerException ignored) { }
	}
	
	@SubscribeEvent
	public void onUpdate(final TickEvent.ClientTickEvent event) 
	{
		if(nullCheck()) return;
		cancelled = mc.player.getHealth() + mc.player.getAbsorptionAmount() <= disableHealth.getIntegerValue();
		if (cancelled) 
		{ 
			disableGaps(); return; 
		}
		if (mc.player.getHeldItemOffhand().getItem() != Items.GOLDEN_APPLE) 
		{
			for (int i = 0; i < 45; i++) {
				if (mc.player.inventory.getStackInSlot(i).getItem() == Items.GOLDEN_APPLE)
				{
					gaps = i;
					break;
				}
			}
		}
	}
	
	private boolean passItemCheck() 
	{
		if (swordOrAxeOnly.getBooleanValue()) return false;
		else 
		{
			Item item = mc.player.getHeldItemMainhand().getItem();
			if (item instanceof ItemBow) return false;
			if (item instanceof ItemSnowball) return false;
			if (item instanceof ItemEgg) return false;
			if (item instanceof ItemPotion) return false;
			if (item instanceof ItemEnderEye) return false;
			if (item instanceof ItemEnderPearl) return false;
			if (item instanceof ItemFood) return false;
			if (item instanceof ItemShield) return false;
			if (item instanceof ItemFlintAndSteel) return false;
			if (item instanceof ItemFishingRod) return false;
			if (item instanceof ItemArmor) return false;
			if (item instanceof ItemExpBottle) return false;
		}
		return true;
	}
	
	private void disableGaps() 
	{
		if (autoTotemWasEnabled != Client.moduleManager.getModule("AutoTotem").isEnabled())
		{
			moveGapsWaitForNoGui();
			Client.moduleManager.getModule("AutoTotem").enable();
			autoTotemWasEnabled = false;
		}
	}

	private void enableGaps(int slot) 
	{
		if (mc.player.getHeldItemOffhand().getItem() != Items.GOLDEN_APPLE) 
		{
			mc.playerController.windowClick(0, slot < 9 ? slot + 36 : slot, 0, ClickType.PICKUP, mc.player);
			mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
		}
	}

	private void moveGapsToInventory(int slot) {
		if (mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE) 
		{
			mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
			mc.playerController.windowClick(0, slot < 9 ? slot + 36 : slot, 0, ClickType.PICKUP, mc.player);
		}
	}

	private void moveGapsWaitForNoGui()
	{
		if (isGuiOpened) return;
		moveGapsToInventory(gaps);
	}
}
