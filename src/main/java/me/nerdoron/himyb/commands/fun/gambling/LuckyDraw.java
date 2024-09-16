package me.nerdoron.himyb.commands.fun.gambling;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.bot.Rng;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.Objects;

import static me.nerdoron.himyb.Global.BROCOINS_SQL;
import static me.nerdoron.himyb.Global.COOLDOWN_MANAGER;

public class LuckyDraw extends SlashCommand {

    private static final Logger logger = LoggingHandler.logger(LuckyDraw.class);

    @SuppressWarnings("LoggingSimilarMessage")
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        assert member != null;
        if (COOLDOWN_MANAGER.hasCooldown(CooldownManager.commandID(event))) {
            String time = COOLDOWN_MANAGER.parseCooldown(CooldownManager.commandID(event));
            event.reply("Chill out bucko! I know it's tempting... " + time).setEphemeral(true)
                    .queue();
            return;
        }
        int bet = Objects.requireNonNull(event.getOption("bet")).getAsInt();
        int number = Objects.requireNonNull(event.getOption("number")).getAsInt();
        if (bet > BROCOINS_SQL.getBroCash(event.getMember())) {
            event.reply("You don't have enough cash!").setEphemeral(true).queue();
            return;
        }

        int rand = Rng.generateNumber(1, 100);
        if (doesWin(rand, number)) {
            // win
            try {
                BROCOINS_SQL.updateCash(event.getMember(), 14 * bet);
                event.reply(String.format("You bet %d %s on %d and won %d %s!", bet, Global.broCoin.getAsMention(), number, bet * 15, Global.broCoin.getAsMention())).queue();
                logger.info("{}(ID:{}) won a Lucky Draw while betting {}.", event.getUser().getAsTag(), event.getMember().getId(), bet);
                COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), 30);
            } catch (SQLException e) {
                logger.error("{}(ID:{}) Tried to bet on Lucky Draw, but an error has occurred.", member.getUser().getName(), member.getId());
                e.printStackTrace();
                event.reply("An error has occurred. Please try again").setEphemeral(true).queue();
            }
        } else {
            // lose
            try {
                BROCOINS_SQL.updateCash(event.getMember(), -bet);
                event.reply(String.format("You bet %d %s on %d and lost. What a shame!", bet, Global.broCoin.getAsMention(), number)).queue();
                logger.info("{}(ID:{}) lost a Lucky Draw while betting {}.", event.getUser().getAsTag(), event.getMember().getId(), bet);
                COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), 30);
            } catch (SQLException e) {
                logger.error("{}(ID:{}) Tried to bet on Lucky Draw, but an error has occurred.", member.getUser().getName(), member.getId());
                e.printStackTrace();
                event.reply("An error has occurred. Please try again").setEphemeral(true).queue();
            }

        }
    }


    @Override
    public SlashCommandData getSlash() {
        SlashCommandData ld = Commands.slash("luckydraw",
                "Bet on a game of Lucky Draw. Ultimate High risk, high reward.");
        OptionData bet = new OptionData(OptionType.INTEGER, "bet", "How much do you want to bet?", true);
        OptionData number = new OptionData(OptionType.INTEGER, "number",
                "Which number would you like to place your bet on?", true);
        bet.setMinValue(15);
        number.setMinValue(1);
        number.setMaxValue(100);
        ld.addOptions(bet, number);
        return ld;
    }

    private Boolean doesWin(int rand, int num) {
        return rand == num;
    }

}
