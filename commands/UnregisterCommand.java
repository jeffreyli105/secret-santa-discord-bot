package com.jeffrey.commands;

import com.jeffrey.services.RegistrationService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class UnregisterCommand implements Command {

    private final RegistrationService regService;

    public UnregisterCommand(RegistrationService regService) {
        this.regService = regService;
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        String userId = event.getUser().getId();

        // If not registered
        if (!regService.isRegistered(userId)) {
            event.reply("You are **not registered**, so there's nothing to remove.您**尚未注册**，因此无法删除任何内容.")
                 .setEphemeral(true)
                 .queue();
            return;
        }

        // Remove them
        boolean removed = regService.removeRegistration(userId);

        if (removed) {
            event.reply("Your registration has been **successfully removed**. You may re-register anytime.您的注册信息已被**成功移除**. 您可以随时重新注册.")
                 .setEphemeral(true)
                 .queue();
        } else {
            event.reply("An error occurred while removing your registration. Please contact the administrator.删除您的注册信息时发生错误。请联系管理员.")
                 .setEphemeral(true)
                 .queue();
        }
    }

    @Override
    public String getName() {
        return "unregister";
    }

    @Override
    public String getDescription() {
        return "Deletes your registration from the Secret Santa system.从秘密圣诞老人系统中删除您的注册信息.";
    }
}
