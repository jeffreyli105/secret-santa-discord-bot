package com.jeffrey.commands;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class InfoCommand implements Command {
	@Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Displays information about the bot.";
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Bot Information");
        embedBuilder.setDescription("This is a Discord Bot that executes commands for the Secret Santa game.");
        embedBuilder.setColor(new Color(0, 255, 0));
        embedBuilder.addField("Author", "Jeffrey", false);
        embedBuilder.addField("Language", "Java", true);
        embedBuilder.addField("Library", "JDA (Java Discord API)", true);
        embedBuilder.setFooter("Created with ❤️! Have fun!");

        MessageEmbed embed = embedBuilder.build();
        event.replyEmbeds(embed).queue();

    }
}
