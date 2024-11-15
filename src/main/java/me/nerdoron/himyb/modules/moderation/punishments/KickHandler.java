package me.nerdoron.himyb.modules.moderation.punishments;

import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.moderation.modlogs.ModLogHandler;
import me.nerdoron.himyb.modules.moderation.modlogs.ModLogType;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class KickHandler extends ListenerAdapter {
    private static final Logger logger = LoggingHandler.logger(KickHandler.class);

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        Guild guild = event.getGuild();
        String userId = event.getUser().getId();

        if (event.getUser().equals(event.getJDA().getSelfUser())) return;
        if (!guild.getId().equals("850396197646106624")) return;

        AtomicReference<String> reason = new AtomicReference<>("Unable to retrieve reason");

        guild.retrieveAuditLogs().type(ActionType.KICK).limit(10).queue(auditLogs -> {
            auditLogs.stream()
                    .filter(entry -> entry.getTargetId().equals(userId))
                    .filter(entry -> entry.getTimeCreated().isAfter(OffsetDateTime.now().minusMinutes(1)))
                    .findFirst()
                    .ifPresent(entry -> {
                        reason.set(entry.getReason() != null ? entry.getReason() : "No reason provided");
                        logger.info("User {} was kicked for reason {}", event.getUser(), reason.get());

                        User moderator = entry.getUser();
                        if (moderator != null) {
                            Objects.requireNonNull(guild.getTextChannelById(ModLogHandler.modLogChannelId)).sendMessage("Loading...")
                                    .queue(message -> {
                                        String messageId = message.getId();
                                        MessageEmbed embed = ModLogHandler.generateLog(
                                                ModLogType.KICK, moderator, event.getUser(), reason.get(), 0, 0, messageId);
                                        message.editMessage("\u200E").queue();
                                        message.editMessageEmbeds(embed).queue();
                                    });
                        }
                    });
        });
    }

    public CompletableFuture<Boolean> kickMember(Member member, User mod, Guild guild, String reason) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();

        if (!guild.getId().equals("850396197646106624") || member.hasPermission(Permission.BAN_MEMBERS)) {
            result.complete(false);
            return result;
        }

        logger.info("{} (ID:{}) was kicked for reason: {}.", member.getUser().getName(), member.getId(), reason);

        member.getUser().openPrivateChannel()
                .flatMap(privateChannel -> privateChannel.sendMessage(
                        String.format("You have been kicked from how i met your discord for reason `%s`.", reason))
                ).queue();

        guild.kick(member).reason(reason).queue(
                kickSuccess -> {
                    result.complete(true);
                    Objects.requireNonNull(guild.getTextChannelById(ModLogHandler.modLogChannelId)).sendMessage("Loading...")
                            .queue(message -> {
                                String messageId = message.getId();
                                MessageEmbed embed = ModLogHandler.generateLog(
                                        ModLogType.KICK, mod, member.getUser(), reason, 0, 0, messageId);
                                message.editMessage("\u200E").queue();
                                message.editMessageEmbeds(embed).queue();
                            });
                },
                result::completeExceptionally
        );

        return result;
    }

}
