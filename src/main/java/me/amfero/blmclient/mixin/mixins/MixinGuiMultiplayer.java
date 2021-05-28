package me.amfero.blmclient.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;

@Mixin(GuiMultiplayer.class)
public class MixinGuiMultiplayer extends GuiScreen {
	private GuiButton joinButton;
	@Inject(method = "createButtons", at = @At("RETURN"))
	public void createButtons( CallbackInfo info ) {
		joinButton = new GuiButton(777, 10, this.height - 28, 90, 20, "Join morgenpvp");
		this.buttonList.add(joinButton);
	}
	
	@Inject(method = "actionPerformed", at = @At("HEAD"))
	public void actionPerformed(GuiButton button, CallbackInfo info) {
		if(button.id == joinButton.id) {
			ServerData data = new ServerData( "", "morgenpvp.xyz", false );
			net.minecraftforge.fml.client.FMLClientHandler.instance().connectToServer(null, data);
		}
	}
}