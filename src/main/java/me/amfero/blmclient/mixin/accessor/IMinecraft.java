package me.amfero.blmclient.mixin.accessor;

import net.minecraft.util.Timer;

public interface IMinecraft
{
	Timer getTimer();
	
	void setRightClickDelayTimer(final int p0);
}