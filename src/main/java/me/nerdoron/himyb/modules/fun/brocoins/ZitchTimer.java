package me.nerdoron.himyb.modules.fun.brocoins;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.bot.Rng;
import me.nerdoron.himyb.modules.fun.broofthemonth.BroOfTheMonthSQL;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ZitchTimer {

    private static final Logger logger = LoggingHandler.logger(ZitchTimer.class);
    private static final String GUILD_ID = "850396197646106624";
    private static final String CHANNEL_ID = "850437485687078932";
    private final JDA jda;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final BroCoinsSQL broCoinsSQL = new BroCoinsSQL();
    private final AtomicBoolean waitingForResponse = new AtomicBoolean(false);

    public ZitchTimer(JDA jda) {
        this.jda = jda;
    }

    public void start() {
        scheduleNext();
    }

    private void scheduleNext() {
        if (waitingForResponse.get()) {
            logger.info("Still waiting for a response. Skipping scheduling the next zitch dog.");
            return;
        }

        long delay = Rng.generateNumber(Global.HOUR_IN_SECONDS / 2, Global.HOUR_IN_SECONDS * 3);
        scheduler.schedule(this::sendZitchDog, delay, TimeUnit.SECONDS);
    }

    private void sendZitchDog() {
        try {
            Guild guild = jda.getGuildById(GUILD_ID);
            if (guild == null) {
                logger.error("Guild not found: {}", GUILD_ID);
                return;
            }

            TextChannel channel = guild.getTextChannelById(CHANNEL_ID);
            if (channel == null) {
                logger.error("Channel not found: {}", CHANNEL_ID);
                return;
            }

            channel.sendMessage("\uD83D\uDC36").queue(message -> {
                waitingForResponse.set(true);
                eventHandler(message);
            });

        } catch (Exception e) {
            logger.error("Error in sendZitchDog: ", e);
        }
    }

    private void eventHandler(Message message) {
        Guild guild = jda.getGuildById(GUILD_ID);
        if (guild == null) {
            logger.error("Guild not found in event handler: {}", GUILD_ID);
            return;
        }

        TextChannel channel = guild.getTextChannelById(CHANNEL_ID);
        if (channel == null) {
            logger.error("Channel not found in event handler: {}", CHANNEL_ID);
            return;
        }

        jda.listenOnce(MessageReceivedEvent.class)
                .filter(event -> event.getChannel().equals(channel))
                .filter(event -> event.getMessage().getContentRaw().equalsIgnoreCase("zitch dog"))
                .timeout(Duration.ofMinutes(10), () -> {
                    message.delete().queue();
                    waitingForResponse.set(false);
                })
                .subscribe(event -> {
                    Member member = event.getMember();
                    if (member == null) {
                        logger.error("Member is null in event handler.");
                        return;
                    }

                    int reward = Rng.generateNumber(3, 10);
                    try {
                        broCoinsSQL.updateCashMultiplierDM(member, reward);
                        message.editMessage(String.format("Zitch dog! %s won %d %s. Your new cash balance is %d %s.", member.getAsMention(), reward, Global.broCoin.getAsMention(), broCoinsSQL.getBroCash(member), Global.broCoin.getAsMention())).queue();
                        logger.info("{}(ID:{}) won zitch dog and received {} coins.", member.getUser().getName(), member.getId(), reward);
                        BroOfTheMonthSQL broOfTheMonthSQL = new BroOfTheMonthSQL();
                        broOfTheMonthSQL.incrementZitch(member.getId());
                        waitingForResponse.set(false);
                        scheduleNext();
                    } catch (SQLException e) {
                        message.editMessage("Uh oh, there's been a DB error.").queue();
                        logger.error("Database error: ", e);
                    }
                });
    }
}
