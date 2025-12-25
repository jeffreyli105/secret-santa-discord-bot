package com.jeffrey.commands;

import com.jeffrey.services.RegistrationService;

import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class RegistrationCommand implements Command {
    private final RegistrationService regService;
    public RegistrationCommand(RegistrationService regService) { this.regService = regService; }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
    	// if already registered, tell them
    	if (regService != null && regService.isRegistered(event.getUser().getId())) {
            event.reply("You are already registered.您已注册成功.").setEphemeral(true).queue();
            return;
        }
    	StringSelectMenu menu = StringSelectMenu.create("lang_select")
                .setPlaceholder("Choose language 选择语言")
                .addOption("English英语", "english")
                .addOption("Mandarin中文", "mandarin")
                .build();

        event.reply("Choose your language to begin registration请选择您的语言开始注册：")
                .setEphemeral(true)
                .setComponents(ActionRow.of(menu))
                .queue();
    }

	@Override
	public String getName() {
		return "register";
	}

	@Override
	public String getDescription() {
		return "Registers a user注册一个用户";
	}
}
