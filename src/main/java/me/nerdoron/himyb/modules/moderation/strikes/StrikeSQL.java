package me.nerdoron.himyb.modules.moderation.strikes;

import me.nerdoron.himyb.modules.bot.Database;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import net.dv8tion.jda.api.entities.Member;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StrikeSQL {

    private static final Logger logger = LoggingHandler.logger(StrikeSQL.class);
    private static final Connection con = Database.connect();

    public static int getStrikes(Member member) {
        assert con != null;
        String SQL = "SELECT amount FROM strikes WHERE uid=?";
        try (PreparedStatement ps = con.prepareStatement(SQL)) {
            ps.setString(1, member.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("amount");
                }
            }
        } catch (SQLException e) {
            logger.error(e.toString());
        }
        return 0;
    }

    public static boolean addStrike(Member member, int amount) {
        assert con != null;
        String SQL = "INSERT INTO strikes (uid, amount) VALUES (?, ?) " +
                "ON CONFLICT(uid) DO UPDATE SET amount = amount + ?";
        try (PreparedStatement ps = con.prepareStatement(SQL)) {
            ps.setString(1, member.getId());
            ps.setInt(2, amount);
            ps.setInt(3, amount);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.error(e.toString());
            return false;
        }
    }

    public static boolean removeStrike(Member member, int amount) {
        assert con != null;
        String getStrikesSQL = "SELECT amount FROM strikes WHERE uid = ?";
        try (PreparedStatement ps = con.prepareStatement(getStrikesSQL)) {
            ps.setString(1, member.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int currentStrikes = rs.getInt("amount");
                    int newAmount = Math.max(0, currentStrikes - amount);
                    String updateStrikesSQL = "UPDATE strikes SET amount = ? WHERE uid = ?";
                    try (PreparedStatement updatePs = con.prepareStatement(updateStrikesSQL)) {
                        updatePs.setInt(1, newAmount);
                        updatePs.setString(2, member.getId());
                        updatePs.executeUpdate();
                        return true;
                    }
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            logger.error(e.toString());
            return false;
        }
    }


}
