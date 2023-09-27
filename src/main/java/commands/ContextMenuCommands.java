package commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ContextMenuCommands extends ListenerAdapter {
    // --- user context commands ---
    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        String command = event.getName();

        // --- sends the user's avatar ---
        if (command.equals("Get user avatar")) {
            String avatarURL = event.getTarget()
                    .getEffectiveAvatarUrl();

            event.reply("Avatar: " + avatarURL)
                    .setEphemeral(true)
                    .queue();

        // --- sends the user's online status ---
        } else if (command.equals("Get online status")) { // FIX THIS
            try {
                String userStatus = Objects.requireNonNull(event.getTargetMember())
                        .getOnlineStatus()
                        .getKey();
                String userName = event.getTargetMember()
                        .getAsMention();

                event.reply(userName + " is " + userStatus)
                        .setEphemeral(true)
                        .queue();
            } catch (NullPointerException e) {
                System.out.println("ERROR: " + e.getMessage() + " (Get online status)");
                System.out.println("EXCEPTION THROWN: NullPointerException");

                event.reply("❌  **ERROR:** Something went wrong")
                        .queue();
            }

        // --- sends all the user's mutual servers ---
        } else if (command.equals("Mutual servers")) {
            List<Guild> guildList = event.getTarget().getMutualGuilds();
            List<String> guildListString = new ArrayList<>(guildList.size());

            for (Guild guild: guildList) {
                guildListString.add(guild.getName());
            }

            StringBuilder message = new StringBuilder();
            message.append("Mutual servers:\n");

            for (String guild: guildListString) {
                message.append(guild);

                boolean isLastElement = guild.equals(guildListString.get(guildListString.size()-1));
                if (isLastElement)
                    continue;
                message.append("\n");
            }

            event.reply(message.toString())
                    .setEphemeral(true)
                    .queue();

        }
    }

    // --- message context commands ---
    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        String command = event.getName();

        // --- counts the words in a message ---
        if (command.equals("Count words")) {
            int wordCount = event.getTarget()
                    .getContentRaw()
                    .split("\\s+")
                    .length;

            event.reply("Word count: " + wordCount)
                    .setEphemeral(true)
                    .queue();

        // --- reverses a message ---
        } else if (command.equals("Reverse message")) {
            String message = event.getTarget()
                                .getContentRaw();
            String reversedMessage = new StringBuilder(message).reverse()
                                                            .toString();

            event.reply("Reversed message: " + reversedMessage)
                    .setEphemeral(true)
                    .queue();

        // --- shuffles the words in a message ---
        } else if (command.equals("Shuffle message")) {
            List<String> message = new ArrayList<>(Arrays.asList(event.getTarget()
                                                                    .getContentRaw()
                                                                    .split("\\s+")));
            List<String> shuffle = new ArrayList<>();

            while(message.size() > 0) {
                int randomIndex = (int) (Math.random() * message.size());
                String word = message.remove(randomIndex);
                shuffle.add(word);
            }

            String shuffledMessage = String.join(" ", shuffle);
            event.reply("Shuffled message: " + shuffledMessage)
                    .setEphemeral(true)
                    .queue();

        }
    }
}
