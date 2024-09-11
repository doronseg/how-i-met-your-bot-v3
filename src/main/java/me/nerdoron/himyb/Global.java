package me.nerdoron.himyb;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import me.nerdoron.himyb.modules.bot.BotCommandsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import static net.dv8tion.jda.api.entities.emoji.Emoji.fromCustom;

public class Global {
    // global definitions
    public static CustomEmoji broCoin = fromCustom("brocoin", 997162208180064276L, false);
    public static final Color embedColor = Color.decode("#2f3136");
    public static BotCommandsHandler COMMANDS_HANDLER;
    public static final String footertext = "how i met your bot | developed by Doron & Goseale";
    public static final String footerpfp = "https://media.discordapp.net/attachments/850432082738937896/901742492347691028/discord_bot_pfp.jpg";
    public static Dotenv dotenvg;

    // times
    public static final int HOUR_IN_SECONDS = 3600;
    public static final int DAY_IN_SECONDS = HOUR_IN_SECONDS * 24;
    public static final int MONTH_IN_SECONDS = DAY_IN_SECONDS * 30;
    public static final int SECONDS_IN_MS = 1000;
    public static final int MIN_IN_MS = SECONDS_IN_MS * 60;
    public static final int HOUR_IN_MS = MIN_IN_MS * 60;

    // rng
    public static int generateNumber(int min, int max) {
        Random r = new Random();
        int high = max + 1;
        return r.nextInt(high - min) + min;
    }

    // get time format
    public static String getTimeStamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);

    }

    // logging handler
    public static String className(Class<?> clazz) {

        if (clazz.getName().contains("commands")) {
            String[] dots = clazz.getName().split("\\.");
            return getTimeStamp() + " COMMAND > " + dots[dots.length - 1];
        }
        if (clazz.getName().contains("modules.bot")) {
            String[] dots = clazz.getName().split("\\.");
            return getTimeStamp() + " CORE > " + dots[dots.length - 1];
        }

        if (clazz.getName().contains("Main")) {
            String[] dots = clazz.getName().split("\\.");
            return getTimeStamp() + " HIMYB > " + dots[dots.length - 1];
        }

        return clazz.getName();
    }

    // logger
    public static Logger logger(Class<?> clazz) {
        return LoggerFactory.getLogger(className(clazz));
    }

}