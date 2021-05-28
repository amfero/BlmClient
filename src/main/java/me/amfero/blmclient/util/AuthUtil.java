package me.amfero.blmclient.util;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.lang.reflect.Field;
import java.net.Proxy;

public final class AuthUtil {
    
    public static Session AuthUtil(String username, String password, Proxy proxy) throws AuthenticationException
	{
		YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(proxy, "");
		YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service
				.createUserAuthentication(Agent.MINECRAFT);

		auth.setUsername(username);
		auth.setPassword(password);

		auth.logIn();
		return new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
	}

	public static String loginPassword(String email, String password)
	{
		try
		{
			Session session = AuthUtil(email, password, Proxy.NO_PROXY);
			Field field = Minecraft.class.getDeclaredField("session");
			field.setAccessible(true);
			field.set(Minecraft.getMinecraft(), session);

			return "Success";
		}
		catch (Exception ignored)
		{
			return "Error";
		}
	}

	public static Session getSession()
	{
		return Minecraft.getMinecraft().getSession();
	}
}
