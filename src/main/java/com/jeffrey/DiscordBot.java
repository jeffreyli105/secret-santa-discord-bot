package com.jeffrey;

import java.nio.file.Path;
import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeffrey.config.BotConfig;
import com.jeffrey.listeners.CommandListener;
import com.jeffrey.listeners.LanguageSelectListener;
import com.jeffrey.listeners.ModalRevealSubmitListener;
import com.jeffrey.listeners.RegistrationModalListener;
import com.jeffrey.listeners.VoteSelectListener;
import com.jeffrey.services.AssignmentService;
import com.jeffrey.services.RegistrationService;
import com.jeffrey.services.VoteService;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class DiscordBot {
	private static final Logger logger = LoggerFactory.getLogger(DiscordBot.class);
	private static JDA jda;
	
	public static void main(String[] args) {
		String botToken = BotConfig.getBotToken();
		if (botToken == null || botToken.isEmpty()) {
			logger.error("Bot token not found in config.properties. Please provide a valid token");
			return;
		}
		
		try {
			Path registrationsFile = FilePaths.REGISTRATIONS_FILE;
			Path assignmentsFile = FilePaths.ASSIGNMENTS_FILE;
			Path votesFile = FilePaths.VOTES_FILE;
	        RegistrationService regService = new RegistrationService(registrationsFile);
	        AssignmentService assignmentService = new AssignmentService(assignmentsFile);
	        VoteService voteService = new VoteService(votesFile);

			logger.info("Registrations file = " + registrationsFile.toAbsolutePath());

			jda = JDABuilder.createDefault(botToken)
					.enableIntents(EnumSet.allOf(GatewayIntent.class))
					.addEventListeners(
					   new CommandListener(voteService, regService, assignmentService),
					   new RegistrationModalListener(regService),
					   new ModalRevealSubmitListener(regService, assignmentService),
					   new LanguageSelectListener(regService),
					   new VoteSelectListener(voteService, regService, assignmentService)
					)
					.build();
			
			registerSlashCommands();
			jda.awaitReady();
			logger.info("Bot is online and ready!");
		} catch (Exception e) {
			logger.error("Error starting the bot: ", e);
		}
	}
	
	private static void registerSlashCommands() {
		if (jda == null) {
			logger.error("JDA instance is not initialized. Cannot register slash commands.");
			return;
		}
		
		logger.info("Registering Slash Commands...");
		jda.updateCommands()
		    .addCommands(
		    	Commands.slash("ping", "Checks the bot's latency to Discord's gateway."),
		    	Commands.slash("info", "Displays information about the bot."),
		    	Commands.slash("echo", "Responds back with your message.")
		    	    .addOption(OptionType.STRING, "text", "The text to echo", true),
		    	Commands.slash("register", "Open the registration flow (private)"),
		    	Commands.slash("unregister", "Unregisters yourself"),
		    	Commands.slash("start", "Starts the Secret Santa game"),
		    	Commands.slash("reveal", "Reveal your Secret Santa assignment (password required)"),
		    	Commands.slash("vote", "Start voting rounds (owner only)")
			    )
		    .queue(success -> logger.info("Slash commands registered successfully!"),
		    		failure -> logger.error("Failed to register slash commands: ", failure));
	}

}
