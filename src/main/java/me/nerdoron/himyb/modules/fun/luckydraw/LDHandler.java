package me.nerdoron.himyb.modules.fun.luckydraw;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.bot.Rng;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static me.nerdoron.himyb.Global.BROCOINS_SQL;
import static me.nerdoron.himyb.Global.COOLDOWN_MANAGER;


public class LDHandler {
    private static final Logger logger = LoggingHandler.logger(LDHandler.class);


    public void LDGameHandler(SlashCommandInteractionEvent event, int bet, int number) throws SQLException {
        Member member = event.getMember();
        assert member != null;

        int no1 = Rng.generateNumber(0, 9);
        int no2 = Rng.generateNumber(0, 9);
        int no3 = Rng.generateNumber(0, 9);

        event.replyEmbeds(LDHelper.ldMain(bet, number)).queue();

        boolean win = false;

        if (no1 == Character.getNumericValue(Integer.toString(number).charAt(0))) {
            event.getHook().editOriginalEmbeds(LDHelper.stageTwo(bet, number, no1, no2)).queueAfter(4, TimeUnit.SECONDS);
            if (no2 == Character.getNumericValue(Integer.toString(number).charAt(1))) {
                if (no3 == Character.getNumericValue(Integer.toString(number).charAt(2))) {
                    event.getHook().editOriginalEmbeds(LDHelper.stageThreeWin(bet, number, no1, no2, no3)).queueAfter(6, TimeUnit.SECONDS);
                    win = true;
                } else {
                    event.getHook().editOriginalEmbeds(LDHelper.stageThreeLose(bet, number, no1, no2, no3)).queueAfter(6, TimeUnit.SECONDS);
                }
            } else {
                event.getHook().editOriginalEmbeds(LDHelper.stageTwoLose(bet, number, no1, no2)).queueAfter(4, TimeUnit.SECONDS);
            }
        } else {
            event.getHook().editOriginalEmbeds(LDHelper.stageOneLose(bet, number, no1)).queueAfter(2, TimeUnit.SECONDS);
        }

        if (win) {
            BROCOINS_SQL.updateCashMultiplier(member, event, 99 * bet);
            COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), 2 * Global.HOUR_IN_SECONDS);
            logger.info("{}(ID:{}) won a Lucky Draw while betting {}.", event.getUser().getAsTag(), event.getMember().getId(), bet);
        } else {
            BROCOINS_SQL.updateCashWithoutMultiplier(event.getMember(), -bet);
            logger.info("{}(ID:{}) lost a Lucky Draw while betting {}.", event.getUser().getAsTag(), event.getMember().getId(), bet);
            COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), 30);
        }
    }


}
