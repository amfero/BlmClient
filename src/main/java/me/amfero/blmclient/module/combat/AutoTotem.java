package me.amfero.blmclient.module.combat;

import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import me.amfero.blmclient.setting.Setting;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoTotem extends Module
{
	Setting soft = new Setting("Soft", this, false);
	private int totems;
    boolean moving = false;
    boolean returnI = false;
    
	public AutoTotem()
	{
		super("AutoTotem", "", Category.COMBAT);
		addSetting(soft);
	}
	
	@SubscribeEvent
	public void onUpdate(final TickEvent.ClientTickEvent event)
	{
		if (nullCheck() || mc.currentScreen instanceof GuiContainer) return;
        if (returnI)
        {
            int t = -1;
            for (int i = 0; i < 45; i++)
            {
                if (mc.player.inventory.getStackInSlot(i).isEmpty()) 
                {
                    t = i;
                    break;
                }
            }
            if (t == -1) 
            {
                return;
            }
            mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
            returnI = false;
        }
        totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING)
        {
            totems++;
        }
        else 
        {
            if (soft.getBooleanValue() && !mc.player.getHeldItemOffhand().isEmpty()) 
            {
                return;
            }
            if (moving)
            {
                mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
                moving = false;
                if (!mc.player.inventory.getItemStack().isEmpty()) 
                {
                    returnI = true;
                }
                return;
            }
            if (mc.player.inventory.getItemStack().isEmpty())
            {
                if (totems == 0)
                {
                    return;
                }
                int t = -1;
                for (int i = 0; i < 45; i++) 
                {
                    if (mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) 
                    {
                        t = i;
                        break;
                    }
                }
                if (t == -1) 
                {
                    return;
                }
                mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
                moving = true;
            }
            else if (!soft.getBooleanValue())
            {
                int t = -1;
                for (int i = 0; i < 45; i++) 
                {
                    if (mc.player.inventory.getStackInSlot(i).isEmpty())
                    {
                        t = i;
                        break;
                    }
                }
                if (t == -1) 
                {
                    return;
                }
                mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
            }
        }
    }
}
