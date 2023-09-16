package events;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MessageEvents extends ListenerAdapter {
    //optional
    private final Map<Long, Message> messageCache = new HashMap<>();

    public void onMessageReceived(MessageReceivedEvent event) {
        //        String[] messageSent = event.getMessage()
        //                .getContentRaw().split(" ");

        String messageSent = event.getMessage()
                                .getContentRaw();

        String name = Objects.requireNonNull(event.getMember())
                                                .getUser()
                                                .getEffectiveName();
        //optional
        Message message = event.getMessage();
        messageCache.put(message.getIdLong(), message);

        if(messageSent.equalsIgnoreCase("hello"))
            if(!event.getMember().getUser().isBot()) {
                    event.getChannel()
                            .sendMessage("Hello, " + name + "!")
                            .queue();
//                    event.getMessage()
//                            .addReaction(Emoji.fromUnicode("U+1F60A"))
//                            .queue();
            }
    }

    public void onMessageDelete(MessageDeleteEvent event) {
        //optional
        long messageId = event.getMessageIdLong();
        Message deletedMessage = messageCache.get(messageId);
        User deletedBy = deletedMessage != null ? deletedMessage.getAuthor() : null;

        if(deletedBy != null && !deletedBy.isBot())
            event.getChannel()
                    .sendMessage(deletedBy.getAsMention() + " deleted a message in this channel.")
                    .queue();
    }
}
