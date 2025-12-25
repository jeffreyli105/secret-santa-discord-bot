package com.jeffrey.listeners;

import com.jeffrey.services.VoteService;
import com.jeffrey.services.RegistrationService;
import com.jeffrey.services.TextService;
import com.jeffrey.models.User;
import com.jeffrey.services.AssignmentService;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public class VoteSelectListener extends ListenerAdapter {
    private final VoteService voteService;
    private final RegistrationService regService;
    private final AssignmentService assignmentService;

    public VoteSelectListener(VoteService vs, RegistrationService rs, AssignmentService as) {
        this.voteService = vs;
        this.regService = rs;
        this.assignmentService = as;
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
    	System.out.println("VOTE SELECT LISTENER BEING CALLEDSFODS HFDSHFH OSDHFOHDSIOFHOIDS");
        String comp = event.getComponentId();
        if (comp == null || !comp.startsWith("vote_for:")) return;

        String recipientId = comp.substring("vote_for:".length());
        String voterId = event.getUser().getId();
        String guessedGiverId = event.getValues().get(0);

        // validate it's the active poll
        Optional<String> cur = voteService.getCurrentRecipient();
        if (cur.isEmpty() || !cur.get().equals(recipientId)) {
            event.reply("This poll is not active.").setEphemeral(true).queue();
            return;
        }

        boolean ok = voteService.recordVote(recipientId, voterId, guessedGiverId);
        if (!ok) {
            event.reply("You already voted; votes are final.").setEphemeral(true).queue();
            return;
        }

        event.reply("Vote recorded — thanks!").setEphemeral(true).queue();

        // if everyone voted -> reveal and advance
        if (voteService.hasEveryoneVotedFor(recipientId)) {
            try {
                Map<String,String> assignments = assignmentService.getAllAssignments();
                Optional<String> maybeGiver = voteService.revealCurrentAndAdvance(assignments, assignmentService);
                String actualGiverId = maybeGiver.orElse(null);

                // public reveal
                String recipientName = regService.loadUserByDiscordId(recipientId).getName();
                String giverName = actualGiverId == null ? "Unknown" : regService.loadUserByDiscordId(actualGiverId).getName();
                event.getChannel().sendMessage("Reveal揭示: **" + recipientName + "** was gifted by收到礼物从 **" + giverName + "** 🎁").queue();

                // if more polls -> post next poll
                Optional<String> next = voteService.getCurrentRecipient();
                if (next.isPresent()) {
                    String nextId = next.get();
                    int currentIndex = voteService.getCurrentIndex() + 1;
                    event.getChannel().sendMessage("Voting started — poll投票开始——民意调查 " + currentIndex + "/" + assignments.size() + " posted发布.").queue();
                    // build and send poll for next person
                    postPollForRecipient(event.getChannel().asTextChannel(), nextId);
                } else {
                    // finished -> post public leaderboard
                    var points = voteService.getPointsSnapshot();
                    StringBuilder sb = new StringBuilder("Final leaderboard最终排行榜:\n");
                    points.entrySet().stream().sorted((a,b)->b.getValue().compareTo(a.getValue()))
                            .forEach(e -> {
                                String name = e.getKey();
                                try { name = regService.loadUserByDiscordId(e.getKey()).getName(); } catch (Exception ignored) {}
                                sb.append(name).append(" — ").append(e.getValue()).append(" pts点\n");
                            });
                    event.getChannel().sendMessage(sb.toString()).queue();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                event.getChannel().sendMessage("Error during reveal: " + ex.getMessage()).queue();
            }
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
