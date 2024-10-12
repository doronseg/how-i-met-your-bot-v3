package me.nerdoron.himyb;

import io.github.cdimascio.dotenv.Dotenv;
import me.nerdoron.himyb.modules.bot.BotCommandsHandler;
import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.fun.brocoins.BroCoinsSQL;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;

import java.awt.*;
import java.time.Duration;

import static net.dv8tion.jda.api.entities.emoji.Emoji.fromCustom;

@SuppressWarnings("unused")
public class Global {
    public static final Color embedColor = Color.decode("#2f3136");
    public static final String botVersion = "v3.0.0";
    public static final String jdaVersion = "v5.1.1";
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
    // classes and definitions
    //public static final JailHandler JAIL_CHECKER = new JailHandler();
    public static final BroCoinsSQL BROCOINS_SQL = new BroCoinsSQL();
    public static final Duration TIMEOUT_DURATION = Duration.ofMinutes(5);
    // cards
    public static final CustomEmoji BACK_OF_CARD = fromCustom("back_of_card", 1289554822068179034L, false);
    public static final String[] CLUBS_CARDS = new String[]{"997163265601835090", "997163146118697082", "997163183745806376", "997163191756918894", "997163200271360021", "997163208475410462", "997163217883246633", "997163227391733770", "997163239114821662", "997163255258693732", "997163279686303844", "997169401725993122", "997163302562045962"};
    public static final String[] DIAMONDS_CARDS = new String[]{"997163268672077824", "997163148228448327", "997163185616453672", "997163193623400489", "997163202301411398", "997163210958438500", "997163219804242000", "997163230474551366", "997163241174204497", "997163257863340103", "997163286938259608", "997169417945366578", "997163304365608992"};
    public static final String[] HEARTS_CARDS = new String[]{"997163270840528946", "997163151579676723", "997163187482919093", "997163195506622605", "997163203932995727", "997163213227565196", "997163222052384788", "997163232567509032", "997163249210495006", "997163260522532925", "997163293443641434", "997169432277307452", "997163306622128280"};
    public static final String[] SPADES_CARDS = new String[]{"997163273151582258", "997163144218689616", "997163189441671278", "997163197436002305", "997163206143397918", "997163215337312326", "997163224304717834", "997163234677235762", "997163252528185374", "997163263768920124", "997163300393582743", "997163181434740846", "997163314318671901"};

    public static BotCommandsHandler COMMANDS_HANDLER = new BotCommandsHandler();
    public static Dotenv dotenvg;

}