package net.luminacollection.rollbound.paper.configuration;

import net.luminacollection.rollbound.paper.RollboundPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import software.axios.api.Axios;
import software.axios.api.configuration.AxiosSettings;
import software.axios.api.configuration.SettingsField;
import software.axios.api.configuration.SettingsInterface;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class Settings<T> implements SettingsInterface
{
	private static final List<Map<?, ?>> successStatesMap = List.of(
		Map.of(
			"id", "critical-success",
			"percentage", 10,
			"triggered", "below",
			"sound", "ui.toast.challenge_complete",
			"pitch", 1.4F
		),
		Map.of(
			"id", "critical-failure",
			"percentage", -98,
			"triggered", "above",
			"sound", "item.goat_horn.sound.5",
			"pitch", 1.4F
		));
	@SettingsField
	public static Settings<Boolean> HOOKS_VENTURECHAT = new Settings<>("hooks.venturechat", Boolean.class, false);
	@SettingsField
	public static Settings<Integer> CHAT_RANGE = new Settings<>("defaults.chat-range", Integer.class, 30);
	@SettingsField
	public static Settings<String> TRIGGERED = new Settings<>("defaults.triggered", String.class, "below");
	@SettingsField
	public static Settings<Integer> NUMBER_OF_FACES = new Settings<>("defaults.number-of-faces", Integer.class, 100);
	@SettingsField
	public static Settings<List<Map<?, ?>>> SUCCESS_STATES = new Settings<>("defaults.success-states", (Class<List<Map<?,?>>>) (Class<?>) List.class, successStatesMap);
	
	private final RollboundPlugin plugin = RollboundPlugin.instance();
	private final Axios axios = plugin.axios();
	private final AxiosSettings<T, Settings<T>> axiosSettings;
	
	private Settings(String path, Class<T> type, T defaultValue)
	{
		axiosSettings = (AxiosSettings<T, Settings<T>>) axios.axiosSettings(this.getClass(), path, type, defaultValue);
	}
	
	@Override
	public @NonNull String path()
	{
		return axiosSettings.path();
	}
	
	@Override
	public @NonNull List<String> comments()
	{
		return axiosSettings.comments();
	}
	
	@Override
	public @NonNull T get()
	{
		return axiosSettings.get(this);
	}
	
	@Override
	public @NonNull T defaultValue()
	{
		return axiosSettings.defaultValue();
	}
	
	@Override
	public @NonNull Class<T> type()
	{
		return axiosSettings.type();
	}
}
