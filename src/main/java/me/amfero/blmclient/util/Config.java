package me.amfero.blmclient.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import me.amfero.blmclient.Client;
import me.amfero.blmclient.module.Module;
import me.amfero.blmclient.setting.Setting;
import net.minecraft.client.Minecraft;

public class Config extends Thread 
{
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final File mainFolder = new File(mc.gameDir + File.separator + "blmclient");
    private static final String ENABLED_MODULES = "EnabledModules.txt";
    private static final String SETTINGS = "Settings.txt";
    private static final String BINDS = "Binds.txt";

    @Override
    public void run() 
    {
        if (!mainFolder.exists() && !mainFolder.mkdirs()) System.out.println("Failed to create config folder");

        try { FileUtil.saveFile(new File(mainFolder.getAbsolutePath(), ENABLED_MODULES), Client.moduleManager.getEnabledModules().stream().map(Module::getName).collect(Collectors.toCollection(ArrayList::new))); }
        catch (IOException e) { e.printStackTrace(); }

        try { FileUtil.saveFile(new File(mainFolder.getAbsolutePath(), SETTINGS), getSettings()); }
        catch (IOException e) { e.printStackTrace(); }

        try { FileUtil.saveFile(new File(mainFolder.getAbsolutePath(), BINDS), Client.moduleManager.getModules().stream().map(module -> module.getName() + ":" + module.getBind()).collect(Collectors.toCollection(ArrayList::new))); }
        catch(IOException e) { e.printStackTrace(); }
    }

    public static void loadConfig() 
    {	
        if (!mainFolder.exists()) return;

        try 
        {
        	for (String s : FileUtil.loadFile(new File(mainFolder.getAbsolutePath(), ENABLED_MODULES))) 
        	{
                try 
                {
                    Client.moduleManager.getModule(s).enable();
                }
                catch (Exception e) 
                {
                    e.printStackTrace();
                }
            }

            for (String s : FileUtil.loadFile(new File(mainFolder.getAbsolutePath(), SETTINGS))) 
            {
                try 
                {
                    String[] split = s.split(":");
                    saveSetting(Client.settingManager.getSetting(split[1], split[0]), split[2]);
                }
                catch (Exception e) 
                {
                    e.printStackTrace();
                }
            }

            for (String s : FileUtil.loadFile(new File(mainFolder.getAbsolutePath(), BINDS))) 
            {
                try 
                {
                    String[] split = s.split(":");
                    Client.moduleManager.getModule(split[0]).setBind(Integer.parseInt(split[1]));
                }
                catch (Exception e) 
                {
                    e.printStackTrace();
                }
            }
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    private static ArrayList<String> getSettings() 
    {
        ArrayList<String> content = new ArrayList<>();
        
        for (Setting setting : Client.settingManager.getSettings()) 
        {
            switch (setting.getType()) 
            {
                case BOOLEAN:
                    content.add(String.format("%s:%s:%s", setting.getName(), setting.getModule().getName(), setting.getBooleanValue()));
                    break;
                case INTEGER:
                    content.add(String.format("%s:%s:%s", setting.getName(), setting.getModule().getName(), setting.getIntegerValue()));
                    break;
                case ENUM:
                    content.add(String.format("%s:%s:%s", setting.getName(), setting.getModule().getName(), setting.getEnumValue()));
                    break;
                default:
                    break;
            }
        }

        return content;
    }

    private static void saveSetting(Setting setting, String value) 
    {
        switch (setting.getType()) 
        {
            case BOOLEAN:
                setting.setBooleanValue(Boolean.parseBoolean(value));
                break;
            case INTEGER:
                setting.setIntegerValue(Integer.parseInt(value));
                break;
            case ENUM:
                setting.setEnumValue(value);
                break;
            default:
                break;
        }
    }
}
