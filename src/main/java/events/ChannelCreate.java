package events;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;

public class ChannelCreate extends ListenerAdapter {
    public void onChannelCreate(ChannelCreateEvent event) {
        String channelName = event.getChannel()
                                .getName();
//repair
        event.getGuild()
                .getDefaultChannel()
                .asTextChannel()
                .sendMessage("A new channel was created: " + channelName)
                .queue();

    }

}
