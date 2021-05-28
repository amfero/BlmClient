package me.amfero.blmclient.module.combat;

import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import me.amfero.blmclient.setting.Setting;
import me.amfero.blmclient.util.Timer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoArmor extends Module
{
	Setting delay = new Setting("Delay", this, 50, 3, 1000);
	
	private final Timer timer = new Timer();
	
	public AutoArmor() 
	{
		super("AutoArmor", "", Category.COMBAT);
		addSetting(delay);
	}

	@SubscribeEvent
	public void onUpdate(final TickEvent.ClientTickEvent event)
	{
		if (nullCheck() || mc.currentScreen instanceof GuiInventory) return;
        final ItemStack helm = mc.player.inventoryContainer.getSlot(5).getStack();
        if (helm.getItem() == Items.AIR)
        {
            final int slot = this.findArmorSlot(EntityEquipmentSlot.HEAD);
            if (slot != -1) 
            {
                this.clickSlot(slot);
            }
        }
        final ItemStack chest = mc.player.inventoryContainer.getSlot(6).getStack();
        if (chest.getItem() == Items.AIR) 
        {
            final int slot = this.findArmorSlot(EntityEquipmentSlot.CHEST);
            if (slot != -1)
            {
                this.clickSlot(slot);
            }
        }

        final ItemStack legging = mc.player.inventoryContainer.getSlot(7).getStack();
        if (legging.getItem() == Items.AIR) 
        {
            final int slot = this.findArmorSlot(EntityEquipmentSlot.LEGS);
            if (slot != -1)
            {
                this.clickSlot(slot);
            }
        }
        final ItemStack feet = mc.player.inventoryContainer.getSlot(8).getStack();
        if (feet.getItem() == Items.AIR)
        {
            final int slot = this.findArmorSlot(EntityEquipmentSlot.FEET);
            if (slot != -1) 
            {
                this.clickSlot(slot);
            }
        }
    }


    private void clickSlot(int slot) 
    {
        if (timer.passedMs(delay.getIntegerValue()))
        {
            Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().player.inventoryContainer.windowId, slot, 0, ClickType.QUICK_MOVE, Minecraft.getMinecraft().player);
            this.timer.reset();
        }
    }

    private int findArmorSlot(EntityEquipmentSlot type)
    {
        int slot = -1;
        float damage = 0;
        for (int i = 9; i < 45; i++) 
        {
            final ItemStack s = Minecraft.getMinecraft().player.inventoryContainer.getSlot(i).getStack();
            if (s.getItem() != Items.AIR) 
            {
                if (s.getItem() instanceof ItemArmor) 
                {
                    final ItemArmor armor = (ItemArmor) s.getItem();
                    if (armor.armorType == type) 
                    {
                        final float currentDamage = (armor.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, s));
                        if (currentDamage > damage) 
                        {
                            damage = currentDamage;
                            slot = i;
                        }
                    }
                }
            }
        }
        return slot;
    }
}
