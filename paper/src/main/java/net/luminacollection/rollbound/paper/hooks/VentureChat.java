package net.luminacollection.rollbound.paper.hooks;

import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import net.luminacollection.rollbound.paper.configuration.Settings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Map.Entry;

public class VentureChat
{
	private final boolean HOOK_ENABLED;
	private static VentureChat instance;
	public static VentureChat instance()
	{
		if (instance == null) instance = new VentureChat();
		return instance;
	}
	private VentureChat() {
		HOOK_ENABLED = isVentureChatEnabled();
	}
	
	private boolean isVentureChatEnabled()
	{
		var ventureChat = Bukkit.getPluginManager().getPlugin("VentureChat");
		var ventureChatIsLoaded = ventureChat != null;
		return ventureChatIsLoaded && ventureChat.isEnabled() && Settings.HOOKS_VENTURECHAT.get();
	}
	
	public boolean hookEnabled()
	{
		return HOOK_ENABLED;
	}
	
	public Entry<Integer, String> getRangeAndPermission(Player player)
	{
		if (!hookEnabled()) return Map.entry(Settings.CHAT_RANGE.get(), "");
		var chatPlayer = MineverseChatAPI.getOnlineMineverseChatPlayer(player);
		var channel = chatPlayer.getCurrentChannel();
		var range = channel.getDistance().intValue();
		var permission = channel.hasPermission() ? channel.getPermission() : "";
		return Map.entry(range, permission);
	}
}
