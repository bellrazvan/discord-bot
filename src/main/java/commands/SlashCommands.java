package commands;

import bot.Bot;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;
import uz.khurozov.jokeapi.JokeApi;
import uz.khurozov.jokeapi.constant.Category;
import uz.khurozov.jokeapi.constant.Lang;
import uz.khurozov.jokeapi.dto.Joke;
import uz.khurozov.jokeapi.dto.JokeFilter;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;

public class SlashCommands extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String command = event.getName();
        // [___] -> required
        // {___} -> optional

        // --- /echo [message] {ephemeral} ---
        if (command.equals("echo")) {
            String message = Objects.requireNonNull(event.getOption("message"))
                                                        .getAsString();
            // default -> false
            boolean ephemeral = false;

            try {
                ephemeral = Objects.requireNonNull(event.getOption("ephemeral"))
                                                        .getAsBoolean();
            } catch (NullPointerException ignored) {

            }

            event.reply(message)
                    .setEphemeral(ephemeral)
                    .queue();

        // --- /nasa ---
        } else if (command.equals("nasa")) {
            event.deferReply()
                    .queue();

            final String apiKey = Bot.getDotenv()
                                    .get("NASA_API_KEY");

            String photoUrl = null;
            String description = null;
            try {
                JsonNode jsonNode = getJsonFromURL("https://api.nasa.gov/planetary/apod?api_key=" + apiKey);
                photoUrl = jsonNode.get("url")
                                .asText();
                description = jsonNode.get("title")
                                .asText();
            } catch (IOException | InterruptedException e) {
                System.out.println("ERROR: " + e.getMessage());
                event.getHook()
                        .sendMessage("Error: Something went wrong")
                        .setEphemeral(true)
                        .queue();
            }

            File photo = null;
            try {
                // --- URL is deprecated, used uriObj.toURL() instead ---
                URI uri = URI.create(Objects.requireNonNull(photoUrl));
                URL url = uri.toURL();
                BufferedImage img = ImageIO.read(url);
                photo = new File("temp.jpg");
                ImageIO.write(img, "jpg", photo);

                event.getHook()
                        .sendMessage(Objects.requireNonNull(description))
                        .queue();
                event.getChannel()
                        .sendFiles(FileUpload.fromData(photo))
                        .queue();
            } catch (IOException | NullPointerException e) {
                System.out.println("ERROR: " + e.getMessage());
                event.getHook()
                        .sendMessage("Error: Something went wrong")
                        .setEphemeral(true)
                        .queue();
            } finally {
                boolean success = false;
                if (photo != null && photo.exists())
                    success = photo.delete();

                // --- it's possible for the file to be locked by another process ---
                if(!success)
                    System.out.println("INFO: Could not delete photo.");
            }

        // --- /joke {category} {ephemeral} ---
        } else if (command.equals("joke")) {
            event.deferReply()
                    .queue();

            // default -> false
            boolean ephemeral = false;
            // default -> any
            String category = "";

            try {
                ephemeral = Objects.requireNonNull(event.getOption("ephemeral"))
                                                    .getAsBoolean();
                category = Objects.requireNonNull(event.getOption("category"))
                                                    .getAsString()
                                                    .toUpperCase();
            } catch (NullPointerException ignored) {

            }

            Category type;
            switch(category) {
                case "DARK":
                        type = Category.Dark;
                        break;
                case "MISC":
                        type = Category.Misc;
                        break;
                case "PROGRAMMING":
                        type = Category.Programming;
                        break;
                case "PUN":
                        type = Category.Pun;
                        break;
                default:
                        type = Category.Any;
            }

            JokeApi jokeApi = new JokeApi();
            JokeFilter filter = new JokeFilter.Builder()
                                            .category(type)
                                            .lang(Lang.en)
                                            .build();
            Joke joke = jokeApi.getJoke(filter);
            event.getHook()
                    .sendMessage(joke.jokeString())
                    .setEphemeral(ephemeral)
                    .queue();

        // --- /welcome ---
        } else if (command.equals("welcome")) {
            try {
                String userMention = Objects.requireNonNull(event.getMember())
                        .getUser()
                        .getAsMention();
                event.reply("Welcome, " + userMention + "!")
                        .queue();
            } catch (NullPointerException e) {
                System.out.println("ERROR: Cannot find user");
            }

        // --- /roles ---
        } else if (command.equals("roles")) {
            event.deferReply()
                    .queue();

            List<Role> roleList = null;
            try {
                StringBuilder output = new StringBuilder("Roles: \n");
                roleList = Objects.requireNonNull(event.getGuild())
                                                    .getRoles();
                for (Role role : roleList)
                    output.append(role.getAsMention())
                            .append("\n");

                event.getHook()
                        .sendMessage(output.toString())
                        .queue();
            } catch (NullPointerException e) {
                event.getHook()
                        .sendMessage("Error: Something went wrong")
                        .setEphemeral(true)
                        .queue();
            } finally {
                if(roleList != null)
                    roleList.clear();
            }

        // --- /riddle {ephemeral} ---
        } else if (command.equals("riddle")) {
            event.deferReply()
                    .queue();

            //default -> false
            boolean ephemeral = false;
            try {
                ephemeral = Objects.requireNonNull(event.getOption("ephemeral"))
                                                        .getAsBoolean();
            } catch (NullPointerException ignored) {
                
            }

            try {
                JsonNode jsonNode = getJsonFromURL("https://riddles-api.vercel.app/random");
                String riddle = jsonNode.get("riddle")
                                    .asText();
                String answer = jsonNode.get("answer")
                                    .asText();

                event.getHook()
                        .sendMessage(riddle)
                        .queue();
                event.getHook()
                        .sendMessage("Answer: " + answer)
                        .setEphemeral(ephemeral)
                        .queue();
            } catch (IOException | InterruptedException e) {
                System.out.println("ERROR: " + e.getMessage());
                event.getHook()
                        .sendMessage("Error: Something went wrong")
                        .setEphemeral(true)
                        .queue();
            }

        }
    }

    // --- guild commands ---
    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        Guild guild = event.getGuild();
        guild.updateCommands().addCommands(
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
                                    .addOption(OptionType.BOOLEAN, "ephemeral", "Whether or not the answer should be sent as an ephemeral message.")
        ).queue();
    }

    // --- converts the url's content (string) to json ---
    private static JsonNode getJsonFromURL(@NotNull String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(responseBody, JsonNode.class);
    }

    //// --- global commands ---
//    @Override
//    public void onReady(@NotNull ReadyEvent event) {
//
//    }
}