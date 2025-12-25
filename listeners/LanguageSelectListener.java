package com.jeffrey.listeners;

import org.jetbrains.annotations.NotNull;

import com.jeffrey.services.RegistrationService;
import com.jeffrey.services.TextService;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public class LanguageSelectListener extends ListenerAdapter {
	private final RegistrationService registrationService;
    public static final String MODAL_ID_PREFIX = "register_modal|lang:";

    public LanguageSelectListener(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        String compId = event.getComponentId(); // e.g., "lang_select", "quiz_q1", etc.
        if (compId == null) return;

        String discordId = event.getUser().getId();

        switch (compId) {
            case "lang_select" -> handleLangSelect(event, discordId);
            case "quiz_q1" -> handleQuizAnswer(event, discordId, "q1");
            case "quiz_q2" -> handleQuizAnswer(event, discordId, "q2");
            case "quiz_q3" -> handleQuizAnswer(event, discordId, "q3");
            case "quiz_q4" -> handleQuizAnswerThenModal(event, discordId, "q4");
        }
    }

    // ----------------- Handlers -----------------

    private void handleLangSelect(StringSelectInteractionEvent event, String discordId) {
        String lang = event.getValues().get(0);
        registrationService.ensureDraftRegistration(discordId);
        registrationService.setLanguageFor(discordId, lang);

        event.reply(TextService.get(lang, "q1_ask"))
                .setComponents(ActionRow.of(buildQ1(lang)))
                .setEphemeral(true).queue();
    }

    private void handleQuizAnswer(StringSelectInteractionEvent event, String discordId, String questionId) {
        String answer = event.getValues().get(0); // store A/B/C/D
        registrationService.setPersonalityAnswer(discordId, questionId, answer);
        String lang = registrationService.getLanguageFor(discordId);
        // advance to next question
        switch (questionId) {
            case "q1" -> event.reply(TextService.get(lang, "q2_ask"))
                    .setComponents(ActionRow.of(buildQ2(lang)))
                    .setEphemeral(true).queue();
            case "q2" -> event.reply(TextService.get(lang, "q3_ask"))
                    .setComponents(ActionRow.of(buildQ3(lang)))
                    .setEphemeral(true).queue();
            case "q3" -> event.reply(TextService.get(lang, "q4_ask"))
                    .setComponents(ActionRow.of(buildQ4(lang)))
                    .setEphemeral(true).queue();
        }
    }

    private void handleQuizAnswerThenModal(StringSelectInteractionEvent event, String discordId, String questionId) {
        String answer = event.getValues().get(0);
        registrationService.setPersonalityAnswer(discordId, questionId, answer);

        // Build registration modal
        Modal modal = buildRegistrationModal(registrationService.getLanguageFor(discordId));
        event.replyModal(modal).queue();
    }

    // ----------------- UI builders -----------------

    private StringSelectMenu buildQ1(String lang) {
        return StringSelectMenu.create("quiz_q1")
                .setPlaceholder(TextService.get(lang, "q1_placeholder"))
                .addOption("🍪🎬 " + TextService.get(lang, "q1_option1"), "A")
                .addOption("🃏 " + TextService.get(lang, "q1_option2"), "B")
                .addOption("🍽️ " + TextService.get(lang, "q1_option3"), "C")
                .addOption("✨ " + TextService.get(lang, "q1_option4"), "D")
                .build();
    }

    private StringSelectMenu buildQ2(String lang) {
        return StringSelectMenu.create("quiz_q2")
                .setPlaceholder(TextService.get(lang, "q2_placeholder"))
                .addOption("❤️💚 " + TextService.get(lang, "q2_option1"), "A")
                .addOption("✨🤍 " + TextService.get(lang, "q2_option2"), "B")
                .addOption("❄️🔷 " + TextService.get(lang, "q2_option3"), "C")
                .addOption("🕯️🍷 " + TextService.get(lang, "q2_option4"), "D")
                .build();
    }

    private StringSelectMenu buildQ3(String lang) {
        return StringSelectMenu.create("quiz_q3")
                .setPlaceholder(TextService.get(lang, "q3_placeholder"))
                .addOption("🍪 " + TextService.get(lang, "q3_option1"), "A")
                .addOption("🍫 " + TextService.get(lang, "q3_option2"), "B")
                .addOption("🧀 " + TextService.get(lang, "q3_option3"), "C")
                .addOption("🥐☕ " + TextService.get(lang, "q3_option4"), "D")
                .build();
    }

    private StringSelectMenu buildQ4(String lang) {
        return StringSelectMenu.create("quiz_q4")
                .setPlaceholder(TextService.get(lang, "q4_placeholder"))
                .addOption("🦌 " + TextService.get(lang, "q4_option1"), "A")
                .addOption("🐧 " + TextService.get(lang, "q4_option2"), "B")
                .addOption("🦊 " + TextService.get(lang, "q4_option3"), "C")
                .addOption("🐻‍❄️ " + TextService.get(lang, "q4_option4"), "D")
                .build();
    }

    private Modal buildRegistrationModal(String lang) {
        String modalId = LanguageSelectListener.MODAL_ID_PREFIX + (lang == null ? "english" : lang);

        TextInput nameInput = TextInput.create("name_input", TextInputStyle.SHORT)
                .setRequired(true)
                .setPlaceholder(TextService.get(lang, "name_label")).build();

        TextInput passwordInput = TextInput.create("password_input", TextInputStyle.SHORT)
                .setRequired(true)
                .setMinLength(6)
                .setPlaceholder(TextService.get(lang, "password_label")).build();

        TextInput emailInput = TextInput.create("email_input", TextInputStyle.SHORT)
                .setRequired(true)
                .setPlaceholder(TextService.get(lang, "email_label")).build();

        TextInput wishlistInput = TextInput.create("wishlist_input", TextInputStyle.PARAGRAPH)
                .setRequired(false)
                .setPlaceholder(TextService.get(lang, "wishlist_label")).build();

        return Modal.create(MODAL_ID_PREFIX + lang, "Complete Registration")
                .addComponents(
                        Label.of("Name", nameInput),
                        Label.of("Password", passwordInput),
                        Label.of("Email", emailInput),
                        Label.of("Wishlist", wishlistInput)
                ).build();
    }
}
