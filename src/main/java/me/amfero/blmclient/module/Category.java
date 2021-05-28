package me.amfero.blmclient.module;

public enum Category
{
	COMBAT("Combat"),
	EXPLOIT("Exploit"),
	HUD("Hud"),
	RENDER("Render"),
	MOVEMENT("Movement"),
	MISC("Misc");

	private String name;

	Category(String name)
	{
		setName(name);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
