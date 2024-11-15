package me.nerdoron.himyb.modules.moderation.modlogs;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.Database;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;

import java.sql.*;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class ModLogHandler {
    public static final String modLogChannelId = "1306366183741722734";
    private static final Logger logger = LoggingHandler.logger(ModLogHandler.class);
    private static final Connection con = Database.connect();

    public static MessageEmbed generateLog(ModLogType modLogType, User mod, User target, String reason, int timeInHours, int pardonedStrikes, String discordMessageId) {
        int modLogId = insertNewModLog(target.getId(), mod.getId(), reason, OffsetDateTime.now(), modLogType, discordMessageId);
        String timestamp = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (modLogId == -1) {
            return new EmbedBuilder()
                    .setTitle("Error")
                    .setDescription("An error has when trying to generate a modlog! Error message: `SQLException`")
                    .setColor(Global.embedColor)
                    .setFooter(Global.footertext, Global.footerpfp)
                    .build();
        }
        String time = timeInHours + " hours.";
        if (timeInHours == -1) {
            time = "Permanent mute.";
        }

        switch (modLogType) {
            case MUTE:
                return new EmbedBuilder()
                        .setTitle("\uD83E\uDD10 User Muted")
                        .setDescription(String.format("%s was muted by %s.", target.getAsMention(), mod.getAsMention()))
                        .setColor(Global.embedColor)
                        .addField("Reason", reason, false)
                        .addField("Mute Length", time, true)
                        .addField("Modlog ID", String.valueOf(modLogId), true)
                        .addField("Time", timestamp, true)
                        .build();
            case UNMUTE:
                return new EmbedBuilder()
                        .setTitle("\uD83D\uDD0A User Unmuted")
                        .setDescription(String.format("%s was unmuted by %s.", target.getAsMention(), mod.getAsMention()))
                        .setColor(Global.embedColor)
                        .addField("Reason", reason, false)
                        .addField("Modlog ID", String.valueOf(modLogId), true)
                        .addField("Time", timestamp, true)
                        .build();
            case BAN:
                return new EmbedBuilder()
                        .setTitle("\uD83D\uDD28 User Banned")
                        .setDescription(String.format("%s was banned by %s.", target.getAsMention(), mod.getAsMention()))
                        .setColor(Global.embedColor)
                        .addField("Reason", reason, false)
                        .addField("Modlog ID", String.valueOf(modLogId), true)
                        .addField("Time", timestamp, true)
                        .build();
            case UNBAN:
                return new EmbedBuilder()
                        .setTitle("\uD83D\uDD27 User Unbanned")
                        .setDescription(String.format("%s was unbanned by %s.", target.getAsMention(), mod.getAsMention()))
                        .setColor(Global.embedColor)
                        .addField("Reason", reason, false)
                        .addField("Modlog ID", String.valueOf(modLogId), true)
                        .addField("Time", timestamp, true)
                        .build();
            case KICK:
                return new EmbedBuilder()
                        .setTitle("\uD83D\uDC62 User Kicked")
                        .setDescription(String.format("%s was kicked by %s.", target.getAsMention(), mod.getAsMention()))
                        .setColor(Global.embedColor)
                        .addField("Reason", reason, false)
                        .addField("Modlog ID", String.valueOf(modLogId), true)
                        .addField("Time", timestamp, true)
                        .build();
            case STRIKE:
                return new EmbedBuilder()
                        .setTitle("\uD83D\uDEA9 User Striked")
                        .setDescription(String.format("%s was issued a strike by %s.", target.getAsMention(), mod.getAsMention()))
                        .setColor(Global.embedColor)
                        .addField("Reason", reason, false)
                        .addField("Modlog ID", String.valueOf(modLogId), true)
                        .addField("Time", timestamp, true)
                        .build();
            case PARDON:
                return new EmbedBuilder()
                        .setTitle("\uD83C\uDFF3 User Pardoned")
                        .setDescription(String.format("%s was pardoned %d strike(s) by %s.", target.getAsMention(), pardonedStrikes, mod.getAsMention()))
                        .setColor(Global.embedColor)
                        .addField("Reason", reason, false)
                        .addField("Modlog ID", String.valueOf(modLogId), true)
                        .addField("Time", timestamp, true)
                        .build();
            case RAID_MODE_ON:
                return new EmbedBuilder()
                        .setTitle("\uD83D\uDD12 Raid Mode Activated")
                        .setDescription(String.format("%s activated raid mode.", target.getAsMention()))
                        .setColor(Global.embedColor)
                        .addField("Reason", reason, false)
                        .addField("Modlog ID", String.valueOf(modLogId), true)
                        .addField("Time", timestamp, true)
                        .build();
            case RAID_MODE_OFF:
                return new EmbedBuilder()
                        .setTitle("\uD83D\uDD12 Raid Mode Disabled")
                        .setDescription(String.format("%s disabled raid mode.", target.getAsMention()))
                        .setColor(Global.embedColor)
                        .addField("Reason", reason, false)
                        .addField("Modlog ID", String.valueOf(modLogId), true)
                        .addField("Time", timestamp, true)
                        .build();
        }
        return new EmbedBuilder()
                .setTitle("Error")
                .setDescription("An unknown error has when trying to generate a modlog!")
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    private static int insertNewModLog(String memberId, String modId, String reason, OffsetDateTime time, ModLogType modLogType, String discordMessageId) {
        String SQL = "INSERT INTO modlogs (memberId, modId, reason, timestamp, type, discordMessageId) VALUES (?, ?, ?, ?, ?, ?)";
        assert con != null;
        try (PreparedStatement ps = con.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, memberId);
            ps.setString(2, modId);
            ps.setString(3, reason);
            ps.setString(4, time.toString());
            ps.setInt(5, modLogType.getId());
            ps.setString(6, discordMessageId);
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error(e.toString());
        }
        return -1;
    }

    public void updateModLogReason(int modLogId, String newReason) {
        String SQL = "UPDATE modlogs SET reason = ? WHERE id = ?";
        assert con != null;
        try (PreparedStatement ps = con.prepareStatement(SQL)) {
            ps.setString(1, newReason);
            ps.setInt(2, modLogId);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Failed to update modlog reason: {}", e.getMessage());
        }
    }

    public String getDiscordMessageIdByModLogId(int modLogId) {
        String SQL = "SELECT discordMessageId FROM modlogs WHERE id = ?";
        assert con != null;
        try (PreparedStatement ps = con.prepareStatement(SQL)) {
            ps.setInt(1, modLogId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("discordMessageId");
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving Discord message ID for mod log ID {}: {}", modLogId, e.getMessage());
        }
        return null;
    }


}
