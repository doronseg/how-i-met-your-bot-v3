package me.nerdoron.himyb.modules.useful.timezones;

import me.nerdoron.himyb.modules.bot.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TimezoneSQL {

    private static final Connection con = Database.connect();

    public void setUserTimezone(String uid, String timezone) throws SQLException {
        assert con != null;
        if (!userHasTimezone(uid)) {
            String SQL = "INSERT INTO timezones(uid,tz) VALUES(?,?)";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, uid);
            ps.setString(2, timezone);
            ps.execute();
            ps.close();
        } else {
            String SQL = "UPDATE timezones SET tz=? WHERE uid=?";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, timezone);
            ps.setString(2, uid);
            ps.execute();
            ps.close();
        }
    }

    public String getUserTimezone(String uid) throws SQLException {
        assert con != null;
        if (!userHasTimezone(uid)) return "error";
        String timezone;
        String SQL = "SELECT tz FROM timezones WHERE uid=?";
        PreparedStatement ps = con.prepareStatement(SQL);
        ps.setString(1, uid);
        ResultSet rs = ps.executeQuery();
        timezone = rs.getString(1);
        rs.close();
        ps.close();
        return timezone;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean userHasTimezone(String uid) {
        String SQL = "SELECT uid from timezones WHERE uid=?";
        assert con != null;
        try {
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, uid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ps.close();
                return true;
            } else {
                ps.close();
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
