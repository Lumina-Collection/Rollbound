package net.luminacollection.rollbound.paper.hooks;

import net.draycia.carbon.api.CarbonChat;
import net.draycia.carbon.api.CarbonChatProvider;
import net.kyori.adventure.audience.Audience;
import net.luminacollection.rollbound.paper.RollboundPlugin;
import net.luminacollection.rollbound.paper.configuration.Settings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Map.Entry;

public class Carbon
{
	private final boolean HOOK_ENABLED;
	
	private static Carbon instance;
	public static Carbon instance()
	{
		return instance == null ? instance = new Carbon() : instance;
	}
	private Carbon() {
		HOOK_ENABLED = isCarbonEnabled();
	}
	
	private boolean isCarbonEnabled()
	{
		var carbon = Bukkit.getPluginManager().getPlugin("CarbonChat");
		var carbonIsLoaded = carbon != null;
		return carbonIsLoaded && carbon.isEnabled() && Settings.HOOKS_CARBON.get();
	}
	
	public boolean hookEnabled()
	{
		return HOOK_ENABLED;
	}
	
	public Entry<Integer, String> getRangeAndPermission(Player player)
	{
		if (!hookEnabled()) return null;
		RollboundPlugin.instance().debug("Carbon hook enabled, getting range and permission...");
		CarbonChat carbonChat = CarbonChatProvider.carbonChat();
		try
		{
			var user = carbonChat.userManager().user(player.getUniqueId()).get();
			var channel = user.selectedChannel();
			if (channel == null) return null;
			var range = Math.round(channel.radius());
			var permission = channel.permission();
			permission = permission != null ? permission + ".see" : "";
			RollboundPlugin.instance().debug("Carbon range: " + range);
			RollboundPlugin.instance().debug("Carbon permission: " + permission);
			return Map.entry(Math.toIntExact(range), permission);
		}
		catch (Exception e)
		{
			RollboundPlugin.instance().debug("Carbon hook failed to get range and permission: " + e.getMessage());
			return null;
		}
	}
	
	public Audience partyAudience(Player player)
	{
		if (!hookEnabled()) return null;
		RollboundPlugin.instance().debug("Carbon hook enabled, getting party audience...");
		CarbonChat carbonChat = CarbonChatProvider.carbonChat();
		try
		{
			var user = carbonChat.userManager().user(player.getUniqueId()).get();
			var party = user.party().join();
			if (party == null) return null;
			var members = party.members();
			return Audience.audience(members.stream().map(Bukkit::getPlayer).toList());
		}
		catch (Exception e)
		{
			RollboundPlugin.instance().debug("Carbon hook failed to get party audience: " + e.getMessage());
			return null;
		}
	}
}
