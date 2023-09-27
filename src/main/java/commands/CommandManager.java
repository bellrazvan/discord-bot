package commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

public class CommandManager extends ListenerAdapter {
    // --- guild commands ---
    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        Guild guild = event.getGuild();
        guild.updateCommands().addCommands(

                // --- slash commands ---
                Commands.slash("echo", "Repeats the message you type.")
                        .addOption(OptionType.STRING, "message", "The message to repeat.", true)
                        .addOption(OptionType.BOOLEAN, "ephemeral", "Whether or not the message should be sent as an ephemeral message."),
                Commands.slash("nasa", "Displays NASA's Astronomy Picture of the Day."),
                Commands.slash("joke", "Displays a random joke.")
                        .addOptions(
                                new OptionData(OptionType.STRING, "category", "Choose the joke's category.")
                                        .addChoice("Dark", "Dark")
                                        .addChoice("Misc", "Misc")
                                        .addChoice("Programming", "Programming")
                                        .addChoice("Pun", "Pun")
                        )
                        .addOption(OptionType.BOOLEAN, "ephemeral", "Whether or not the joke should be sent as an ephemeral message."),
                Commands.slash("welcome", "Get a welcome message."),
                Commands.slash("roles", "Get a list with all the server's roles."),
                Commands.slash("riddle", "Get a random riddle.")
                        .addOption(OptionType.BOOLEAN, "ephemeral", "Whether or not the answer should be sent as an ephemeral message."),
                Commands.slash("giverole", "Give a role to a user.")
                        .addOption(OptionType.USER, "user", "The user who gets the role.", true)
                        .addOption(OptionType.ROLE, "role", "The role to be given.", true)   // FIX THIS -> Exclude BOT roles and @everyone role
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),

                // --- context menu commands ---
                // --- right click on user -> Apps ---
                Commands.context(Command.Type.USER, "Get user avatar"),
                Commands.context(Command.Type.USER, "Get online status"),
                Commands.context(Command.Type.USER, "Mutual servers"),

                // --- right click on message -> Apps ---
                Commands.message("Count words"),
                Commands.message("Reverse message"),
                Commands.message("Shuffle message")

        ).queue();
    }
}
