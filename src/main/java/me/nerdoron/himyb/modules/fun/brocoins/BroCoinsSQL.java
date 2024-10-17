package me.nerdoron.himyb.modules.fun.brocoins;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.bot.Database;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
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

    public boolean hasAccount(Member member) {
        String SQL = "SELECT uid FROM brocoins WHERE UID=?";
        try {
            assert con != null;
            PreparedStatement ps = con.prepareStatement(SQL);
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
            logger.error("Error while attempting to retrieve {}'s cash balance from the DB ", member.getUser().getName());
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
            String SQL = "select bank FROM brocoins WHERE uid=?";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, member.getId());
            ResultSet rs = ps.executeQuery();
            brocoins = rs.getInt(1);
            ps.close();
        } catch (SQLException e) {
            logger.error("Error while attempting to retrieve {}'s bank balance from the DB ", member.getUser().getName());
            e.printStackTrace();
            return 0;
        }
        return brocoins;
    }

    public Map<String, Integer> getBroCoins() {
        Map<String, Integer> result = new HashMap<>();
        try {
            assert con != null;
            String SQL = "SELECT * from brocoins";
            PreparedStatement ps = con.prepareStatement(SQL);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String userID = rs.getString(1);
                int userCash = rs.getInt(2);
                int userBank = rs.getInt(3);
                int total = userBank + userCash;
                result.put(userID, total);
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

    public boolean hasCoinBoost(Member member) {
        String[] boostTypes = {"noob", "pro", "gambler"};
        for (String boost : boostTypes) {
            if (Global.COOLDOWN_MANAGER.hasCooldown(CooldownManager.coinsCooldown(member, boost))) {
                return true;
            }
        }
        return false;
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

    public void updateCashMultiplier(Member member, SlashCommandInteractionEvent event, int amountToChange) throws SQLException {
        int memberCoins = this.getBroCash(member);
        if (amountToChange > 0 && hasCoinBoost(member)) {
            CoinMultiplier multiplier = CoinMultiplier.getMultiplier(member);
            setCash(member, multiplier.applyMultiplier(amountToChange));
            event.getChannel().sendMessage(String.format("%s won %d %s thanks to their active coin multiplier!", member.getAsMention(), multiplier.applyMultiplier(amountToChange), Global.broCoin.getAsMention())).queue();
            return;
        }
        memberCoins += amountToChange;
        setCash(member, memberCoins);
    }

    public void updateCashMultiplierDM(Member member, int amountToChange) throws SQLException {
        int memberCoins = this.getBroCash(member);
        if (amountToChange > 0 && hasCoinBoost(member)) {
            CoinMultiplier multiplier = CoinMultiplier.getMultiplier(member);
            setCash(member, multiplier.applyMultiplier(amountToChange));
            member.getUser().openPrivateChannel().flatMap(channel ->
                    channel.sendMessage(String.format("You won %d %s thanks to your active coin multiplier!", multiplier.applyMultiplier(amountToChange), Global.broCoin.getAsMention()))
            ).queue();
            return;
        }
        memberCoins += amountToChange;
        setCash(member, memberCoins);
    }

    public void updateCashWithoutMultiplier(Member member, int amountToChange) throws SQLException {
        int memberCoins = this.getBroCash(member);
        memberCoins += amountToChange;
        setCash(member, memberCoins);

    }


}
