package me.nerdoron.himyb.commands.useful;

import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.useful.timezones.TimezoneSQL;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class TimeCommand extends SlashCommand {
    private static final Logger logger = LoggingHandler.logger(TimeCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member target = Objects.requireNonNull(event.getInteraction().getOption("user")).getAsMember();

        TimezoneSQL timezoneSQL = new TimezoneSQL();
        try {
            assert target != null;
            if (!timezoneSQL.userHasTimezone(target.getId())) {
                event.reply(String.format("%s doesn't have a timezone set!", target.getAsMention())).setEphemeral(true).queue();
                return;
            }
            ZoneId tz = ZoneId.of(timezoneSQL.getUserTimezone(target.getId()));
            ZonedDateTime now = ZonedDateTime.now(tz);
            String time = now.format(DateTimeFormatter.ofPattern("HH:mm"));
            event.reply(String.format("%s's current time is **%s**.", target.getAsMention(), time)).queue();
        } catch (SQLException | DateTimeException ex) {
            event.reply("An error occurred while trying to get this user timezone.").queue();
            logger.error("Error occurred while trying to get a timezone: {}", ex.getMessage());
        }

    }

    @Override
    public SlashCommandData getSlash() {
        SlashCommandData time = Commands.slash("time", "Check a what time it is for a user");
        time.addOption(OptionType.USER, "user", "Which user?", true);
        return time;
    }
}
