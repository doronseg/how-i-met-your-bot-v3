package me.nerdoron.himyb.commands.useful;

import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.useful.timezones.TimezoneSQL;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Objects;

public class SetTimezoneCommand extends SlashCommand {
    private static final Logger logger = LoggingHandler.logger(SetTimezoneCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String tz = Objects.requireNonNull(event.getOption("timezone")).getAsString();

        if (!ZoneId.getAvailableZoneIds().contains(tz)) {
            event.reply("Invalid timezone. Please try again.").setEphemeral(true).queue();
            return;
        }

        try {
            TimezoneSQL timezoneSQL = new TimezoneSQL();
            timezoneSQL.setUserTimezone(event.getUser().getId(), tz);
            event.reply(String.format("Your timezone has been set to %s", tz)).setEphemeral(true).queue();
        } catch (SQLException ex) {
            event.reply("Error. Try again later.").setEphemeral(true).queue();
            logger.error("Error occurred while trying to set a timezone: {}", ex.getMessage());
        }
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("settimezone", "Set your timezone")
                .addOptions(new OptionData(OptionType.STRING, "timezone", "Type a city or timezone", true).setAutoComplete(true));
    }
}
