package me.nerdoron.himyb.commands.fun.gambling;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.bot.Rng;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.fun.brocoins.JailHelper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static me.nerdoron.himyb.Global.BROCOINS_SQL;
import static me.nerdoron.himyb.Global.COOLDOWN_MANAGER;

public class RussianRoulette extends SlashCommand {
    private static final Logger logger = LoggingHandler.logger(RussianRoulette.class);

    @SuppressWarnings("LoggingSimilarMessage")
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        assert member != null;
        if (JailHelper.checkIfInJail(member)) {
            event.replyEmbeds(JailHelper.inJailEmbed(member)).queue();
            return;
        }
        if (COOLDOWN_MANAGER.hasCooldown(CooldownManager.commandID(event))) {
            String time = COOLDOWN_MANAGER.parseCooldown(CooldownManager.commandID(event));
            event.reply("You have already bet on Russian Roulette. Please try again in " + time).setEphemeral(true)
                    .queue();
            return;
        }
        int bet = Objects.requireNonNull(event.getOption("bet")).getAsInt();
        int number = Objects.requireNonNull(event.getOption("number")).getAsInt();
        if (bet > BROCOINS_SQL.getBroCash(event.getMember())) {
            event.reply("You don't have enough cash!").setEphemeral(true).queue();
            return;
        }

        int rand = Rng.generateNumber(1, 6);
        File video = getRandomRussianRoulette(rand);
        FileUpload file = FileUpload.fromData(video);
        event.reply(
                        "You bet " + bet + " " + Global.broCoin.getAsMention() + " on " + number + ". Let's see if you won..")
                .addFiles(file)
                .queue();

        if (doesWin(rand, number)) {
            // win
            try {
                BROCOINS_SQL.updateCash(event.getMember(), 2 * bet);
                event.getHook().editOriginal(String.format("You bet %d %s on %d and won %d %s!", bet, Global.broCoin.getAsMention(), number, bet * 3, Global.broCoin.getAsMention())).queueAfter(10, TimeUnit.SECONDS);
                logger.info("{}(ID:{}) won a russian roulette while betting {}.", event.getUser().getAsTag(), event.getMember().getId(), bet);
                COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), Global.HOUR_IN_SECONDS / 2);
            } catch (SQLException e) {
                logger.error("{}(ID:{}) Tried to bet on russian roulette, but an error has occurred.", member.getUser().getName(), member.getId());
                e.printStackTrace();
                event.reply("An error has occurred. Please try again").setEphemeral(true).queue();
            }
        } else {
            // lose
            try {
                BROCOINS_SQL.updateCash(event.getMember(), -bet);
                event.getHook().editOriginal(String.format("You bet %d %s on %d and lost. What a shame!", bet, Global.broCoin.getAsMention(), number)).queueAfter(10, TimeUnit.SECONDS);
                logger.info("{}(ID:{}) lost a russian roulette while betting {}.", event.getUser().getAsTag(), event.getMember().getId(), bet);
                COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), Global.HOUR_IN_SECONDS / 2);
            } catch (SQLException e) {
                logger.error("{}(ID:{}) Tried to bet on russian roulette, but an error has occurred.", member.getUser().getName(), member.getId());
                e.printStackTrace();
                event.reply("An error has occurred. Please try again").setEphemeral(true).queue();
            }
        }


    }

    @Override
    public SlashCommandData getSlash() {
        SlashCommandData rr = Commands.slash("russianroulette",
                "Bet on a game of Russian Roulette. High risk, high reward");
        OptionData bet = new OptionData(OptionType.INTEGER, "bet", "How much do you want to bet?", true);
        OptionData number = new OptionData(OptionType.INTEGER, "number",
                "Which number would you like to place your bet on?", true);
        bet.setMinValue(15);
        number.setMinValue(1);
        number.setMaxValue(6);
        rr.addOptions(bet, number);
        return rr;
    }

    private Boolean doesWin(int rand, int num) {
        return rand == num;
    }


    private File getRandomRussianRoulette(int num) {
        ArrayList<File> files = new ArrayList<>();
        files.add(new File("videos/Russian1.mp4"));
        files.add(new File("videos/Russian2.mp4"));
        files.add(new File("videos/Russian3.mp4"));
        files.add(new File("videos/Russian4.mp4"));
        files.add(new File("videos/Russian5.mp4"));
        files.add(new File("videos/Russian6.mp4"));
        return files.get(num - 1);
    }
}
