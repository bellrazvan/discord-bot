package events;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;

public class ChannelCreate extends ListenerAdapter {
    public void onChannelCreate(ChannelCreateEvent event) {
        String channelName = event.getChannel()
                                .getAsMention();
        String channelType = event.getChannelType()
                                .toString()
                                .toLowerCase();
        String message = "A new " + channelType + " channel was created: " + channelName;

        event.getGuild()
                .getTextChannels()
                .get(0)
                .sendMessage(message)
                .queue();
    }

}
