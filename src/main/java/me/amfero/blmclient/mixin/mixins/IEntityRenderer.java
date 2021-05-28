package me.amfero.blmclient.mixin.mixins;

import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={EntityRenderer.class})
public interface IEntityRenderer {
    @Accessor(value="drawBlockOutline")
    public void setDrawBlockOutline(boolean var1);

    @Invoker(value="orientCamera")
    public void invokeOrientCamera(float var1);
}