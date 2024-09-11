package me.nerdoron.himyb;

import java.sql.SQLException;
import java.util.Arrays;

import me.nerdoron.himyb.modules.bot.ChangeStatus;
import me.nerdoron.himyb.modules.bot.Database;
import me.nerdoron.himyb.modules.bot.RegisterEvents;
import org.slf4j.Logger;

import io.github.cdimascio.dotenv.DotEnvException;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {
    private static final Logger logger = Global.logger(Main.class);

    public static void main(String[] args) {
        String version = "3.0.0 - September 2024";
        logger.info("Hello! You're running {}", version);
        logger.info("Starting stage 1 - setting up environment");
        try {
            setupEnv();
        } catch (DotEnvException ex) {
            logger.error("Exception occurred whilst trying to setup env file!\n", ex);
        }
    }

    private static void setupEnv() throws DotEnvException {
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
        logger.info("Attempting to establish database connection...");
        try {
            tryDb();
        } catch (ClassNotFoundException ex) {
            logger.error("An exception (ClassNotFound) occurred while trying to establish connection to the database!",
                    ex);
        } catch (SQLException ex) {
            logger.error("An exception occurred (SQLException) while trying to establish connection to the database!",
                    ex);
        }

        // initial status change
        ChangeStatus.changeActivity(jda);
        // Time to change status every 15-30 minutes:
        Thread.sleep(15 * Global.MIN_IN_MS);
        ChangeStatus.activityTimer(jda);
    }

    private static void tryDb() throws ClassNotFoundException, SQLException {
        Database.connect();
        logger.info("Database connection established!");
    }

}