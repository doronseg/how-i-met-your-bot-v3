package me.nerdoron.himyb.modules.moderation.punishments;

import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.moderation.modlogs.ModLogHandler;
import me.nerdoron.himyb.modules.moderation.modlogs.ModLogType;
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

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class BanHandler extends ListenerAdapter {
    private static final Logger logger = LoggingHandler.logger(BanHandler.class);

    @Override
    public void onGuildBan(GuildBanEvent event) {
        Guild guild = event.getGuild();
        if (!guild.getId().equals("850396197646106624")) return;
        if (event.getUser().equals(guild.getSelfMember().getUser())) return;
        AtomicReference<String> reason = new AtomicReference<>("Unable to retrieve reason");
        guild.retrieveAuditLogs().limit(10).type(ActionType.BAN).queue(entries -> {
            AuditLogEntry entry = entries.get(0);
            reason.set(entry.getReason());
            Objects.requireNonNull(guild.getTextChannelById(ModLogHandler.modLogChannelId))
                    .sendMessageEmbeds(ModLogHandler.generateLog(ModLogType.BAN, Objects.requireNonNull(entry.getUser()), event.getUser(), reason.get(), 0, 0))
                    .queue();
            logger.info("User {} was banned for reason {}", event.getUser(), reason.get());
        });
    }

    @Override
    public void onGuildUnban(GuildUnbanEvent event) {
        Guild guild = event.getGuild();
        if (!guild.getId().equals("850396197646106624")) return;
        if (event.getUser().equals(guild.getSelfMember().getUser())) return;
        AtomicReference<String> reason = new AtomicReference<>("Unable to retrieve reason");
        guild.retrieveAuditLogs().type(ActionType.UNBAN).queue(entries -> {
            AuditLogEntry entry = entries.get(0);
            reason.set(entry.getReason());
            Objects.requireNonNull(guild.getTextChannelById(ModLogHandler.modLogChannelId))
                    .sendMessageEmbeds(ModLogHandler.generateLog(ModLogType.UNBAN, Objects.requireNonNull(entry.getUser()), event.getUser(), reason.get(), 0, 0))
                    .queue();
            logger.info("User {} was unbanned for reason {}", event.getUser(), reason.get());

        });

    }

    public CompletableFuture<Boolean> BanMember(Member member, Member mod, Guild guild, String reason) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        if (!guild.getId().equals("850396197646106624") || member.hasPermission(Permission.BAN_MEMBERS)) {
            result.complete(false);
            return result;
        }
        member.getUser().openPrivateChannel()
                .flatMap(privateChannel -> privateChannel.sendMessage(
                        String.format("You have been banned from how i met your discord for reason `%s`.", reason))
                ).queue();
        guild.ban(member, 7, TimeUnit.DAYS).reason(reason).queue(
                banSuccess -> {
                    result.complete(true);
                    logger.info("{} (ID:{}) was banned for reason: {}.", member.getUser().getName(), member.getId(), reason);
                    Objects.requireNonNull(guild.getTextChannelById(ModLogHandler.modLogChannelId))
                            .sendMessageEmbeds(ModLogHandler.generateLog(ModLogType.BAN, mod.getUser(), member.getUser(), reason, 0, 0))
                            .queue();
                },
                banFailure -> result.complete(false));
        return result;
    }

    public CompletableFuture<Boolean> UnbanMember(User user, Member mod, Guild guild, String reason) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        if (!guild.getId().equals("850396197646106624")) {
            result.complete(false);
            return result;
        }
        guild.unban(user).reason(reason).queue(
                success -> {
                    result.complete(true);
                    Objects.requireNonNull(guild.getTextChannelById(ModLogHandler.modLogChannelId))
                            .sendMessageEmbeds(ModLogHandler.generateLog(ModLogType.UNBAN, mod.getUser(), user, reason, 0, 0))
                            .queue();
                    logger.info("{} (ID:{}) was unbanned for reason: {}.", user.getName(), user.getId(), reason);
                },
                failure -> result.complete(false)
        );
        return result;
    }
}
