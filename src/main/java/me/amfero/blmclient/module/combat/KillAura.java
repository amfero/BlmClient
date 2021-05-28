package me.amfero.blmclient.module.combat;

import me.amfero.blmclient.event.events.PacketSendEvent;
import me.amfero.blmclient.mixin.accessor.ICPacketPlayer;
import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import me.amfero.blmclient.setting.Setting;
import me.amfero.blmclient.util.MathUtil;

import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class KillAura extends Module
{
	Setting range = new Setting("Range", this, 40, 10, 80);
	Setting rotation = new Setting("Rotate", this, true);
	
	float[] angle;

	public KillAura() 
	{
		super("KillAura", "", Category.COMBAT);
		addSetting(rotation);
		addSetting(range);
	}
	
	@SubscribeEvent
	public void onUpdate(final TickEvent.ClientTickEvent event) 
	{
		if (!nullCheck()) 
		{
            for (EntityPlayer player : mc.world.playerEntities) 
            {
                if (player != mc.player)
                {
                    if (mc.player.getDistance(player) < (double)(range.getIntegerValue() / 10)) 
                    {
                    	if (rotation.getBooleanValue()) angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), player.getPositionVector());
                    	attackPlayer(player);
                    } 
                    else
                    {
                    	angle = null;
                    }
                }
            }
        }
	}
	
	@SubscribeEvent
	public void onPacket(PacketSendEvent event)
	{
		if(!rotation.getBooleanValue() || nullCheck()) return;
		Object p;
        if (event.getPacket() instanceof CPacketPlayer && angle != null && angle.length != 0) /*&& this.rotate_mode.getValue().equalsIgnoreCase("Old") || rotate_mode.getValue().equalsIgnoreCase("Strict"))*/ {
            p = (CPacketPlayer)((Object)event.getPacket());
            ((ICPacketPlayer)p).setYaw((float)angle[0]);
            ((ICPacketPlayer)p).setPitch((float)angle[1]);
        }
	}
	
	public void attackPlayer(EntityPlayer player)
	{
		if (player != null) 
		{
			if (player != mc.player) {
				if (mc.player.getCooledAttackStrength(0.0f) >= 1)
				{
					mc.playerController.attackEntity(mc.player, player);
					mc.player.swingArm(EnumHand.MAIN_HAND);
				}
			}
		}
	}
}

