package commands;

import bot.Bot;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
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
    // --- slash commands ---
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String command = event.getName();
        // <___> -> required
        // [___] -> optional

        // --- /echo <message> [ephemeral] ---
        // --- repeats a given message ---
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
        // --- sends NASA's Astronomy Photo of the Day ---
        } else if (command.equals("nasa")) {
            event.deferReply()
                    .queue();

            final String apiKey = Bot.getDotenvVariable("NASA_API_KEY");

            File photo = null;
            try {
                JsonNode jsonNode = getJsonFromURL("https://api.nasa.gov/planetary/apod?api_key=" + apiKey);
                String photoURL = Objects.requireNonNull(jsonNode.get("url"))
                                                        .asText();
                String description = Objects.requireNonNull(jsonNode.get("title"))
                                                        .asText();

                // --- URL is deprecated, used uriObj.toURL() instead ---
                URI uri = URI.create(photoURL);
                URL url = uri.toURL();
                BufferedImage img = ImageIO.read(url);
                photo = new File("temp.jpg");
                ImageIO.write(img, "jpg", photo);

                event.getHook()
                        .editOriginal(description)
                        .queue();
                event.getChannel()
                        .sendFiles(FileUpload.fromData(photo))
                        .queue();
            } catch (IOException | InterruptedException | NullPointerException e) {
                System.out.println("ERROR: " + e.getMessage() + " (/nasa)");
                if (e instanceof IOException)
                    System.out.println("EXCEPTION THROWN: IOException");
                else if (e instanceof InterruptedException)
                    System.out.println("EXCEPTION THROWN: InterruptedException");
                else
                    System.out.println("EXCEPTION THROWN: NullPointerException");

                event.getHook()
                        .sendMessage( "❌  **ERROR:** Something went wrong")
                        .queue();
            } finally {
                boolean success = false;
                if (photo != null && photo.exists())
                    success = photo.delete();
                // --- it's possible for the file to be locked by another process ---
                if(!success)
                    System.out.println("INFO: Could not delete photo.");
            }

        // --- /joke [category] [ephemeral] ---
        // --- displays a random joke ---
        } else if (command.equals("joke")) {
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
            event.reply(joke.jokeString())
                    .setEphemeral(ephemeral)
                    .queue();

        // --- /welcome ---
        // --- sends a welcome message ---
        } else if (command.equals("welcome")) {
            try {
                String userMention = Objects.requireNonNull(event.getMember())
                        .getUser()
                        .getAsMention();
                event.reply("Welcome, " + userMention + "!")
                        .queue();
            } catch (NullPointerException e) {
                System.out.println("ERROR: " + e.getMessage() + " (/welcome)");
                System.out.println("EXCEPTION THROWN: NullPointerException");

                event.reply("❌  **ERROR:** Cannot find user")
                        .queue();
            }

        // --- /roles ---
        // --- lists all the server's roles ---
        } else if (command.equals("roles")) {
            event.deferReply()
                    .queue();

            try {
                StringBuilder output = new StringBuilder("Roles: \n");
                List<Role> roleList = Objects.requireNonNull(event.getGuild())
                                                    .getRoles();
                for (Role role : roleList)
                    output.append(role.getAsMention())
                            .append("\n");

                event.getHook()
                        .sendMessage(output.toString())
                        .queue();
            } catch (NullPointerException e) {
                System.out.println("ERROR: " + e.getMessage() + " (/roles)");
                System.out.println("EXCEPTION THROWN: NullPointerException");

                event.getHook()
                        .sendMessage("❌  **ERROR:** Something went wrong")
                        .queue();
            }

        // --- /riddle [ephemeral] ---
        // --- displays a random riddle ---
        } else if (command.equals("riddle")) {
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

                event.reply(riddle + "\n\t\t" + "Answer: " + answer)
                        .setEphemeral(ephemeral)
                        .queue();
            } catch (IOException | InterruptedException e) {
                System.out.println("ERROR: " + e.getMessage() + " (/riddle)");
                if (e instanceof IOException)
                    System.out.println("EXCEPTION THROWN: IOException");
                else
                    System.out.println("EXCEPTION THROWN: InterruptedException");

                event.reply("❌  **ERROR:** Something went wrong")
                        .queue();
            }

        // --- /giverole <user> <role> ---
        // --- gives a role to a user ---
        } else if (command.equals("giverole")) {
            try {
                Member user = Objects.requireNonNull(event.getOption("user"))
                                                        .getAsMember();
                Role role = Objects.requireNonNull(event.getOption("role"))
                                                        .getAsRole();
                Objects.requireNonNull(event.getGuild())
                        .addRoleToMember(Objects.requireNonNull(user), role)
                        .queue();

                event.reply(user.getAsMention() + " has been given the "
                                + role.getAsMention() + " role.")
                        .queue();
            } catch (NullPointerException ignored) {

            }
        }
    }

    // --- converts the URL's content (string) to json ---
    private static JsonNode getJsonFromURL(@NotNull String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers
                                                                        .ofString());
        String responseBody = response.body();
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(responseBody, JsonNode.class);
    }
}