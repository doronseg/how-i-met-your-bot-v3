package me.nerdoron.himyb.modules.fun.brocoins;

import me.nerdoron.himyb.modules.bot.Database;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import net.dv8tion.jda.api.entities.Member;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class BroCoinsSQL {
    private static final Logger logger = LoggingHandler.logger(BroCoinsSQL.class);
    private static final Connection con = Database.connect();
    private static PreparedStatement ps;

    public boolean hasBroCoins(Member member) {
        String SQL = "SELECT uid FROM brocoins WHERE UID=?";
        try {
            assert con != null;
            ps = con.prepareStatement(SQL);
            ps.setString(1, member.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ps.close();
                return true;
            } else {
                ps.close();
                return false;
            }
        } catch (Exception ex) {
            logger.error(ex.toString());
        }
        return false;
    }

    public boolean hasAccount(Member member) {
        String SQL = "SELECT uid FROM brocoins WHERE UID=?";
        try {
            assert con != null;
            ps = con.prepareStatement(SQL);
            ps.setString(1, member.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ps.close();
                return true;
            } else {
                ps.close();
                return false;
            }
        } catch (Exception ex) {
            logger.error(ex.toString());
        }
        return false;
    }

    public int getBroCash(Member member) {
        if (!hasAccount(member))
            return 0;
        int brocoins;
        try {
            assert con != null;
            String SQL = "select cash FROM brocoins WHERE uid=?";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, member.getId());
            ResultSet rs = ps.executeQuery();
            brocoins = rs.getInt(1);
            ps.close();
        } catch (SQLException e) {
            logger.error("Error while attempting to retrieve {}'s cash balance from the DB ", member.getEffectiveName());
            e.printStackTrace();
            return 0;
        }
        return brocoins;
    }

    public int getBroBank(Member member) {
        if (!hasAccount(member))
            return 0;
        int brocoins;
        try {
            assert con != null;
            String SQL = "select cash FROM brocoins WHERE uid=?";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, member.getId());
            ResultSet rs = ps.executeQuery();
            brocoins = rs.getInt(1);
            ps.close();
        } catch (SQLException e) {
            logger.error("Error while attempting to retrieve {}'s bank balance from the DB ", member.getEffectiveName());
            e.printStackTrace();
            return 0;
        }
        return brocoins;
    }

    public Map<String, Integer> getBroCash() {
        Map<String, Integer> result = new HashMap<>();
        try {
            assert con != null;
            String SQL = "SELECT * from brocoins";
            PreparedStatement ps = con.prepareStatement(SQL);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String userID = rs.getString(1);
                int userBroCoins = rs.getInt(2);
                result.put(userID, userBroCoins);
            }
            ps.close();
        } catch (SQLException e) {
            logger.error("Error while attempting to retrieve an unknown user's brocoins from the DB ");
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public void setBank(Member member, int newAmount) throws SQLException {
        if (hasAccount(member)) {
            assert con != null;
            String SQL = "UPDATE brocoins SET bank = ? WHERE uid = ?";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, newAmount);
            ps.setString(2, member.getId());
            ps.execute();
            ps.close();
        } else {
            String SQL = "INSERT into brocoins (uid, cash, bank) values(?,0,?)";
            assert con != null;
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, member.getId());
            ps.setInt(2, newAmount);
            ps.execute();
            ps.close();
        }
    }

    public void updateBank(Member member, int amountToChange) throws SQLException {
        int memberCoins = this.getBroBank(member);
        memberCoins += amountToChange;
        setBank(member, memberCoins);
    }

    public void setCash(Member member, int newAmount) throws SQLException {
        if (hasAccount(member)) {
            assert con != null;
            String SQL = "UPDATE brocoins SET cash = ? WHERE uid = ?";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setInt(1, newAmount);
            ps.setString(2, member.getId());
            ps.execute();
            ps.close();
        } else {
            String SQL = "INSERT into brocoins (uid, cash, bank) values(?,?,0)";
            assert con != null;
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, member.getId());
            ps.setInt(2, newAmount);
            ps.execute();
            ps.close();
        }
    }

    public void updateCash(Member member, int amountToChange) throws SQLException {
        int memberCoins = this.getBroCash(member);
        memberCoins += amountToChange;
        setCash(member, memberCoins);
    }
}
