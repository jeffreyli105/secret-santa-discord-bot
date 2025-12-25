package com.jeffrey.listeners;

import com.jeffrey.services.RegistrationService;
import com.jeffrey.services.TextService;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.mindrot.jbcrypt.BCrypt;

public class RegistrationModalListener extends ListenerAdapter {
    private final RegistrationService registrationService;
    private static final String MODAL_ID_PREFIX = "register_modal|lang:";

    public RegistrationModalListener(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        String modalId = event.getModalId();
        if (modalId == null || !modalId.startsWith(MODAL_ID_PREFIX)) return;

        String language = modalId.substring(MODAL_ID_PREFIX.length());

        String name = event.getValue("name_input").getAsString().trim();
        String password = event.getValue("password_input").getAsString();
        String email = event.getValue("email_input").getAsString().trim();

        // hash password
        String pwHash = BCrypt.hashpw(password, BCrypt.gensalt(12));
        String userId = event.getUser().getId();
        registrationService.ensureDraftRegistration(userId);
        
        String wishlist = event.getValue("wishlist_input").getAsString().trim();

        RegistrationService.Registration reg = registrationService.getDraftRegistration(userId); // get existing draft

        reg.name = name;
        reg.language = language;
        reg.email = email;
        reg.passwordHash = pwHash;
        reg.wishlist = wishlist;

        registrationService.addRegistration(userId, reg); // saves everything including personality answers

        event.reply(TextService.get(language, "thank_you")).setEphemeral(true).queue();
    }
}
