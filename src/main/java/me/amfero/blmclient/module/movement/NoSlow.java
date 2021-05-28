package me.amfero.blmclient.module.movement;

import me.amfero.blmclient.event.events.PacketSendEvent;
import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import me.amfero.blmclient.setting.Setting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoSlow extends Module
{
	Setting strict = new Setting("Strict", this, true);
	Setting sneakPacket = new Setting("SneakPacket", this, false);
	
	private boolean sneaking = false;
	
	public NoSlow() {
		super("NoSlow", "", Category.MOVEMENT);
		addSetting(sneakPacket);
		addSetting(strict);
	}

	@SubscribeEvent
    public void onUseItem(PlayerInteractEvent.RightClickItem event) 
	{
		if(nullCheck()) return;
        Item item = mc.player.getHeldItem(event.getHand()).getItem();
        if ((item instanceof ItemFood || item instanceof ItemBow || item instanceof ItemPotion && this.sneakPacket.getBooleanValue()) && !this.sneaking) 
        {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            this.sneaking = true;
        }
    }
	
	@SubscribeEvent
    public void onInput(InputUpdateEvent event) {
		if(nullCheck()) return;
        if (mc.player.isHandActive() && !mc.player.isRiding()) 
        {
            event.getMovementInput().moveStrafe *= 5.0f;
            event.getMovementInput().moveForward *= 5.0f;
        }
    }
	
	@SubscribeEvent
    public void onPacket(PacketSendEvent event) 
	{
		if(nullCheck()) return;
        if (event.getPacket() instanceof CPacketPlayer && this.strict.getBooleanValue() && mc.player.isHandActive() && !mc.player.isRiding()) 
        {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ)), EnumFacing.DOWN));
        }
    }
}
