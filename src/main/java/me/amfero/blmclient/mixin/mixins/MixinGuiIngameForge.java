package me.amfero.blmclient.mixin.mixins;

import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.amfero.blmclient.Client;

@Mixin(GuiIngameForge.class)
public class MixinGuiIngameForge {

    @Inject(method="renderCrosshairs", at=@At("HEAD"), cancellable = true, remap = false)
    public void renderCrosshairs(CallbackInfo ci)
    {
    	if(Client.moduleManager.getModule("Crosshair").isEnabled()) ci.cancel();
    }
}