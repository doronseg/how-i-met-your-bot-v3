package me.nerdoron.himyb.modules.useful.birthdays;

import me.nerdoron.himyb.modules.bot.Database;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BirthdayChecks {
    private static final Logger logger = LoggingHandler.logger(BirthdayChecks.class);
    Connection con = Database.connect();

    public boolean hasBirthday(String uid) {
        try {
            String SQL = "SELECT month FROM birthday WHERE uid=?";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, uid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ps.close();
                return true;
            }
            ps.close();
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<String> getBirthdays(int day, int month) {
        ArrayList<String> users = new ArrayList<>();
        try {
            String SQL = "select uid FROM birthday WHERE day=? AND month=?";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, day + "");
            ps.setString(2, month + "");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(rs.getString(1));
            }
            ps.close();
        } catch (SQLException ex) {
            logger.error("Error while trying to find someone in the Birthday database!");
            ex.printStackTrace();
        }
        return users;
    }
}