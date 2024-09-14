package me.nerdoron.himyb;


import io.github.cdimascio.dotenv.Dotenv;
import me.nerdoron.himyb.modules.bot.ChangeStatus;
import me.nerdoron.himyb.modules.bot.Database;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.bot.RegisterEvents;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;

public class Main {
    private static final Logger logger = LoggingHandler.logger(Main.class);

    public static void main(String[] args) {
        logger.info("Hello! You're running {}", Global.botVersion);
        logger.info("Starting stage 1 - setting up environment");
        setupEnv();
    }

    private static void setupEnv() {
        Dotenv dotenv = Dotenv.load();
        Global.dotenvg = dotenv;
        logger.info("Stage 1 complete!");
        logger.info("Starting stage 2 - bot login");
        try {
            Login(dotenv);
        } catch (InterruptedException ex) {
            logger.error("Exception occurred whilst trying to setup env file!\n", ex);
        }
    }

    public static void Login(Dotenv dotenv) throws InterruptedException {
        String token = dotenv.get("TOKEN");
        JDA jda = JDABuilder.createLight(token)
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MODERATION,
                        GatewayIntent.GUILD_MESSAGE_POLLS, GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                        GatewayIntent.DIRECT_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .build();
        logger.info("Logged in as {}.", jda.getSelfUser().getName());
        logger.info("Stage 2 complete!");
        RegisterEvents.registerEvents(jda);
        logger.info("Attempting to establish database connection..");
        tryDb();

        // initial status change
        ChangeStatus.changeActivity(jda);
        // Time to change status every 15-30 minutes:
        Thread.sleep(15 * Global.MIN_IN_MS);
        ChangeStatus.activityTimer(jda);
    }

    private static void tryDb() {
        Database.connect();
        logger.info("Database connection established!");
    }

}