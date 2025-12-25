package com.jeffrey.listeners;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

import com.jeffrey.services.AssignmentService;
import com.jeffrey.services.RegistrationService;
import com.jeffrey.services.TextService;
import com.jeffrey.models.User;

import org.mindrot.jbcrypt.BCrypt;

import org.jetbrains.annotations.NotNull;

/**
 * Handles the modal submission from /reveal.
 * Verifies password against stored hash and responds ephemerally with the assignment.
 */
public class ModalRevealSubmitListener extends ListenerAdapter {

    private final RegistrationService regService;
    private final AssignmentService assignmentService;

    public ModalRevealSubmitListener(RegistrationService regService, AssignmentService assignmentService) {
        this.regService = regService;
        this.assignmentService = assignmentService;
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        // Only handle the modal we created
    	System.out.println("Submitted modal");
    	String modalId = event.getModalId();
        if (modalId == null || !"reveal_modal".equals(modalId)) {
        	System.out.println("Modal Id is not reveal_modal");
        	return;
        }

        String discordId = event.getUser().getId();

        // Load user record via your RegistrationService (implement this method)
		try {
            String submitted = event.getValue("reveal_password").getAsString();
			
			User user = regService.loadUserByDiscordId(discordId);
			String lang = regService.getLanguageFor(discordId);
	        if (user == null) {
	        	event.reply(TextService.get(lang, "not_registered")).setEphemeral(true).queue();
	            return;
	        }

	        String storedHash = user.getPasswordHash(); // bcrypt hash stored at registration
	        if (storedHash == null || storedHash.isEmpty() || !BCrypt.checkpw(submitted, storedHash)) {
                event.reply(TextService.get(lang, "incorrect_password")).setEphemeral(true).queue();
                return;
            }

	        // Password OK -> fetch assignment (implement this in RegistrationService)
	        String assignedDiscordId = assignmentService.getAssignmentFor(discordId);
	        if (assignedDiscordId == null) {
	        	event.reply(TextService.get(lang, "reveal_nullid")).setEphemeral(true).queue();
	            return;
	        }

	        User assigned = regService.loadUserByDiscordId(assignedDiscordId);
            String assignedName = assigned != null ? assigned.getName() : "Unknown";
            String assignedWishlist = assigned.getWishlist();
            
	        // Reply ephemerally so only the user sees it
            String replyMessage = "🎁 " + TextService.get(lang, "gifting_to") + ": **" + assignedName + "**\n"
            		+ "📝 " + TextService.get(lang, "here_wishlist") + ": " + assignedWishlist;
	        event.reply(replyMessage).setEphemeral(true).queue();
		} catch (Exception e) {
			e.printStackTrace();
            String msg = e.getMessage() != null ? e.getMessage() : "internal error";
            event.reply("Something went wrong出了点问题: " + msg).setEphemeral(true).queue();
		}
        
    }
}
