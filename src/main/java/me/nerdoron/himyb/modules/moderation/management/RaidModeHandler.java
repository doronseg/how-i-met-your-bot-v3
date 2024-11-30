package me.nerdoron.himyb.modules.moderation.management;

import me.nerdoron.himyb.modules.bot.Database;
import me.nerdoron.himyb.modules.moderation.punishments.KickHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RaidModeHandler extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(RaidModeHandler.class);
    private static final Connection con = Database.connect();

    public static boolean isRaidMode() {
        String SQL = "SELECT raidmode FROM raidmode LIMIT 1";
        try {
            assert con != null;
            try (PreparedStatement ps = con.prepareStatement(SQL);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("raidmode") == 1;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking raid mode: ", e);
        }
        return false;  // Default to false if error occurs
    }

    public static void setRaidMode(boolean raidMode) {
        assert con != null;
        String SQL_CHECK = "SELECT raidmode FROM raidmode LIMIT 1";
        String SQL_INSERT = "INSERT INTO raidmode (raidmode) VALUES (?)";
        String SQL_UPDATE = "UPDATE raidmode SET raidmode = ? WHERE raidmode IS NOT NULL";

        try (PreparedStatement checkPs = con.prepareStatement(SQL_CHECK);
             ResultSet rs = checkPs.executeQuery()) {

            if (rs.next()) {
                try (PreparedStatement updatePs = con.prepareStatement(SQL_UPDATE)) {
                    updatePs.setInt(1, raidMode ? 1 : 0);
                    updatePs.executeUpdate();
                }
            } else {
                try (PreparedStatement insertPs = con.prepareStatement(SQL_INSERT)) {
                    insertPs.setInt(1, raidMode ? 1 : 0);
                    insertPs.executeUpdate();
                }
            }
        } catch (SQLException e) {
            logger.error("Error setting raid mode: ", e);
        }
    }


    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        Member member = event.getMember();
        if (!isRaidMode()) return;

        Guild guild = event.getGuild();
        TextChannel modChannel = guild.getTextChannelById("850467018367828039");
        assert modChannel != null;

        KickHandler kickHandler = new KickHandler();
        member.getUser().openPrivateChannel()
                .flatMap(channel -> channel.sendMessage("Sorry, how i met your discord is currently under lockdown. Try joining again later, sorry for the inconvenience."))
                .queue(null, throwable -> logger.warn("Could not send private message to user {}: {}", member.getUser().getId(), throwable.getMessage()));

        kickHandler.kickMember(member, event.getJDA().getSelfUser(), guild, "Raid mode active")
                .thenAccept(success -> {
                    if (success) {
                        modChannel.sendMessage(String.format("<@&850439278717829190>, kicked %s due to raid mode.", member.getAsMention())).queue(
                                null,
                                throwable -> {
                                    if (throwable instanceof RateLimitedException) {
                                        RateLimitedException rateLimitEx = (RateLimitedException) throwable;
                                        long retryAfter = rateLimitEx.getRetryAfter();
                                        logger.warn("Rate limited. Retrying in {}ms", retryAfter);
                                    } else {
                                        logger.error("Failed to send message due to error: {}", throwable.getMessage());
                                    }
                                }
                        );
                    } else {
                        modChannel.sendMessage(String.format("<@&850439278717829190>, could not kick %s even though raid mode is on. Please investigate the user.", member.getAsMention())).queue();
                    }
                });

    }
}
