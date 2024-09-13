package me.nerdoron.himyb.commands.fun.currency;

import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.bot.Rng;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.fun.brocoins.CurrencyHelper;
import me.nerdoron.himyb.modules.fun.brocoins.JailHelper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;

import java.sql.SQLException;

import static me.nerdoron.himyb.Global.BROCOINS_SQL;
import static me.nerdoron.himyb.modules.fun.brocoins.CurrencyHelper.successCrimeEmbed;

public class CrimeCommand extends SlashCommand {
    private static final Logger logger = LoggingHandler.logger(CrimeCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();

        if (JailHelper.checkIfInJail(member)) {
            event.replyEmbeds(JailHelper.inJailEmbed(member)).queue();
        }

        int chance = Rng.generateNumber(1, 300);
        int reward = Rng.generateNumber(20, 30);
        logger.info(String.valueOf(chance));
        // success
        if (chance % 3 == 0) {
            try {
                if (chance == 3 || chance == 99 || chance == 102 || chance == 199) reward = reward * 3;
                BROCOINS_SQL.updateCash(member, reward);
                //COOLDOWN_MANAGER.addCooldown(commandID(event), "Success", 3 * HOUR_IN_SECONDS);
                assert member != null;
                logger.info("{}(ID:{}) won {} while committing a crime.", member.getEffectiveName(), member.getId(), reward);
                event.replyEmbeds(successCrimeEmbed(reward)).queue();
            } catch (SQLException e) {
                assert member != null;
                logger.error("{}(ID:{}) Tried to commit a crime, but an error has occurred.", member.getEffectiveName(), member.getId());
                e.printStackTrace();
                event.reply("An error has occurred. Please try again").setEphemeral(true).queue();
            }
            return;
        } else if (chance < 60) {
            //COOLDOWN_MANAGER.addCooldown(commandID(event), "BackOff", 2 * HOUR_IN_SECONDS);
            event.replyEmbeds(CurrencyHelper.backOffCrimeCommand()).queue();
            assert member != null;
            logger.info("{}(ID:{}) tried to commit a crime, but the cops closed in on him.", member.getEffectiveName(), member.getId());
            return;
        }
        event.reply("Caught.").queue();


    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("crime", "Commit some criminal activities, and earn some good money.");
    }
}
