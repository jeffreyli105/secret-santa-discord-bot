package com.jeffrey.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.modals.Modal;

public class RevealCommand implements Command {

    public RevealCommand() {
        
    }

    @Override
    public String getName() {
        return "reveal";
    }

    @Override
    public String getDescription() {
        return "Reveal your Secret Santa assignment (password required)揭晓你的秘密圣诞老人任务（需要密码)";
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        // Build a modal with a single password field
        TextInput passwordInput = (TextInput) TextInput.create("reveal_password", TextInputStyle.SHORT)
                .setRequired(true)
                .setMinLength(6)
                .setPlaceholder("Enter your registration password输入您的注册密码")
                .build();
        Modal modal = Modal.create("reveal_modal", "Reveal your recipient显示您的收件人")
                .addComponents(
                        Label.of("Enter your password输入您的密码", passwordInput)
                ).build();

        // Show the modal to the user
        event.replyModal(modal).queue();
    }
}
