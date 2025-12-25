package com.jeffrey.commands;

import com.jeffrey.config.BotConfig;
import com.jeffrey.models.User;
import com.jeffrey.services.AssignmentService;
import com.jeffrey.services.RegistrationService;
import com.jeffrey.services.TextService;
import com.jeffrey.services.VoteService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.components.actionrow.ActionRow;

import java.util.Map;

public class VoteCommand implements Command {
    private final AssignmentService assignmentService;
    private final RegistrationService regService;
    private final VoteService voteService;

    public VoteCommand(AssignmentService as, RegistrationService rs, VoteService vs) {
        this.assignmentService = as;
        this.regService = rs;
        this.voteService = vs;
    }

    @Override public String getName() { return "vote"; }
    @Override public String getDescription() { return "Start voting rounds (owner only)"; }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
    	System.out.println("[VoteCommand] executeSlash called by user=" + event.getUser().getId()
    	        + " at " + java.time.Instant.now() + " on thread " + Thread.currentThread().getName());
        String owner = BotConfig.getOwnerID();
        if (owner == null || !event.getUser().getId().equals(owner)) {
            event.reply("Not authorized.").setEphemeral(true).queue(); return;
        }
        try {
            Map<String,String> assignments = assignmentService.getAllAssignments();
            if (assignments == null || assignments.isEmpty()) {
                event.reply("No assignments found. Run /start first.").setEphemeral(true).queue();
                return;
            }
            voteService.startSession(assignments);
            int currentIndex = voteService.getCurrentIndex() + 1;
            event.getChannel().sendMessage("Voting started — poll投票开始——民意调查 " + currentIndex + "/" + assignments.size() + " posted发布.").queue();

            // post first poll
            String recipientId = voteService.getCurrentRecipient().orElse(null);
            if (recipientId != null) {
                postPollForRecipient(event.getChannel().asTextChannel(), recipientId);
            }
            event.reply("Launched polls.").setEphemeral(true).queue();
        } catch (Exception ex) {
            ex.printStackTrace();
            event.reply("Failed to start voting: " + ex.getMessage()).setEphemeral(true).queue();
        }
    }

    // helper: build and send poll message to a TextChannel
    private void postPollForRecipient(TextChannel channel, String recipientId) throws Exception {
        Map<String, User> users = regService.loadAllUsers(); // id->User
        User recipient = users.get(recipientId);
        String giverId = assignmentService.getGiverFor(recipientId);
        User giver = users.get(giverId);
        
     // 1) Build personality display
        Map<String, String> answers = regService.getPersonalityAnswers(giverId);
        String personalityBlock;
        if (answers == null || answers.isEmpty()) {
            personalityBlock = "_No personality answers provided._\n\n";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("**Personality answers for the Secret Santa秘密圣诞老人的性格答案 ").append(":**\n");
            // Q1
            sb.append("Q1 — Your perfect winter evening你完美的冬夜: ")
              .append(expandQ1(answers.get("q1"))).append("\n");
            sb.append("Q2 — Favourite holiday colour palette最喜欢的节日配色方案: ")
              .append(expandQ2(answers.get("q2"))).append("\n");
            sb.append("Q3 — Best holiday treat最好的节日款待: ")
              .append(expandQ3(answers.get("q3"))).append("\n");
            sb.append("Q4 — Christmas animal you relate to most你最能感同身受的圣诞动物是什么: ")
              .append(expandQ4(answers.get("q4"))).append("\n\n");
            personalityBlock = sb.toString();
        }
        
        StringSelectMenu.Builder menu = StringSelectMenu.create("vote_for:" + recipientId)
                .setPlaceholder("Choose who you think gifted " + recipient.getName())
                .setMinValues(1).setMaxValues(1);
        
        for (String uid : users.keySet()) {
            if (uid.equals(recipientId)) continue;
            menu.addOption(users.get(uid).getName(), uid);
        }
        channel.sendMessage(personalityBlock + "Who gifted谁送的礼物给 **" + recipient.getName() + "**?").setComponents(ActionRow.of(menu.build())).queue();
    }
    
    private String expandQ1(String choice) {
        if (choice == null) return "(no answer)";
        switch (choice.toUpperCase()) {
            case "A": return "🍪🎬 Baking cookies and watching movies" + TextService.get("mandarin", "q1_option1") +  "(A)";
            case "B": return "🃏 Playing games with friends and family" + TextService.get("mandarin", "q1_option2") + "(B)";
            case "C": return "🍽️ A big family dinner" + TextService.get("mandarin", "q1_option3") + "(C)";
            case "D": return "✨ Out on the town — holiday market, light walk, concert, or event" + TextService.get("mandarin", "q1_option4") + "(D)";
            default: return "(invalid)";
        }
    }
    private String expandQ2(String choice) {
        if (choice == null) return "(no answer)";
        switch (choice.toUpperCase()) {
            case "A": return "❤️💚 Classic — red & forest green" + TextService.get("mandarin", "q2_option1") + "(A)";
            case "B": return "✨🤍 Warm — golds, creams, and cozy neutrals" + TextService.get("mandarin", "q2_option2") + "(B)";
            case "C": return "❄️🔷 Frosty — icy blue & silver" + TextService.get("mandarin", "q2_option3") + "(C)";
            case "D": return "🕯️🍷 Deep — burgundy, plum & candlelit tones" + TextService.get("mandarin", "q2_option4") + "(D)";
            default: return "(invalid)";
        }
    }
    private String expandQ3(String choice) {
        if (choice == null) return "(no answer)";
        switch (choice.toUpperCase()) {
            case "A": return "🍪❄️ Cookies (ginger, shortbread, sugar cookies)" + TextService.get("mandarin", "q3_option1") + "(A)";
            case "B": return "🍫🍬 Candy & chocolate (truffles)" + TextService.get("mandarin", "q3_option2") + "(B)";
            case "C": return "🧀🥨Savoury platter / cheese & charcuterie" + TextService.get("mandarin", "q3_option3") + "(C)";
            case "D": return "🥐☕ Something special from a local bakery or café" + TextService.get("mandarin", "q3_option4") + "(D)";
            default: return "(invalid)";
        }
    }
    private String expandQ4(String choice) {
        if (choice == null) return "(no answer)";
        switch (choice.toUpperCase()) {
            case "A": return "🦌 Reindeer — cheerful & helpful" + TextService.get("mandarin", "q4_option1") + "(A)";
            case "B": return "🐧 Penguin — playful & social" + TextService.get("mandarin", "q4_option2") + "(B)";
            case "C": return "🦊 Fox — clever & mischievous" + TextService.get("mandarin", "q4_option3") + "(C)";
            case "D": return "🐻‍❄️ Polar Bear — cozy, big-hearted & generous" + TextService.get("mandarin", "q4_option4") + "(D)";
            default: return "(invalid)";
        }
    }
}
