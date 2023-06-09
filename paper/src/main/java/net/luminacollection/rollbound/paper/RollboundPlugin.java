package net.luminacollection.rollbound.paper;

import net.luminacollection.rollbound.common.roll.SuccessState;
import net.luminacollection.rollbound.paper.commands.CommandProll;
import net.luminacollection.rollbound.paper.commands.CommandRoll;
import net.luminacollection.rollbound.paper.commands.CommandRollbound;
import net.luminacollection.rollbound.paper.configuration.Settings;
import net.luminacollection.rollbound.common.i18n.Messages;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import software.axios.api.Axios;
import software.axios.api.AxiosApiPlugin;
import software.axios.api.command.CommandsInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class RollboundPlugin extends JavaPlugin implements AxiosApiPlugin
{
	private final List<CommandsInterface> commands = new ArrayList<>();
	private Axios axios;
	private static RollboundPlugin instance;
	public static RollboundPlugin instance()
	{
		return instance;
	}
	
	@Override
	public void onLoad()
	{
		instance = this;
	}
	
	@Override
	public void onEnable()
	{
		RegisteredServiceProvider<Axios> provider = Bukkit.getServicesManager().getRegistration(Axios.class);
		if (provider != null) axios = provider.getProvider();
		else throw new RuntimeException("Axios not found!");
		
		reload();
		
		commands.addAll(Arrays.asList(
			CommandRollbound.instance(),
			CommandRoll.instance(),
			CommandProll.instance()
		));
		
		commands.forEach(CommandsInterface::register);
	}
	
	@Override
	public void onDisable()
	{
		commands.forEach(CommandsInterface::unregister);
	}
	
	public Axios axios()
	{
		return axios;
	}
	
	private void setupSettings()
	{
		axios.configManager().setup(this, Settings.class);
	}
	private void setupMessages()
	{
		axios.i18nManager().setup(this, Messages.class, Locale.GERMAN);
	}
	
	public void reload()
	{
		setupSettings();
		setupMessages();
		SuccessState.refresh(Settings.SUCCESS_STATES.get());
	}
	
	@Override
	public void saveResources(String resourcePath, boolean replace)
	{
		saveResource(resourcePath, replace);
	}
	
	@Override
	public @NotNull File pluginFolder()
	{
		return getDataFolder();
	}
}
