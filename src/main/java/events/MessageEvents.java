package events;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.Objects;

public class MessageEvents extends ListenerAdapter {
    // --- sends a greeting when a user mentions the bot ---
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String[] messageSent = event.getMessage()
                                    .getContentRaw()
                                    .split(" ");

        boolean containsGreeting = Arrays.stream(messageSent)
                .anyMatch(str -> str.contains("hello")
                        || str.contains("hi")
                        || str.contains("hey"));
        boolean mentionsBot = Arrays.stream(messageSent)
                .anyMatch(str -> str.contains("bot"));

        try {
            String userMention = Objects.requireNonNull(event.getMember())
                    .getUser()
                    .getAsMention();

            if(containsGreeting && mentionsBot) {
                boolean isNotBot = !(event.getMember()
                        .getUser()
                        .isBot());
                if (isNotBot) {
                    event.getChannel()
                            .sendMessage("Hello, " + userMention)
                            .queue();
                }
            }
        } catch (NullPointerException e) {
            System.out.println("ERROR: " + e.getMessage());
            System.out.println("EXCEPTION THROWN: NullPointerException");

            if(containsGreeting && mentionsBot) {
                    event.getChannel()
                            .sendMessage("Hello!")
                            .queue();
                }
        }
    }
}
