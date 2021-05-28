package me.amfero.blmclient.mixin.mixins;

import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import me.amfero.blmclient.mixin.accessor.ICPacketUseEntity;

@Mixin({CPacketUseEntity.class})
public abstract class MixinCPacketUseEntity implements ICPacketUseEntity {
    @Shadow
    protected Action action;
    @Shadow
    protected int entityId;

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
 