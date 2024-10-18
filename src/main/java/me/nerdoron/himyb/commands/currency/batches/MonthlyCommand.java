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

public class MonthlyCommand extends SlashCommand {
    private static final Logger logger = LoggingHandler.logger(WeeklyCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        int reward = Rng.generateNumber(100, 300);
        if (Global.COOLDOWN_MANAGER.hasCooldown(CooldownManager.commandID(event))) {
            String remaining = Global.COOLDOWN_MANAGER.parseCooldown(CooldownManager.commandID(event));
            event.reply("A month hasn't passed since you last claimed your monthly batch. " + remaining + " remaining.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        try {
            BROCOINS_SQL.updateCashMultiplierDM(event.getMember(), reward);
            Global.COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), 30 * Global.DAY_IN_SECONDS);
            event.reply("You claimed your monthly batch of coins, and got " + reward + " " + Global.broCoin.getAsMention()
                    + ".").setEphemeral(true).queue();
            logger.info("{}(ID:{}) got the monthly batch of coins, ({} Coins", Objects.requireNonNull(event.getMember()).getUser().getName(), event.getMember().getId(), reward);
        } catch (SQLException e) {
            event.reply("Error!").setEphemeral(true).queue();
            e.printStackTrace();
        }

    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("monthly", "Get a monthly batch of BroCoins.");
    }
}
