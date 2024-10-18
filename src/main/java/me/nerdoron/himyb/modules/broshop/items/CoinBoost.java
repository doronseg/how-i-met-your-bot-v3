package me.nerdoron.himyb.modules.broshop.items;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.broshop.ShopHelper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static me.nerdoron.himyb.Global.COOLDOWN_MANAGER;

public class CoinBoost {

    private static final Logger logger = LoggingHandler.logger(CoinBoost.class);
    private final Map<String, CoinBoostInfo> boostInfoMap;

    {
        boostInfoMap = new HashMap<>();
        boostInfoMap.put("C1", new CoinBoost.CoinBoostInfo(1296495024086843393L, "noob", Global.DAY_IN_SECONDS, "The Noob coin boost for 24 hours."));
        boostInfoMap.put("C2", new CoinBoost.CoinBoostInfo(1296495024086843393L, "noob", Global.DAY_IN_SECONDS * 2, "The Noob coin boost for 48 hours."));
        boostInfoMap.put("C3", new CoinBoost.CoinBoostInfo(1296495213854064742L, "pro", Global.DAY_IN_SECONDS, "The Pro coin boost for 24 hours."));
        boostInfoMap.put("C4", new CoinBoost.CoinBoostInfo(1296495213854064742L, "pro", Global.DAY_IN_SECONDS * 2, "The Pro coin boost for 48 hours."));
        boostInfoMap.put("C5", new CoinBoost.CoinBoostInfo(1296495296200573068L, "gambler", Global.HOUR_IN_SECONDS, "The Gambler coin boost for 1 hour."));
    }

    public Boolean useCoinBoost(String type, Member member, SlashCommandInteractionEvent event) {
        CoinBoostInfo boostInfo = boostInfoMap.get(type);
        Guild guild = event.getGuild();
        assert guild != null;

        if (COOLDOWN_MANAGER.hasCooldown(CooldownManager.coinsCooldown(member, type))) {
            String remainingTime = COOLDOWN_MANAGER.parseCooldown(CooldownManager.coinsCooldown(member, boostInfo.cooldownName));
            event.reply(String.format("You already have this type of boost active.\nTime remaining: %s", remainingTime))
                    .setEphemeral(true).queue();
            return false;
        }

        Role role = guild.getRoleById(boostInfo.roleId);
        addRole(member, boostInfo.cooldownName, boostInfo.durationInSeconds, role, guild);
        event.reply("Successfully redeemed " + boostInfo.successMessage).setEphemeral(true).queue();
        Objects.requireNonNull(event.getGuild().getTextChannelById("1296434268238516274")).sendMessageEmbeds(ShopHelper.boostRedeemed(member, boostInfo.successMessage)).queue();
        logger.info("{} (ID:{}) redeemed {}.", member.getUser().getName(), member.getId(), type);
        return true;
    }

    private void addRole(Member member, String cooldownName, int time, Role role, Guild guild) {
        assert role != null;
        guild.addRoleToMember(member, role).queue();
        COOLDOWN_MANAGER.addCooldown(CooldownManager.expCooldown(member, cooldownName), time);
    }

    private static class CoinBoostInfo {
        final long roleId;
        final String cooldownName;
        final int durationInSeconds;
        final String successMessage;

        public CoinBoostInfo(long roleId, String cooldownName, int durationInSeconds, String successMessage) {
            this.roleId = roleId;
            this.cooldownName = cooldownName;
            this.durationInSeconds = durationInSeconds;
            this.successMessage = successMessage;
        }
    }
}