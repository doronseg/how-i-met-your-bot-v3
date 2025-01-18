package me.nerdoron.himyb.modules.fun.broofthemonth;

import me.nerdoron.himyb.modules.bot.Database;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BroOfTheMonthSQL {

    private static final Logger logger = LoggingHandler.logger(BroOfTheMonthSQL.class);
    private static final Connection con = Database.connect();

    static {
        try {
            assert con != null;
            try (PreparedStatement ps = con.prepareStatement("PRAGMA journal_mode=WAL;")) {
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to enable WAL mode", e);
        }
    }

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

    public void resetStats() {
        String SQL = "UPDATE botm SET zitch=0, coins=0, crimes=0, spent=0, commendation=0";
        executeWithRetry(SQL, ps -> {
        });
        logger.warn("All user stats have been reset to zero.");
    }

    private void incrementUserStat(String uid, String column) {
        ensureUserExists(uid);
        String SQL = "UPDATE botm SET " + column + " = " + column + " + 1 WHERE uid=?";
        executeWithRetry(SQL, ps -> ps.setString(1, uid));
    }

    private void addToUserStat(String uid, String column, int amount) {
        ensureUserExists(uid);
        String SQL = "UPDATE botm SET " + column + " = " + column + " + ? WHERE uid=?";
        executeWithRetry(SQL, ps -> {
            ps.setInt(1, amount);
            ps.setString(2, uid);
        });
    }

    private int getUserStat(String uid, String column) {
        ensureUserExists(uid);
        String SQL = "SELECT " + column + " FROM botm WHERE uid=?";
        try {
            assert con != null;
            try (PreparedStatement ps = con.prepareStatement(SQL)) {
                ps.setString(1, uid);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getInt(column) : 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
            try (PreparedStatement ps = con.prepareStatement(SQL)) {
                ps.setString(1, uid);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertUser(String uid) {
        String SQL = "INSERT INTO botm (uid, zitch, coins, crimes, spent, commendation) VALUES (?, 0, 0, 0, 0, 0)";
        executeWithRetry(SQL, ps -> ps.setString(1, uid));
    }

    private void executeWithRetry(String sql, StatementPopulator populator) {
        int retries = 5;
        while (retries > 0) {
            try {
                assert con != null;
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    populator.populate(ps);
                    ps.executeUpdate();
                    return;
                }
            } catch (SQLException e) {
                if ("SQLITE_BUSY" .equals(e.getSQLState())) {
                    retries--;
                    try {
                        Thread.sleep(100); // Small delay before retry
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new RuntimeException("Database operation failed after retries.");
    }

    @FunctionalInterface
    private interface StatementPopulator {
        void populate(PreparedStatement ps) throws SQLException;
    }
}
