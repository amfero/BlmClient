package me.amfero.blmclient;

import java.awt.Font;
import org.lwjgl.opengl.Display;
import me.amfero.blmclient.event.EventHandler;
import me.amfero.blmclient.gui.clickgui.ClickGUI;
import me.amfero.blmclient.module.ModuleManager;
import me.amfero.blmclient.setting.SettingManager;
import me.amfero.blmclient.util.Config;
import me.amfero.blmclient.util.font.CustomFontRenderer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = Client.MODID, name = Client.NAME, version = Client.VERSION)
public class Client 
{
	public static final String MODID = "blmclient";
    public static final String NAME = "Blm Client";
    public static final String VERSION = "0.4.1";
    public static SettingManager settingManager;
    public static ModuleManager moduleManager;
    public static ClickGUI clickGUI;
    public static CustomFontRenderer customFontRenderer;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
    	Display.setTitle(NAME + " " + VERSION);
    	settingManager = new SettingManager();
    	moduleManager = new ModuleManager();
    	clickGUI = new ClickGUI();
    	Config.loadConfig();
		Runtime.getRuntime().addShutdownHook(new Config());
    	customFontRenderer = new CustomFontRenderer(new Font("Arial", Font.PLAIN, 19), true, false);
    	MinecraftForge.EVENT_BUS.register(new EventHandler());
    }
}