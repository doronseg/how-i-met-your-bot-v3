package me.nerdoron.himyb;

import io.github.cdimascio.dotenv.Dotenv;
import me.nerdoron.himyb.modules.bot.BotCommandsHandler;
import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.fun.brocoins.BroCoinsSQL;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;

import java.awt.*;

import static net.dv8tion.jda.api.entities.emoji.Emoji.fromCustom;

public class Global {
    public static final Color embedColor = Color.decode("#2f3136");
    public static final String botVersion = "v3.0.0";
    public static final String jdaVersion = "v5.1.0";
    public static final String botGithubLink = "https://github.com/doronseg/how-i-met-your-bot-v3";
    public static final String jdaGithubLink = "https://github.com/discord-jda/JDA/releases/tag/v5.1.0";
    public static final String footertext = "how i met your bot " + botVersion + " | developed by Doron";
    public static final String footerpfp = "https://media.discordapp.net/attachments/850432082738937896/901742492347691028/discord_bot_pfp.jpg";
    public static final CustomEmoji broCoin = fromCustom("brocoin", 997162208180064276L, false);
    public static final CooldownManager COOLDOWN_MANAGER = new CooldownManager();
    // times
    public static final int HOUR_IN_SECONDS = 3600;
    public static final int DAY_IN_SECONDS = HOUR_IN_SECONDS * 24;
    public static final int SECONDS_IN_MS = 1000;
    public static final int MIN_IN_MS = SECONDS_IN_MS * 60;
    public static final int HOUR_IN_MS = SECONDS_IN_MS * HOUR_IN_SECONDS;
    //public static final JailHandler JAIL_CHECKER = new JailHandler();
    public static final BroCoinsSQL BROCOINS_SQL = new BroCoinsSQL();
    public static BotCommandsHandler COMMANDS_HANDLER = new BotCommandsHandler();
    @SuppressWarnings("unused")
    public static Dotenv dotenvg;
}