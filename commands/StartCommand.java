package com.jeffrey.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.jeffrey.config.BotConfig;
import com.jeffrey.models.User;
import com.jeffrey.services.AssignmentService;
import com.jeffrey.services.EmailService;
import com.jeffrey.services.RegistrationService;
import com.jeffrey.services.TextService;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class StartCommand implements Command {
	
	private final RegistrationService regService;
	private final AssignmentService assignmentService;

    public StartCommand(RegistrationService regService, AssignmentService assignmentService) {
        this.regService = regService;
        this.assignmentService = assignmentService;
    }

	@Override
	public String getName() {
		return "start";
	}

	@Override
	public String getDescription() {
		return "Starts the Secret Santa game!";
	}

	@Override
	public void executeSlash(SlashCommandInteractionEvent event) {
		String ownerId = BotConfig.getOwnerID(); // read from properties or env
	    if (ownerId == null || !event.getUser().getId().equals(ownerId)) {
	        event.reply("You are not allowed to run this command.").setEphemeral(true).queue();
	        return;
	    }
		event.deferReply().queue();
		try {
            Map<String, User> users = regService.loadAllUsers();
            List<String> ids = new ArrayList<>(users.keySet());
	        Collections.shuffle(ids);
	        Map<String, String> assignments = new HashMap<>();
	        for (int i = 0; i < ids.size() - 1; i++) assignments.put(ids.get(i), ids.get(i + 1));
	        assignments.put(ids.get(ids.size() - 1), ids.get(0));
            for (String id : ids) {
            	String email = users.get(id).getEmail();
            	String name = users.get(id).getName();
            	String lang = regService.getLanguageFor(id);
            	String body = TextService.get(lang, "hi") + " " + name + ",\n\n"
                		+ TextService.get(lang, "email_body");
            	EmailService.sendEmail(email, TextService.get(lang, "email_subject"), body);
            }
            
            assignmentService.addAssignments(assignments);

            event.getHook().sendMessage("Secret Santa started — emails are being sent.").queue();
        } catch (Exception ex) {
            ex.printStackTrace();
            event.reply("Failed to start Secret Santa: " + ex.getMessage()).setEphemeral(true).queue();
        }
		
	}

}
