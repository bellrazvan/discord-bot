import events.ChannelCreate;
import events.MessageEvents;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import javax.security.auth.login.LoginException;

public class Bot {
    //private final ShardManager shardManager;

    public Bot() throws LoginException {
        Dotenv dotenv = Dotenv.load();
        final String token = dotenv.get("TOKEN");
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new MessageEvents())
                .addEventListeners(new ChannelCreate())
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.listening("your problems"))
                .build();
//        shardManager = builder.build();
    }

    public static void main(String[] args) throws Exception  {
        try {
            Bot bot = new Bot();
        } catch(LoginException e) {
            e.printStackTrace();
        }
    }

//    public ShardManager getShardManager() {
//        return shardManager;
//    }
}