package bot;

import commands.CommandManager;
import commands.ContextMenuCommands;
import commands.SlashCommands;
import events.GuildEvents;
import events.MessageEvents;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import javax.security.auth.login.LoginException;

public class Bot {
    private static final Dotenv dotenv = Dotenv.configure().load();

    public Bot() throws LoginException, InvalidTokenException {
        // --- .env variable setup ---
        final String token = dotenv.get("TOKEN");

        // --- shard manager setup ---
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT,
                                GatewayIntent.GUILD_MEMBERS,
                                GatewayIntent.GUILD_PRESENCES)
                .addEventListeners(new MessageEvents(),
                                new GuildEvents(),
                                new CommandManager(),
                                new SlashCommands(),
                                new ContextMenuCommands())
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.listening("Darude - Sandstorm"))
                .build();
    }

    public static void main(String[] args) {
        try {
            new Bot();
        } catch(LoginException | InvalidTokenException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    public static String getDotenvVariable(String variable) {
        return dotenv.get(variable);
    }
}