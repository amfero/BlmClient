package me.amfero.blmclient.module.misc;

import me.amfero.blmclient.module.Category;
import me.amfero.blmclient.module.Module;
import me.amfero.blmclient.setting.Setting;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;

public class ExtraTab extends Module 
{
	Setting tabSize = new Setting("TabSize", this, 250, 1, 600); 
	
	public ExtraTab() 
	{
		super("ExtraTab", "", Category.MISC);
		addSetting(tabSize);
	}

	public static String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) 
	{
        String dname = networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
        return dname;
    }
}
