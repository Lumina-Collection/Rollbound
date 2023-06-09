package net.luminacollection.rollbound.common.i18n;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.checkerframework.checker.nullness.qual.NonNull;
import software.axios.api.Axios;
import software.axios.api.AxiosProvider;
import software.axios.api.i18n.AxiosMessages;
import software.axios.api.i18n.MessagesInterface;

import java.util.Locale;

public class Messages implements MessagesInterface
{
	public static final Messages COMMAND = new Messages("command.rollbound");
	public static final Messages COMMAND_RELOAD = new Messages("command.rollbound.reload");
	public static final Messages COMMAND_ROLL = new Messages("command.roll");
	public static final Messages COMMAND_ROLL_DIE = new Messages("command.roll.die");
	public static final Messages COMMAND_ROLL_DIE_SEPERATOR = new Messages("command.roll.die.seperator");
	public static final Messages COMMAND_ROLL_RESULT = new Messages("command.roll.result");
	public static final Messages COMMAND_ROLL_RESULTS = new Messages("command.roll.results");
	public static final Messages COMMAND_ROLL_RESULT_SEPERATOR = new Messages("command.roll.result.seperator");
	public static final Messages COMMAND_ROLL_MODIFIER_POSITIVE = new Messages("command.roll.modifier.positive");
	public static final Messages COMMAND_ROLL_MODIFIER_NEGATIVE = new Messages("command.roll.modifier.negative");
	public static final Messages COMMAND_ROLL_SUCCESS_STATE = new Messages("command.roll.success-state");
	
	private final Axios axios = AxiosProvider.get();
	private final AxiosMessages axiosMessages;
	
	private Messages(String path)
	{
		axiosMessages = axios.axiosMessages(this.getClass(), path);
	}
	
	@Override
	public @NonNull String toString(Locale locale)
	{
		return axiosMessages.toString(locale);
	}
	
	@Override
	public @NonNull String toString()
	{
		return axiosMessages.toString();
	}
	
	@Override
	public void sendTo(Audience audience, TagResolver placeholder)
	{
		axiosMessages.sendTo(audience, placeholder);
	}
	
	@Override
	public void sendTo(Audience audience)
	{
		axiosMessages.sendTo(audience);
	}
	
	public static Messages byPath(String path)
	{
		return new Messages(path);
	}
}
