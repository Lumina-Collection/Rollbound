package net.luminacollection.rollbound.paper.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import net.luminacollection.rollbound.common.i18n.Messages;
import net.luminacollection.rollbound.common.roll.Roll;
import net.luminacollection.rollbound.paper.RollboundPlugin;
import net.luminacollection.rollbound.paper.configuration.Settings;
import net.luminacollection.rollbound.paper.roll.RollManager;
import software.axios.api.Axios;
import software.axios.api.command.CommandsInterface;

import java.util.ArrayList;
import java.util.List;

public class CommandRoll implements CommandsInterface
{
	
	private static CommandRoll instance;
	private final RollboundPlugin plugin = RollboundPlugin.instance();
	private final Axios axios = plugin.axios();
	private final RollManager rollManager = RollManager.instance();
	private final List<CommandAPICommand> commands = new ArrayList<>();
	private final List<CommandAPICommand> subCommands = new ArrayList<>();
	private final String COMMAND_NAME = Messages.byPath("command.roll.meta.name").toString();
	private final String COMMAND_ARGUMENT_THRESHOLD = Messages.byPath("command.roll.meta.argument.threshold").toString();
	private final String COMMAND_ARGUMENT_ROLL_STRING = Messages.byPath("command.roll.meta.argument.roll-string").toString();
	private final String COMMAND_SHORT_DESCRIPTION = Messages.byPath("command.roll.meta.short-description").toString();
	
	private CommandRoll()
	{
		for (int i = 0; i < 4; i++)
		{
			commands.add(new CommandAPICommand(COMMAND_NAME)
							 .withShortDescription(COMMAND_SHORT_DESCRIPTION)
							 .withPermission("command.roll"));
		}
		command1(commands.get(0));
		command2(commands.get(1));
		command3(commands.get(2));
		command4(commands.get(3));
		commands.get(0).withSubcommands(subCommands.toArray(new CommandAPICommand[0]));
	}
	
	public static CommandRoll instance()
	{
		if (instance == null) instance = new CommandRoll();
		return instance;
	}
	
	private void command1(CommandAPICommand command)
	{
		command.executesPlayer((player, args) -> {
			Roll roll = new  Roll("1d" + Settings.NUMBER_OF_FACES.get());
			rollManager.roll(player, roll, false);
		});
	}
	
	private void command2(CommandAPICommand command)
	{
		command.withArguments(new IntegerArgument(COMMAND_ARGUMENT_THRESHOLD, 1, 999));
		command.executesPlayer((player, args) -> {
			int threshold = (int) args.getOptional(COMMAND_ARGUMENT_THRESHOLD).orElse(1);
			Roll roll = new  Roll("1d" + Settings.NUMBER_OF_FACES.get());
			roll.setThreshold(threshold);
			rollManager.roll(player, roll, false);
		});
	}
	
	private void command3(CommandAPICommand command)
	{
		command.withArguments(new IntegerArgument(COMMAND_ARGUMENT_THRESHOLD, 1, 999));
		command.withArguments(rollManager.rollStringArgument(COMMAND_ARGUMENT_ROLL_STRING));
		command.executesPlayer((player, args) -> {
			int threshold = (int) args.getOptional(COMMAND_ARGUMENT_THRESHOLD).orElse(1);
			String rollString = String.valueOf(args.get(COMMAND_ARGUMENT_ROLL_STRING));
			Roll roll = new  Roll(rollString);
			roll.setThreshold(threshold);
			rollManager.roll(player, roll, false);
		});
	}
	private void command4(CommandAPICommand command)
	{
		command.withArguments(rollManager.rollStringArgument(COMMAND_ARGUMENT_ROLL_STRING));
		command.executesPlayer((player, args) -> {
			String rollString = String.valueOf(args.get(COMMAND_ARGUMENT_ROLL_STRING));
			Roll roll = new  Roll(rollString);
			rollManager.roll(player, roll, false);
		});
	}
	
	@Override
	public void register()
	{
		commands.forEach(CommandAPICommand::register);
	}
	
	@Override
	public void unregister()
	{
		commands.forEach(
				command -> CommandAPI.unregister(command.getName())
		);
	}
}
