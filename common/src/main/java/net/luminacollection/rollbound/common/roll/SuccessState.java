package net.luminacollection.rollbound.common.roll;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.luminacollection.rollbound.common.i18n.Messages;
import software.axios.api.i18n.MessagesInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public record SuccessState(MessagesInterface message, int percentage, Triggered triggered, Sound sound)
{
	private static final List<SuccessState> successStates = new ArrayList<>();
	
	public enum Triggered {ABOVE, BELOW}
	
	public SuccessState
	{
		if (percentage > 100) percentage = 100;
	}
	
	@SuppressWarnings("unchecked")
	public static void refresh(List<Map<?, ?>> setting)
	{
		successStates.clear();
		for (Map<?, ?> entry : setting)
		{
			var map = ((Map<String, Object>) entry);
			var message = Messages.byPath("success-states.text." + map.get("id"));
			var percentage = Integer.parseInt(map.get("percentage").toString());
			var triggeredString = String.valueOf(map.get("triggered"));
			var triggered = Triggered.BELOW;
			if (triggeredString.equalsIgnoreCase("ABOVE")) triggered = Triggered.ABOVE;
			var sound = Sound.sound(Key.key(String.valueOf(map.get("sound"))), Source.MASTER, 2F, Float.parseFloat(map.get("pitch").toString()));
			
			successStates.add(new SuccessState(message, percentage, triggered, sound));
		}
	}
	
	public static List<SuccessState> getAll()
	{
		return successStates;
	}
}
