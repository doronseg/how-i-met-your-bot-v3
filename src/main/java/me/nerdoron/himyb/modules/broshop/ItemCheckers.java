package me.nerdoron.himyb.modules.broshop;

import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static me.nerdoron.himyb.Global.COOLDOWN_MANAGER;

public class ItemCheckers {

    private static final Logger logger = LoggingHandler.logger(ItemCheckers.class);
    final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public void checkForItems(JDA jda) {
        Guild guild = jda.getGuildById("850396197646106624");
        assert guild != null;

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                guild.loadMembers().onSuccess(members -> {
                    String[] expCooldowns = {"novice", "apprentice", "grandmaster"};
                    String[] coinCooldowns = {"noob", "pro", "gambler"};
                    // exp cooldowns
                    for (String cooldown : expCooldowns) {
                        for (Member member : members) {
                            Role role = getRoleFromCooldown(cooldown, guild);
                            if (!COOLDOWN_MANAGER.hasCooldown(CooldownManager.expCooldown(member, cooldown))) {
                                if (member.getRoles().contains(role)) {
                                    removeRole(role, guild, member, cooldown);
                                    logger.info("XP Booster {} ended for {}. Removing role.", role.getName(), member.getEffectiveName());
                                }
                            }
                        }
                    }
                    // coin booster
                    for (String cooldown : coinCooldowns) {
                        for (Member member : members) {
                            Role role = getRoleFromCooldown(cooldown, guild);
                            if (!COOLDOWN_MANAGER.hasCooldown(CooldownManager.coinsCooldown(member, cooldown))) {
                                if (member.getRoles().contains(role)) {
                                    removeRole(role, guild, member, cooldown);
                                    logger.info("Coin Booster {} ended for {}. Removing role.", role.getName(), member.getEffectiveName());
                                }
                            }
                        }
                    }

                }).onError(e -> logger.error("Failed to load members: {}", e.getMessage()));
            } catch (Exception e) {
                logger.error("An error occurred in the scheduled task", e);
            }
        }, 0, 60, TimeUnit.SECONDS);
    }


    private Role getRoleFromCooldown(String identifier, Guild guild) {
        switch (identifier) {
            case "novice":
                return guild.getRoleById(1135324348027240578L);
            case "apprentice":
                return guild.getRoleById(1135324427739996160L);
            case "grandmaster":
                return guild.getRoleById(1135324489589203095L);
            case "noob":
                return guild.getRoleById(1296495024086843393L);
            case "pro":
                return guild.getRoleById(1296495213854064742L);
            case "gambler":
                return guild.getRoleById(1296495296200573068L);
            default:
                throw new IllegalStateException("Unexpected value: " + identifier);
        }
    }

    private void removeRole(Role roleToRemove, Guild guild, Member member, String identifier) {
        if (roleToRemove != null) {
            guild.removeRoleFromMember(member, roleToRemove).queue(
                    success -> Objects.requireNonNull(guild.getTextChannelById("1296434268238516274")).sendMessageEmbeds(ShopHelper.boostEnd(member, identifier)).queue()
            );
        }

    }

}
