package me.nerdoron.himyb.modules.moderation.punishments;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.moderation.modlogs.ModLogHandler;
import me.nerdoron.himyb.modules.moderation.modlogs.ModLogType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static me.nerdoron.himyb.Global.COOLDOWN_MANAGER;

public class MuteHandler extends ListenerAdapter {
    public static final String muteRoleId = "1305982548983152762"; // TODO: CHANGE THIS
    private static final Logger logger = LoggingHandler.logger(MuteHandler.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final String guildId = "850396197646106624";

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        Guild guild = event.getGuild();
        if (!guild.getId().equals("850396197646106624")) return;
        Member member = event.getMember();
        Role muteRole = guild.getRoleById(muteRoleId);
        if (muteRole == null) return;
        List<Role> rolesAdded = event.getRoles();
        if (!rolesAdded.contains(muteRole)) return;
        guild.retrieveAuditLogs()
                .type(ActionType.MEMBER_ROLE_UPDATE)
                .limit(1)
                .queue(auditLogs -> {
                    AuditLogEntry entry = auditLogs.get(0);
                    if (!Objects.requireNonNull(entry.getUser()).getId().equals(event.getJDA().getSelfUser().getId())) {
                        guild.removeRoleFromMember(member, muteRole).queue();
                        entry.getUser().openPrivateChannel()
                                .flatMap(privateChannel -> privateChannel.sendMessage(
                                        "You can't manually give the muted role, to give a perm mute, put 0 in the /mute command.")
                                ).queue();
                        logger.info("{} (ID:{}) was given the muted role manually, removing role.", member.getUser().getName(), member.getId());
                    }
                });
    }


    public CompletableFuture<Boolean> muteMember(Member member, Member mod, int timeInHours, Guild guild, String reason) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();

        if (!guild.getId().equals(guildId) || member.hasPermission(Permission.BAN_MEMBERS)) {
            result.complete(false);
            return result;
        }

        Optional<Role> mutedRole = Optional.ofNullable(guild.getRoleById(muteRoleId));
        if (!mutedRole.isPresent()) {
            result.complete(false);
            return result;
        }
        logger.info("{} (ID:{}) was muted for reason: {}.", member.getUser().getName(), member.getId(), reason);
        guild.addRoleToMember(member, mutedRole.get()).queue(
                success -> {
                    if (timeInHours != 0) {
                        COOLDOWN_MANAGER.addCooldown(CooldownManager.muteCooldown(member), timeInHours * Global.HOUR_IN_SECONDS);
                        Objects.requireNonNull(guild.getTextChannelById(ModLogHandler.modLogChannelId))
                                .sendMessage("Loading...")
                                .queue(message -> {
                                    String messageId = message.getId();
                                    MessageEmbed embed = ModLogHandler.generateLog(
                                            ModLogType.MUTE, mod.getUser(), member.getUser(), reason, timeInHours, 0, messageId);
                                    message.editMessage("\u200E").queue();
                                    message.editMessageEmbeds(embed).queue();
                                });
                    } else {
                        COOLDOWN_MANAGER.addCooldown(CooldownManager.muteCooldown(member), 150000 * Global.HOUR_IN_SECONDS);
                        Objects.requireNonNull(guild.getTextChannelById(ModLogHandler.modLogChannelId))
                                .sendMessage("Loading...")
                                .queue(message -> {
                                    String messageId = message.getId();
                                    MessageEmbed embed = ModLogHandler.generateLog(
                                            ModLogType.MUTE, mod.getUser(), member.getUser(), reason, -1, 0, messageId);
                                    message.editMessage("\u200E").queue();
                                    message.editMessageEmbeds(embed).queue();
                                });
                    }
                    result.complete(true);

                },
                failure -> {
                    logger.error("Failed to mute {}: {}", member.getUser().getName(), failure.getMessage());
                    result.complete(false);
                }
        );
        return result;
    }

    public CompletableFuture<Boolean> unMuteMember(Member member, User mod, Guild guild, String reason) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        if (!guild.getId().equals(guildId)) {
            result.complete(false);
            return result;
        }
        Optional<Role> mutedRole = Optional.ofNullable(guild.getRoleById(muteRoleId));
        if (!mutedRole.isPresent()) {
            result.complete(false);
            return result;
        }
        logger.info("Unmuting {} for reason: {}", member.getUser().getName(), reason);
        guild.removeRoleFromMember(member, mutedRole.get()).queue(
                success -> {
                    COOLDOWN_MANAGER.deleteCooldown(CooldownManager.muteCooldown(member));
                    result.complete(true);
                    Objects.requireNonNull(guild.getTextChannelById(ModLogHandler.modLogChannelId))
                            .sendMessage("Loading...")
                            .queue(message -> {
                                String messageId = message.getId();
                                MessageEmbed embed = ModLogHandler.generateLog(
                                        ModLogType.UNMUTE, mod, member.getUser(), reason, 0, 0, messageId);
                                message.editMessage("\u200E").queue();
                                message.editMessageEmbeds(embed).queue();
                            });
                },
                failure -> {
                    logger.error("Failed to unmute {}: {}", member.getUser().getName(), failure.getMessage());
                    result.complete(false);
                }
        );
        return result;
    }

    public void checkMutes(JDA jda) {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) {
            logger.warn("Guild with ID {} not found.", guildId);
            return;
        }
        scheduler.scheduleAtFixedRate(() -> {
            try {
                guild.loadMembers().onSuccess(members -> {
                    for (Member member : members) {
                        Role muteRole = guild.getRoleById(muteRoleId);
                        if (muteRole != null && member.getRoles().contains(muteRole)) {
                            if (!COOLDOWN_MANAGER.hasCooldown(CooldownManager.muteCooldown(member))) {
                                unMuteMember(member, jda.getSelfUser(), guild, "Temporary mute completed.");
                                logger.info("Mute ended for {}. Removing role.", member.getUser().getName());
                            }
                        }
                    }
                }).onError(e -> logger.error("Failed to load members: {}", e.getMessage()));

            } catch (Exception e) {
                logger.error("An error occurred in the scheduled task", e);
            }
        }, 0, 60, TimeUnit.SECONDS);
    }
}
