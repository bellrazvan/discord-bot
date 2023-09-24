package events;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MessageEvents extends ListenerAdapter {
    private final Map<Long, Message> messageCache = new HashMap<>();

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
            if(containsGreeting && mentionsBot) {
                    event.getChannel()
                            .sendMessage("Hello!")
                            .queue();
                }
        }

        Message message = event.getMessage();
        messageCache.put(message.getIdLong(), message);
    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        long messageId = event.getMessageIdLong();
        Message deletedMessage = messageCache.get(messageId);
        User deletedBy = deletedMessage != null ? deletedMessage.getAuthor() : null;

        if(deletedBy != null && !deletedBy.isBot())
            event.getChannel()
                    .sendMessage(deletedBy.getAsMention() + " deleted a message in this channel.")
                    .queue();
    }
}
