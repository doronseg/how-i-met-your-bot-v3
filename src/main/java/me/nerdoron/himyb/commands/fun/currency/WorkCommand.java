package me.nerdoron.himyb.commands.fun.currency;

import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.bot.Rng;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.fun.brocoins.CurrencyHelper;
import me.nerdoron.himyb.modules.fun.brocoins.JailHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;

import java.sql.SQLException;

import static me.nerdoron.himyb.Global.BROCOINS_SQL;
import static me.nerdoron.himyb.Global.COOLDOWN_MANAGER;

public class WorkCommand extends SlashCommand {
    private static final Logger logger = LoggingHandler.logger(WorkCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (JailHandler.checkIfInJail(member)) {
            event.replyEmbeds(JailHandler.inJailEmbed(member)).queue();
            return;
        }
        if (COOLDOWN_MANAGER.hasCooldown(CooldownManager.commandID(event))) {
            String remaining = COOLDOWN_MANAGER.parseCooldown(CooldownManager.commandID(event));
            event.reply("Don't work too hard! You can work again in " + remaining + ".")
                    .setEphemeral(true)
                    .queue();
            return;
        }
        int coinsNow = BROCOINS_SQL.getBroCash(member);
        int reward = Rng.generateNumber(1, 15);
        int chance = Rng.generateNumber(1, 100);
        if (chance == 50) {
            event.replyEmbeds(CurrencyHelper.scammedEmbed).queue();
            COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), 30);
            assert member != null;
            logger.info("{} tried to work and got scammed.", member.getEffectiveName());
            return;
        }
        try {
            if (chance == 1 || chance == 99) reward = reward * 3;
            BROCOINS_SQL.updateCash(member, reward);
            COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), 30);
            assert member != null;
            logger.info("{} won {} while working.", member.getEffectiveName(), reward);
            event.replyEmbeds(CurrencyHelper.workEmbed(reward)).queue();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("work", "Work an honest day's work and get some BroCoins as a reward!");
    }
}
