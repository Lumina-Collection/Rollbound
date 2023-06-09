package net.luminacollection.rollbound.paper.roll;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentException;
import dev.jorel.commandapi.arguments.StringArgument;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.luminacollection.rollbound.common.color.SuccessColor;
import net.luminacollection.rollbound.common.roll.Roll;
import net.luminacollection.rollbound.common.roll.SuccessState;
import net.luminacollection.rollbound.common.roll.SuccessState.Triggered;
import net.luminacollection.rollbound.common.utils.ParserStringDice;
import net.luminacollection.rollbound.paper.RollboundPlugin;
import net.luminacollection.rollbound.paper.configuration.Settings;
import net.luminacollection.rollbound.common.i18n.Messages;
import net.luminacollection.rollbound.paper.hooks.VentureChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import software.axios.api.Axios;
import software.axios.api.i18n.TagBuilder;
import software.axios.api.platform.AxiosEntity;

import java.util.Locale;

public class RollManager
{
	private final RollboundPlugin plugin = RollboundPlugin.instance();
	private final Axios axios = plugin.axios();
	
	private static RollManager instance;
	
	public static RollManager instance()
	{
		if (instance == null) instance = new RollManager();
		return instance;
	}
	private RollManager() {}
	private String getDiceGroupsString(Roll roll, Locale locale)
	{
		var sb = new StringBuilder();
		var diceGroups = roll.diceGroups();
		
		for (int i = 0; i < diceGroups.length; i++)
		{
			sb.append(Messages.COMMAND_ROLL_DIE.toString(locale).replace("<count>", String.valueOf(diceGroups[i][0])).replace("<faces>", String.valueOf(diceGroups[i][1])));
			if (i < diceGroups.length - 1) sb.append(Messages.COMMAND_ROLL_DIE_SEPERATOR.toString(locale));
		}
		return sb.toString();
	}
	
	private String getResultsString(Roll roll, Locale locale)
	{
		var sb = new StringBuilder();
		var diceResults = roll.diceResults();
		var diceGroups = roll.diceGroups();
		var countOfComputedDice = 0;
		for (int[] diceGroup : diceGroups)
		{
			var resultGroupString = Messages.COMMAND_ROLL_RESULTS.toString(locale);
			var dieString = Messages.COMMAND_ROLL_DIE.toString(locale)
								.replace("<count>", String.valueOf(diceGroup[0]))
								.replace("<faces>", String.valueOf(diceGroup[1]));
			resultGroupString = resultGroupString.replace("<die>", dieString);
			var resultListBuilder = new StringBuilder();
			for (int countOfDieWithinCurrentDiceGroup = 0; countOfDieWithinCurrentDiceGroup < diceGroup[0]; countOfDieWithinCurrentDiceGroup++)
			{
				var result = diceResults[countOfComputedDice];
				resultListBuilder.append(Messages.COMMAND_ROLL_RESULT.toString(locale)
											 .replace("<result>", String.valueOf(result))
											 .replace("result_color", SuccessColor.byFacesAndResult(diceGroup[1], result, Triggered.valueOf(Settings.TRIGGERED.get().toUpperCase()))));
				if (countOfDieWithinCurrentDiceGroup < diceGroup[0] - 1)
					resultListBuilder.append(Messages.COMMAND_ROLL_RESULT_SEPERATOR.toString(locale));
				countOfComputedDice++;
			}
			resultGroupString = resultGroupString.replace("<dice_group_results>", resultListBuilder.toString());
			sb.append(resultGroupString);
		}
		var resultsString = sb.toString();
		if (resultsString.endsWith("<newline>")) resultsString = resultsString.substring(0, resultsString.length() - 9);
		return resultsString;
	}
	
	private String getModifierString(Roll roll, Locale locale)
	{
		var positive = Messages.COMMAND_ROLL_MODIFIER_POSITIVE.toString(locale);
		var negative = Messages.COMMAND_ROLL_MODIFIER_NEGATIVE.toString(locale);
		var modifier = roll.modifier();
		var string = "";
		if (modifier >= 0) string = positive;
		else string = negative;
		modifier = Math.abs(modifier);
		string = string.replace("<modifier>", String.valueOf(modifier));
		
		return string;
	}
	
	private SuccessState getSuccessState(Roll roll)
	{
		for (SuccessState successState : SuccessState.getAll())
		{
			var targetPercentage = Math.abs(successState.percentage());
			var percentage = roll.threshold() == 0 ? 0F : (float) roll.totalResult() / roll.threshold() * 100F;
			var triggered = successState.triggered();
			var total = false;
			if (successState.percentage() < 0)
			{
				total = true;
				if (roll.dice().length != 1) continue;
				if (!roll.diceWithResults().containsKey(Settings.NUMBER_OF_FACES.get())) continue;
				percentage = roll.diceResults()[0];
			}
			if (!total && percentage == 0) continue;
			var triggeredBelow = triggered == Triggered.BELOW && percentage <= targetPercentage;
			var triggeredAbove = triggered == Triggered.ABOVE && percentage > targetPercentage;
			if (!triggeredBelow && !triggeredAbove) continue;
			return successState;
		}
		return null;
	}
	
	private String getSuccessStateString(SuccessState successState, Locale locale)
	{
		return successState == null ? "" : Messages.COMMAND_ROLL_SUCCESS_STATE.toString(locale)
											   .replace("<success_state>", successState.message().toString(locale));
	}
	
	private Audience getRangedAudience(Player player)
	{
		
		var entry = VentureChat.instance().getRangeAndPermission(player);
		var range = entry.getKey();
		var permission = entry.getValue();
		Audience console = Audience.audience(Bukkit.getConsoleSender());
		Audience players = switch (range)
		{
			case -1 -> Audience.audience(Bukkit.getServer().getOnlinePlayers());
			case 0 -> player;
			default -> Audience.audience(
				Bukkit.getServer().getOnlinePlayers().stream().filter(
					target -> target.getWorld().equals(player.getWorld())
								  && target.getLocation().distance(player.getLocation()) <= range
								  && (permission.isEmpty() || target.hasPermission(permission))
				).toList()
			);
		};
		return Audience.audience(players, console);
	}
	
	public void roll(Player player, Roll roll, boolean privateRoll)
	{
		TagBuilder tagBuilder = axios.tagBuilder();
		AxiosEntity entity = axios.entity(player);
		tagBuilder.add("player", entity);
		tagBuilder.add("results", getResultsString(roll, player.locale()), true);
		tagBuilder.add("dice", getDiceGroupsString(roll, player.locale()), true);
		tagBuilder.add("modifier", getModifierString(roll, player.locale()), true);
		tagBuilder.add("total", roll.totalResult());
		var successState = getSuccessState(roll);
		tagBuilder.add("success_state", getSuccessStateString(successState, player.locale()), true);
		Audience audience = privateRoll ? Audience.audience(player, Bukkit.getConsoleSender()) : getRangedAudience(player);
		Messages.COMMAND_ROLL.sendTo(audience, tagBuilder.build());
		if (successState == null) audience.playSound(Sound.sound(Key.key("block.stem.step"), Source.MASTER, 2F, .1F));
		else audience.playSound(successState.sound());
	}
	
	public Argument<String> rollStringArgument(String nodeName)
	{
		return new CustomArgument<>(new StringArgument(nodeName), info ->
		{
			if (!ParserStringDice.matches(info.input())) throw CustomArgumentException.fromString("The roll string you provided is invalid! Valid roll strings include: 1d5, 3d12.2d5, 99d50+3, 1d4-6, etc.");
			return info.input();
		});
	}
}
