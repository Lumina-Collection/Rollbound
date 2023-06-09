package net.luminacollection.rollbound.paper.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import io.papermc.paper.plugin.configuration.PluginMeta;
import net.luminacollection.rollbound.paper.RollboundPlugin;
import net.luminacollection.rollbound.common.i18n.Messages;
import software.axios.api.Axios;
import software.axios.api.command.CommandsInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandRollbound implements CommandsInterface
{
	private static CommandRollbound instance;
	private final RollboundPlugin plugin = RollboundPlugin.instance();
	private final Axios axios = plugin.axios();
	private final CommandAPICommand command;
	private final List<CommandAPICommand> subCommands = new ArrayList<>();
	
	private CommandRollbound()
	{
		command = new CommandAPICommand("rollbound");
		commandRoot();
		commandReload();
		command.withSubcommands(subCommands.toArray(new CommandAPICommand[0]));
	}
	
	public static CommandRollbound instance()
	{
		if (instance == null) instance = new CommandRollbound();
		return instance;
	}
	
	@SuppressWarnings("all")
	private void commandRoot()
	{
		command.withAliases("rb");
		command.withPermission("rollbound.command.rollbound");
		command.executes((sender, args) -> {
			PluginMeta pluginMeta = plugin.getPluginMeta();
			assert pluginMeta != null;
			Messages.COMMAND.sendTo(sender, axios.tagBuilder().add(Map.of(
				"version", pluginMeta.getVersion(),
				"author", pluginMeta.getAuthors().get(0),
				"website", "<click:open_url:'" + pluginMeta.getWebsite() + "'>" + pluginMeta.getWebsite() + "</click>",
				"description", pluginMeta.getDescription(),
				"name", pluginMeta.getName()
			), true).build());
		});
	}
	
	private void commandReload()
	{
		CommandAPICommand subCommand = new CommandAPICommand("reload");
		subCommand.withAliases("r");
		subCommand.withPermission("command.rollbound.reload");
		subCommand.executes((sender, args) -> {
			plugin.reload();
			Messages.COMMAND_RELOAD.sendTo(sender);
		});
		subCommands.add(subCommand);
	}
	
	@Override
	public void register()
	{
		command.register();
	}
	
	@Override
	public void unregister()
	{
		CommandAPI.unregister(command.getName());
	}
}
