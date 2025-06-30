package me.nerdoron.himyb.modules.fun.broofthemonth;

import me.nerdoron.himyb.modules.bot.Database;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class BroOfTheMonthSQL {

    private static final Logger logger = LoggingHandler.logger(BroOfTheMonthSQL.class);
    private static final Connection con = Database.connect();

    public void incrementZitch(String uid) {
        incrementUserStat(uid, "zitch");
    }

    public void incrementCrimes(String uid) {
        incrementUserStat(uid, "crimes");
    }

    public void incrementCommendation(String uid) {
        incrementUserStat(uid, "commendation");
    }

    public void addCoins(String uid, int amount) {
        addToUserStat(uid, "coins", amount);
    }

    public void addSpent(String uid, int amount) {
        addToUserStat(uid, "spent", amount);
    }

    public void addLost(String uid, int amount) {
        addToUserStat(uid, "lost", amount);
    }

    public int getZitch(String uid) {
        return getUserStat(uid, "zitch");
    }

    public int getCoins(String uid) {
        return getUserStat(uid, "coins");
    }

    public int getCrimes(String uid) {
        return getUserStat(uid, "crimes");
    }

    public int getSpent(String uid) {
        return getUserStat(uid, "spent");
    }

    public int getCommendation(String uid) {
        return getUserStat(uid, "commendation");
    }

    public int getLost(String uid) {
        return getUserStat(uid, "lost");
    }

    public void resetStats() {
        String SQL = "UPDATE botm SET zitch=0, coins=0, crimes=0, spent=0, commendation=0, lost=0";
        try {
            assert con != null;
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.executeUpdate();
            ps.close();
            logger.warn("All user stats have been reset to zero.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void incrementUserStat(String uid, String column) {
        ensureUserExists(uid);
        String SQL = "UPDATE botm SET " + column + " = " + column + " + 1 WHERE uid=?";
        try {
            assert con != null;
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, uid);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void addToUserStat(String uid, String column, int amount) {
        ensureUserExists(uid);
        String SQL = "UPDATE botm SET " + column + " = " + column + " + ? WHERE uid=?";
        try {
            assert con != null;
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, amount);
            ps.setString(2, uid);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int getUserStat(String uid, String column) {
        ensureUserExists(uid);
        String SQL = "SELECT " + column + " FROM botm WHERE uid=?";
        try {
            assert con != null;
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, uid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int value = rs.getInt(column);
                ps.close();
                return value;
            }
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    private void ensureUserExists(String uid) {
        if (!doesUserExist(uid)) {
            insertUser(uid);
        }
    }

    private boolean doesUserExist(String uid) {
        String SQL = "SELECT uid FROM botm WHERE uid=?";
        try {
            assert con != null;
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, uid);
            ResultSet rs = ps.executeQuery();
            boolean exists = rs.next();
            ps.close();
            return exists;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertUser(String uid) {
        String SQL = "INSERT INTO botm (uid, zitch, coins, crimes, spent, commendation, lost) VALUES (?, 0, 0, 0, 0, 0, 0)";
        try {
            assert con != null;
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, uid);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<LeaderboardEntry> getBroScoreLeaderboard() {
        List<LeaderboardEntry> leaderboard = new ArrayList<>();
        String SQL =
                "SELECT uid, " +
                        "((zitch*2) + crimes + (spent/100)) + " +
                        "(commendation * 5) + " +
                        "(coins / 50) - " +
                        "(lost / 100) AS total_points " +
                        "FROM botm " +
                        "ORDER BY total_points DESC LIMIT 10";

        try {
            assert con != null;
            PreparedStatement ps = con.prepareStatement(SQL);
            ResultSet rs = ps.executeQuery();
            int rank = 1;
            while (rs.next()) {
                if (rs.getInt("total_points") <= 0) {
                    continue;
                }
                LeaderboardEntry entry = new LeaderboardEntry(rs.getString("uid"), rs.getInt("total_points"));
                entry.rank = rank++;
                leaderboard.add(entry);
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return leaderboard;
    }

    public static class LeaderboardEntry {
        public final String uid;
        public final int totalPoints;
        public int rank;

        public LeaderboardEntry(String uid, int totalPoints) {
            this.uid = uid;
            this.totalPoints = totalPoints;
        }

        @Override
        public String toString() {
            return String.format("`%d.`  <@%s> | %d BroPoints.", rank, uid, totalPoints);
        }
    }
}