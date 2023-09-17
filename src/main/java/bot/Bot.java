package bot;

import commands.SlashCommands;
import events.ChannelCreate;
import events.GuildEvents;
import events.MessageEvents;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Bot {
    private static final Dotenv dotenv = Dotenv.configure().load();
    private final ShardManager shardManager;

    public Bot() throws LoginException, IOException, InterruptedException {
        // --- .env variables setup ---
        final String token = dotenv.get("TOKEN");

        // --- shard manager setup ---
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT,
                                GatewayIntent.GUILD_MEMBERS,
                                GatewayIntent.GUILD_PRESENCES)
                .addEventListeners(new MessageEvents(),
                                new ChannelCreate(),
                                new GuildEvents(),
                                new SlashCommands())
//                .setMemberCachePolicy(MemberCachePolicy.ALL)
//                .setChunkingFilter(ChunkingFilter.ALL)
//                .enableCache(CacheFlag.ONLINE_STATUS)
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.listening("your problems"));
        shardManager = builder.build();
    }

    public static void main(String[] args) throws Exception  {
        try {
            Bot bot = new Bot();
        } catch(LoginException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public static Dotenv getDotenv() {
        return dotenv;
    }
}