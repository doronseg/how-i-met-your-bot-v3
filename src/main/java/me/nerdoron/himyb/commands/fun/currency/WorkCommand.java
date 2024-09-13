package me.nerdoron.himyb.commands.fun.currency;

import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.bot.Rng;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.fun.brocoins.CurrencyHelper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;

import java.sql.SQLException;

import static me.nerdoron.himyb.Global.*;
import static me.nerdoron.himyb.modules.bot.CooldownManager.commandID;
import static me.nerdoron.himyb.modules.fun.brocoins.CurrencyHelper.workEmbed;
import static me.nerdoron.himyb.modules.fun.brocoins.JailHelper.checkIfInJail;
import static me.nerdoron.himyb.modules.fun.brocoins.JailHelper.inJailEmbed;

public class WorkCommand extends SlashCommand {
    private static final Logger logger = LoggingHandler.logger(WorkCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (checkIfInJail(member)) {
            event.replyEmbeds(inJailEmbed(member)).queue();
            return;
        }
        if (COOLDOWN_MANAGER.hasCooldown(commandID(event))) {
            String remaining = COOLDOWN_MANAGER.parseCooldown(commandID(event));
            event.reply("Don't work too hard! You can work again in " + remaining + ".")
                    .setEphemeral(true)
                    .queue();
            return;
        }
        int reward = Rng.generateNumber(1, 15);
        int chance = Rng.generateNumber(1, 100);
        if (chance == 50) {
            event.replyEmbeds(CurrencyHelper.scammedEmbed).queue();
            COOLDOWN_MANAGER.addCooldown(commandID(event), 2 * HOUR_IN_SECONDS);
            assert member != null;
            logger.info("{}(ID:{}) tried to work and got scammed.", member.getEffectiveName(), member.getId());
            return;
        }
        try {
            if (chance == 1 || chance == 99) reward = reward * 3;
            BROCOINS_SQL.updateCash(member, reward);
            COOLDOWN_MANAGER.addCooldown(commandID(event), HOUR_IN_SECONDS);
            assert member != null;
            logger.info("{}(ID:{}) won {} while working.", member.getEffectiveName(), member.getId(), reward);
            event.replyEmbeds(workEmbed(reward)).queue();
        } catch (SQLException e) {
            assert member != null;
            logger.error("{}(ID:{}) Tried to work, but an error has occurred.", member.getEffectiveName(), member.getId());
            e.printStackTrace();
            event.reply("An error has occurred. Please try again").setEphemeral(true).queue();
        }
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("work", "Work an honest day's work and get some BroCoins as a reward!");
    }
}
