package net.luminacollection.rollbound.paper.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import net.luminacollection.rollbound.common.roll.Roll;
import net.luminacollection.rollbound.paper.RollboundPlugin;
import net.luminacollection.rollbound.paper.roll.RollManager;
import software.axios.api.command.CommandsInterface;

public class CommandProbe implements CommandsInterface
{
	private static CommandProbe instance;
	private final RollboundPlugin plugin = RollboundPlugin.instance();
	private final String COMMAND_NAME = "probe";
	private final CommandAPICommand command = new CommandAPICommand(COMMAND_NAME);
	
	private CommandProbe()
	{
		command.withPermission("rollbound.command.probe");
		command.withArguments(new IntegerArgument("Zielwert", 1, 100));
		command.withOptionalArguments(new IntegerArgument("Modifikator", -100, 100), new IntegerArgument("VT/NT", -100, 100));
		command.executesPlayer((player, args) -> {
			var target = (Integer) args.get("Zielwert");
			var modifier = (Integer) args.getOrDefault("Modifikator", 0);
			var vtnt = (Integer) args.getOrDefault("VT/NT", 0);
			var sDie = "1d20";
			assert target != null;
			var sModifier = modifier > 0 ? "+" + modifier : modifier < 0 ? modifier.toString() : "";
			var sVtnt = vtnt > 0 ? "kh" + vtnt + "d6" : vtnt < 0 ? "dh" + (vtnt * -1) + "d6" : "";
			var sRoll = sDie + sModifier + sVtnt;
			var roll = new Roll(sRoll);
			roll.setThreshold(target);
			RollManager.instance().roll(player, roll, false);
		});
	}
	
	public static CommandProbe instance()
	{
		return instance == null ? instance = new CommandProbe() : instance;
	}
	
	@Override
	public void register()
	{
		command.register(plugin);
	}
	
	@Override
	public void unregister()
	{
		CommandAPI.unregister(COMMAND_NAME);
	}
}
