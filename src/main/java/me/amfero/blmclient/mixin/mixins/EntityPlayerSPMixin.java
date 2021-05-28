package me.amfero.blmclient.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import me.amfero.blmclient.Client;
import me.amfero.blmclient.event.events.MoveEvent;
import me.amfero.blmclient.event.events.WalkEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.MoverType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

@Mixin(value = EntityPlayerSP.class, priority = 634756347)
public class EntityPlayerSPMixin extends AbstractClientPlayer
{
	public EntityPlayerSPMixin(World worldIn, GameProfile playerProfile)
	{
		super(worldIn, playerProfile);
	}

	@Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;move(Lnet/minecraft/entity/MoverType;DDD)V"))
	public void move(final AbstractClientPlayer player, final MoverType moverType, final double x, final double y, final double z)
	{
		MoveEvent event = new MoveEvent(moverType, x, y, z);
		MinecraftForge.EVENT_BUS.post(event);
		if (!event.isCanceled()) super.move(event.getType(), event.getX(), event.getY(), event.getZ());
	}
	
	@Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
	public void onUpdateWalkingPlayer(CallbackInfo ci)
	{
		WalkEvent event = new WalkEvent();
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) ci.cancel();
	}
	
	@Redirect(method = {"onLivingUpdate" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;closeScreen()V"))
	public void closeScreen(final EntityPlayerSP entityPlayerSP) {
		if (Client.moduleManager.getModule("PortalChat").isEnabled()) {
			return;
		}
	}

	@Redirect(method = { "onLivingUpdate" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"))
	public void closeScreen(final Minecraft minecraft, final GuiScreen screen) {
		if (Client.moduleManager.getModule("PortalChat").isEnabled()) {
			return;
		}
	}
}
