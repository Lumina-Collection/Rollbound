package net.luminacollection.rollbound.paper.hooks;

import net.kyori.adventure.audience.Audience;
import net.luminacollection.rollbound.paper.configuration.Settings;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Map.Entry;

public class HooksManager
{
	private static HooksManager instance;
	public static HooksManager instance()
	{
		return instance == null ? instance = new HooksManager() : instance;
	}
	private HooksManager() {}
	
	public Entry<Integer, String> getRangeAndPermission(Player player)
	{
		var hookVentureChat = VentureChat.instance();
		var hookCarbon = Carbon.instance();
		
		Entry<Integer, String> rangeAndPermission = null;
		
		if (hookVentureChat.hookEnabled()) rangeAndPermission = hookVentureChat.getRangeAndPermission(player);
		if (hookCarbon.hookEnabled()) rangeAndPermission = hookCarbon.getRangeAndPermission(player);
		
		if (rangeAndPermission == null) rangeAndPermission = Map.entry(Settings.CHAT_RANGE.get(), "");
		
		return rangeAndPermission;
	}
	
	public Audience partyAudience(Player player)
	{
		var hookCarbon = Carbon.instance();
		if (!hookCarbon.hookEnabled()) return null;
		return hookCarbon.partyAudience(player);
	}
}
