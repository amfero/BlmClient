package me.amfero.blmclient.module.render;

import org.lwjgl.input.Keyboard;

import me.amfero.blmclient.Client;
import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;

public class ClickGUI extends Module
{
	public ClickGUI()
	{
		super("ClickGUI", "", Category.RENDER);
		setBind(Keyboard.KEY_RSHIFT);
	}

	public void onEnable()
	{	
		mc.displayGuiScreen(Client.clickGUI);
	}
}