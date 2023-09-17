package events;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Objects;

public class GuildEvents extends ListenerAdapter {
    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        TextChannel textChannel = Objects.requireNonNull(event.getGuild()
                                                            .getTextChannels()
                                                            .get(0));

        textChannel.sendMessage("Hello! I am happy to be a part of this server \uD83D\uDC4B")
                    .queue();
        textChannel.sendMessage("For more information about me type \"/help\"")
                    .queue();
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
            String newMemberName = Objects.requireNonNull(event.getMember())
                                                            .getAsMention();
            String guildName = event.getGuild().getName();

            boolean isNotBot = !(event.getMember()
                                    .getUser()
                                    .isBot());

            if(isNotBot) {
                TextChannel textChannel = Objects.requireNonNull(event.getGuild()
                                                                    .getTextChannels()
                                                                    .get(0));

                textChannel.sendMessage("Hello, " + newMemberName)
                            .queue();
                textChannel.sendMessage("\nWelcome to " + guildName + "!")
                            .queue();
            }
    }

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
