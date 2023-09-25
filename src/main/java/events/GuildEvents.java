package events;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

public class GuildEvents extends ListenerAdapter {
    // --- sends a message when the bot joins a guild ---
    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        try {
            TextChannel textChannel = Objects.requireNonNull(event.getGuild()
                                                    .getTextChannels()
                                                    .get(0));
            textChannel.sendMessage("Hello! I am happy to be a part of this server \uD83D\uDC4B")
                    .queue();
            textChannel.sendMessage("For more information about me type \"/help\"")
                    .queue();
        } catch (NullPointerException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    // --- sends a greeting to new users ---
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
            String newMemberName = null;
            try {
                newMemberName = Objects.requireNonNull(event.getMember())
                                                        .getAsMention();
            } catch (NullPointerException e) {
                System.out.println("ERROR: " + e.getMessage());
            }

            String guildName = event.getGuild()
                                    .getName();
            boolean isNotBot = !(event.getMember()
                                    .getUser()
                                    .isBot());

            if(isNotBot) {
                try {
                    TextChannel textChannel = Objects.requireNonNull(event.getGuild()
                                                            .getTextChannels()
                                                            .get(0));
                    textChannel.sendMessage("Hello, " + newMemberName)
                            .queue();
                    textChannel.sendMessage("\nWelcome to " + guildName + "!")
                            .queue();
                } catch (NullPointerException e) {
                    System.out.println("ERROR: " + e.getMessage());
                }
            }
    }

    // --- sends a message notifying the creation of a channel ---
    @Override
    public void onChannelCreate(@NotNull ChannelCreateEvent event) {
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

//    // --- sends the number of online users when someone updates their online status ---
//    @Override
//    public void onUserUpdateOnlineStatus(@NotNull UserUpdateOnlineStatusEvent event) {
//        TextChannel textChannel = Objects.requireNonNull(event.getGuild()
//                                                            .getTextChannels()
//                                                            .get(0));
//
//        List<Member> memberList = event.getGuild()
//                .getMembers();
//        int onlineMembers = 0;
//
//        for(Member member : memberList) {
//            boolean memberIsOnline = member.getOnlineStatus().equals(OnlineStatus.ONLINE);
//            boolean isNotBot = !(event.getMember().getUser().isBot());
//            if(memberIsOnline && isNotBot) {
//                onlineMembers++;
//            }
//        }
//        textChannel.sendMessage("There are " + onlineMembers + " members online!")
//                    .queue();
//    }
}
