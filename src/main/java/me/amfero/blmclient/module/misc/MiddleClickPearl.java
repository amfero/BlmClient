package me.amfero.blmclient.module.misc;

import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import com.mojang.realmsclient.gui.ChatFormatting;

import org.lwjgl.input.Mouse;

public class MiddleClickPearl extends Module 
{
	private boolean clicked;

	public MiddleClickPearl()
	{
		super("MiddleClickPearl", "", Category.MISC);
	}

	@SubscribeEvent
    public void onUpdate(final TickEvent.ClientTickEvent event) 
	{
		if(nullCheck()) return;
        if (mc.currentScreen == null) 
        {
            if (Mouse.isButtonDown(2)) 
            {
                if (!this.clicked) 
                {
                    final RayTraceResult result = mc.objectMouseOver;
                    if (result != null && result.typeOfHit == RayTraceResult.Type.MISS) 
                    {
                        final int pearlSlot = findPearlInHotbar(mc);
                        if (pearlSlot != -1) {
                            final int oldSlot = mc.player.inventory.currentItem;
                            mc.player.inventory.currentItem = pearlSlot;
                            mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
                            mc.player.inventory.currentItem = oldSlot;
                        }
                        else
                        {
                        	Minecraft.getMinecraft().player.sendMessage(new TextComponentString(ChatFormatting.GOLD +  "Can't find pearl in hotbar!"));
                        }
                    }
                }
                this.clicked = true;
            } 
            else
            {
                this.clicked = false;
            }
        }
	}
	
	private boolean isItemStackPearl(final ItemStack itemStack) 
	{
        return itemStack.getItem() instanceof ItemEnderPearl;
    }

    private int findPearlInHotbar(final Minecraft mc) 
    {
        for (int index = 0; InventoryPlayer.isHotbar(index); index++)
        {
            if (isItemStackPearl(mc.player.inventory.getStackInSlot(index))) return index;
        }
        return -1;
    }
	
}
