package me.amfero.blmclient.module;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.amfero.blmclient.Client;
import me.amfero.blmclient.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;

public class Module
{
	private String name;
	private String description;
	private Category category;
	private int bind;
	private boolean enabled;
	public final Minecraft mc = Minecraft.getMinecraft();

	public Module(String name, Category category)
	{
		this.name = name;
		this.category = category;
	}

	public Module(String name, String description, Category category)
	{
		this.name = name;
		this.description = description;
		this.category = category;
	}


	public void onEnable() {
		//if(Client.moduleManager.getModule("ToggleMsg").isEnabled() && this.name != "ClickGUI") mc.player.sendMessage(new TextComponentString(ChatFormatting.DARK_GRAY + "[BlmClient] " + ChatFormatting.GREEN + ChatFormatting.BOLD + "<" + this.name + "> +"));
	}
	public void onDisable() {
		//if(Client.moduleManager.getModule("ToggleMsg").isEnabled() && this.getName() != "ClickGUI") mc.player.sendMessage(new TextComponentString(ChatFormatting.DARK_GRAY + "[BlmClient] " + ChatFormatting.RED + ChatFormatting.BOLD + "<" + this.getName() + "> -"));
	}

	public void enable()
	{
		setEnabled(true);
		onEnable();
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void disable()
	{
		setEnabled(false);
		onDisable();
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	public void toggle()
	{
		if (isEnabled()) disable();
		else enable();
	}

	public boolean nullCheck()
	{
		return mc.player == null || mc.world == null;
	}

	public void addSetting(Setting setting)
	{
		Client.settingManager.addSetting(setting);
	}

	public void delSetting(Setting setting)
	{
		Client.settingManager.delSetting(setting);
	}

	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Category getCategory()
	{
		return category;
	}

	public void setCategory(Category category)
	{
		this.category = category;
	}

	public int getBind()
	{
		return bind;
	}

	public void setBind(int bind)
	{
		this.bind = bind;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
}
