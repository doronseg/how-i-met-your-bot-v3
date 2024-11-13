package me.nerdoron.himyb.modules.moderation.punishments;

import me.nerdoron.himyb.modules.bot.LoggingHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class BanHandler extends ListenerAdapter {
    private static final Logger logger = LoggingHandler.logger(BanHandler.class);

    @Override
    public void onGuildBan(GuildBanEvent event) {
        Guild guild = event.getGuild();
        if (!guild.getId().equals("850396197646106624")) return;
        AtomicReference<String> reason = new AtomicReference<>("Unable to retrieve reason");
        guild.retrieveAuditLogs().type(ActionType.BAN).queue(entries -> {
            AuditLogEntry entry = entries.get(0);
            reason.set(entry.getReason());
            logger.info("User {} was banned for reason {}", event.getUser(), reason.get());
            // TODO: Generate modlog with reason
        });
    }

    @Override
    public void onGuildUnban(GuildUnbanEvent event) {
        Guild guild = event.getGuild();
        if (!guild.getId().equals("850396197646106624")) return;
        AtomicReference<String> reason = new AtomicReference<>("Unable to retrieve reason");

        guild.retrieveAuditLogs().type(ActionType.UNBAN).queue(entries -> {
            AuditLogEntry entry = entries.get(0);
            reason.set(entry.getReason());
            logger.info("User {} was unbanned for reason {}", event.getUser(), reason.get());
            // TODO: Generate modlog with reason
        });
    }

    public CompletableFuture<Boolean> BanMember(Member member, Guild guild, String reason) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        if (!guild.getId().equals("850396197646106624") || member.hasPermission(Permission.BAN_MEMBERS)) {
            result.complete(false);
            return result;
        }
        logger.info("{} (ID:{}) was banned for reason: {}.", member.getUser().getName(), member.getId(), reason);
        member.getUser().openPrivateChannel()
                .flatMap(privateChannel -> privateChannel.sendMessage(
                        String.format("You have been banned from how i met your discord for reason `%s`.", reason))
                )
                .queue(
                        success -> guild.ban(member, 7, TimeUnit.DAYS).reason(reason).queue(
                                banSuccess -> result.complete(true),
                                banFailure -> result.complete(false)
                        ),
                        failure -> result.complete(false)
                );

        return result;
    }

    public CompletableFuture<Boolean> UnbanMember(User user, Guild guild, String reason) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        if (!guild.getId().equals("850396197646106624")) {
            result.complete(false);
            return result;
        }
        logger.info("{} (ID:{}) was unbanned for reason: {}.", user.getName(), user.getId(), reason);
        guild.unban(user).reason(reason).queue(
                success -> result.complete(true),
                failure -> result.complete(false)
        );
        return result;
    }
}
