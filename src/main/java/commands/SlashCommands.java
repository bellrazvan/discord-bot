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

        if (command.equals("echo")) {
            String message = Objects.requireNonNull(event.getOption("message"))
                                                        .getAsString();
            boolean ephemeral = false;

            try {
                ephemeral = Objects.requireNonNull(event.getOption("ephemeral"))
                                                        .getAsBoolean();
            } catch (NullPointerException ignored) {

            }

            event.reply(message)
                    .setEphemeral(ephemeral)
                    .queue();

        } else if (command.equals("nasa")) {
            event.deferReply()
                    .queue();

            final String apiKey = Bot.getDotenv()
                                    .get("NASA_API_KEY");

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                                            .uri(URI.create("https://api.nasa.gov/planetary/apod?api_key=" + apiKey))
                                            .GET()
                                            .build();

            HttpResponse<String> response;
            JsonNode jsonNode = null;
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String responseBody = response.body();
                ObjectMapper mapper = new ObjectMapper();
                jsonNode = mapper.readValue(responseBody, JsonNode.class);
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
                event.getHook()
                        .sendMessage("Error: Something went wrong")
                        .setEphemeral(true)
                        .queue();
            }
            assert jsonNode != null;
            String photoUrl = jsonNode.get("url")
                                        .asText();
            String description = jsonNode.get("title")
                                        .asText();

            //URL is deprecated, used uriObj.toURL() instead
            URI uri;
            URL url;
            try {
                uri = URI.create(photoUrl);
                url = uri.toURL();
                BufferedImage img = ImageIO.read(url);
                File photo = new File("temp.jpg");
                ImageIO.write(img, "jpg", photo);

                event.getHook()
                        .sendMessage(description)
                        .queue();
                event.getChannel()
                        .sendFiles(FileUpload.fromData(photo))
                        .queue();
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage() + "\nCouldn't convert video to image");
                event.getHook()
                        .sendMessage("Error: Something went wrong")
                        .setEphemeral(true)
                        .queue();
            }

        } else if (command.equals("joke")) {
            event.deferReply()
                    .queue();

            boolean ephemeral = false;
            String category = "";
            Category type;

            try {
                ephemeral = Objects.requireNonNull(event.getOption("ephemeral"))
                                                    .getAsBoolean();
                category = Objects.requireNonNull(event.getOption("category"))
                                                    .getAsString()
                                                    .toUpperCase();
            } catch (NullPointerException ignored) {

            }

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

        } else if (command.equals("roles")) {
            event.deferReply()
                    .queue();

            try {
                StringBuilder output = new StringBuilder("Roles: \n");
                List<Role> roleList = Objects.requireNonNull(event.getGuild()).getRoles();
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
            }

        }
    }

    //Guild command
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
                            Commands.slash("roles", "Get a list with all the server's roles.")
        ).queue();
    }

    ////Global command
//    @Override
//    public void onReady(@NotNull ReadyEvent event) {
//
//    }
}