package me.nerdoron.himyb.commands.currency.batches;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.bot.Rng;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.Objects;

import static me.nerdoron.himyb.Global.BROCOINS_SQL;

public class DailyCommand extends SlashCommand {
    private static final Logger logger = LoggingHandler.logger(DailyCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        int reward = Rng.generateNumber(5, 15);
        if (Global.COOLDOWN_MANAGER.hasCooldown(CooldownManager.commandID(event))) {
            String remaining = Global.COOLDOWN_MANAGER.parseCooldown(CooldownManager.commandID(event));
            event.reply("A day hasn't passed since you last claimed your daily batch. " + remaining + " remaining.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        try {
            BROCOINS_SQL.updateCash(event.getMember(), reward);
            Global.COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), Global.DAY_IN_SECONDS);
            event.reply("You claimed your daily batch of coins, and got " + reward + " " + Global.broCoin.getAsMention()
                    + ".").setEphemeral(true).queue();
            logger.info("{}(ID:{}) got the daily batch of coins, ({} Coins", Objects.requireNonNull(event.getMember()).getUser().getName(), event.getMember().getId(), reward);
        } catch (SQLException e) {
            event.reply("Error!").setEphemeral(true).queue();
            e.printStackTrace();
        }

    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("daily", "Get a daily batch of BroCoins.");
    }
}
