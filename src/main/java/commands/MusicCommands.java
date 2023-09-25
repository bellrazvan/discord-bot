package commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class MusicCommands extends ListenerAdapter {
//    @Override
//    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
//        String command = event.getName();
//        // [___] -> required
//        // {___} -> optional
//
//        // --- /play ---
//        if(command.equals("play")) {
//
//        }
//    }
//
//    // --- guild commands ---
//    @Override
//    public void onGuildReady(@NotNull GuildReadyEvent event) {
//        Guild guild = event.getGuild();
//        guild.updateCommands().addCommands(
//                Commands.slash("play", "Play a song in your current voice channel.")
//                        .addOption(OptionType.STRING, "input", "Enter a search term or a link.", true)
//        ).queue();
//    }

    //// --- global commands ---
//    @Override
//    public void onReady(@NotNull ReadyEvent event) {
//
//    }
}
