package me.nerdoron.himyb.modules.useful.birthdays;

import me.nerdoron.himyb.modules.bot.Database;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class BirthdayModalHandler extends ListenerAdapter {
    private static final Logger logger = LoggingHandler.logger(BirthdayChecks.class);

    int getDaysInMonth(int month) {
        switch (month) {
            case 1: // January
                return 31;
            case 2: // February
                return 28;
            case 3: // March
                return 31;
            case 4: // April
                return 30;
            case 5: // May
                return 31;
            case 6: // June
                return 30;
            case 7: // July
                return 31;
            case 8: // August
                return 31;
            case 9: // September
                return 30;
            case 10: // October
                return 31;
            case 11: // November
                return 30;
            case 12: // December
                return 31;
            default:
                return 0;
        }
    }

    String parseMonth(int month) {
        switch (month) {
            case 1: // January
                return "January";
            case 2: // February
                return "February";
            case 3: // March
                return "March";
            case 4: // April
                return "April";
            case 5: // May
                return "May";
            case 6: // June
                return "June";
            case 7: // July
                return "July";
            case 8: // August
                return "August";
            case 9: // September
                return "September";
            case 10: // October
                return "October";
            case 11: // November
                return "November";
            case 12: // December
                return "December";
            default:
                return "Error";
        }
    }

    public String parseDay(int day) {
        switch (day) {
            case 1:
                return "1st";
            case 2:
                return "2nd";
            case 3:
                return "3rd";
            case 21:
                return "21st";
            case 22:
                return "22nd";
            case 23:
                return "23rd";
            case 31:
                return "31st";
            default:
                return day + "th";
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (!event.getModalId().equals("birthday-modal")) return;

        int dayOfBirth = Integer.parseInt(Objects.requireNonNull(event.getValue("birthday-day")).getAsString());
        int monthOfBirth = Integer.parseInt(Objects.requireNonNull(event.getValue("birthday-month")).getAsString());

        if (monthOfBirth < 1 || monthOfBirth > 12) {
            event.deferReply().setEphemeral(true).setContent("Invalid month! Please enter a month 1-12").queue();
            return;
        }
        if (monthOfBirth == 2 && dayOfBirth == 29) {
            dayOfBirth = 28;
        }
        if (dayOfBirth < 1 || dayOfBirth > getDaysInMonth(monthOfBirth)) {

            event.deferReply().setEphemeral(true).setContent(String.format("Invalid day! %s only has %d days.", parseMonth(monthOfBirth), getDaysInMonth(monthOfBirth))).queue();
            return;
        }

        Connection con = Database.connect();

        String statement = "insert into birthday (uid, day, month) values(?,?,?)";
        try {
            assert con != null;
            PreparedStatement ps = con.prepareStatement(statement);
            ps.setString(1, event.getUser().getId());
            ps.setInt(2, dayOfBirth);
            ps.setInt(3, monthOfBirth);
            ps.execute();
            logger.info("{}(ID:{}) Set a birthday - {}/{}", event.getUser().getGlobalName(), event.getUser().getId(), dayOfBirth, monthOfBirth);
            event.deferReply().setEphemeral(true).setContent(String.format("Set your birthday to %s %s.", parseMonth(monthOfBirth), parseDay(dayOfBirth))).queue();
        } catch (SQLException ex) {
            event.deferReply().setEphemeral(true).setContent("Error!").queue();
            logger.info("{}(ID:{}) tried to set a birthday, but an error occurred", event.getUser().getGlobalName(), event.getUser().getId());
            ex.printStackTrace();
        }
    }
}
