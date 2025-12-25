package com.jeffrey.listeners;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.jeffrey.commands.Command;
import com.jeffrey.commands.EchoCommand;
import com.jeffrey.commands.InfoCommand;
import com.jeffrey.commands.PingCommand;
import com.jeffrey.commands.RegistrationCommand;
import com.jeffrey.commands.UnregisterCommand;
import com.jeffrey.commands.VoteCommand;
import com.jeffrey.commands.StartCommand;
import com.jeffrey.commands.RevealCommand;
import com.jeffrey.services.AssignmentService;
import com.jeffrey.services.RegistrationService;
import com.jeffrey.services.VoteService;


public class CommandListener extends ListenerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(CommandListener.class);
	private final Map<String, Command> commands = new HashMap<>();
	
	public CommandListener(VoteService voteService, RegistrationService registrationService, AssignmentService assignmentService) {
		commands.put("ping", new PingCommand());
		commands.put("info", new InfoCommand());
		commands.put("echo", new EchoCommand());
		commands.put("register", new RegistrationCommand(registrationService));
		commands.put("unregister", new UnregisterCommand(registrationService));
		commands.put("start", new StartCommand(registrationService, assignmentService));
		commands.put("reveal", new RevealCommand());
		commands.put("vote", new VoteCommand(assignmentService, registrationService, voteService));
		logger.info("Registered {} commands.", commands.size());
	}
	
	@Override
	public void onReady(@NotNull ReadyEvent event) {
		logger.info("JDA is ready! Logged in as {}#{}",
				event.getJDA().getSelfUser().getName(),
				event.getJDA().getSelfUser().getDiscriminator());
	}
	
	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		String commandName = event.getName();
		Command command = commands.get(commandName);
		
		if (command != null) {
			logger.debug("Executing slash command: {} from user: {}", commandName, event.getUser().getName());
			command.executeSlash(event);
		} else {
			logger.warn("Unknown slash command: {} from user: {}", commandName, event.getUser().getName());
			event.reply("Unknown command!").setEphemeral(true).queue();
		}
	}
}
