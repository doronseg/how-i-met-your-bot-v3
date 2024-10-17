package me.nerdoron.himyb.modules.broshop.items.exp;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.broshop.ShopHelper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static me.nerdoron.himyb.Global.COOLDOWN_MANAGER;

public class ExpBoost {

    private final Map<String, ExpBoostInfo> boostInfoMap;

    {
        boostInfoMap = new HashMap<>();
        boostInfoMap.put("X1", new ExpBoostInfo(1135324348027240578L, "novice", Global.DAY_IN_SECONDS, "The Novice XP boost for 24 hours."));
        boostInfoMap.put("X2", new ExpBoostInfo(1135324348027240578L, "novice", Global.DAY_IN_SECONDS * 2, "The Novice XP boost for 48 hours."));
        boostInfoMap.put("X3", new ExpBoostInfo(1135324427739996160L, "apprentice", Global.DAY_IN_SECONDS, "The Apprentice XP boost for 24 hours."));
        boostInfoMap.put("X4", new ExpBoostInfo(1135324427739996160L, "apprentice", Global.DAY_IN_SECONDS * 2, "The Apprentice XP boost for 48 hours."));
        boostInfoMap.put("X5", new ExpBoostInfo(1135324489589203095L, "grandmaster", Global.HOUR_IN_SECONDS, "The Grandmaster XP boost for 1 hour."));
    }

    public Boolean useExpBoost(String type, Member member, SlashCommandInteractionEvent event) {
        ExpBoostInfo boostInfo = boostInfoMap.get(type);
        Guild guild = event.getGuild();
        assert guild != null;

        if (COOLDOWN_MANAGER.hasCooldown(CooldownManager.expCooldown(member, boostInfo.cooldownName))) {
            String remainingTime = COOLDOWN_MANAGER.parseCooldown(CooldownManager.expCooldown(member, boostInfo.cooldownName));
            event.reply(String.format("You already have this type of boost active.\nTime remaining: %s", remainingTime))
                    .setEphemeral(true).queue();
            return false;
        }

        Role role = guild.getRoleById(boostInfo.roleId);
        addRole(member, boostInfo.cooldownName, boostInfo.durationInSeconds, role, guild);
        event.reply("Successfully redeemed " + boostInfo.successMessage).setEphemeral(true).queue();
        Objects.requireNonNull(event.getGuild().getTextChannelById("1296434268238516274")).sendMessageEmbeds(ShopHelper.expBoostRedeemed(member, boostInfo.successMessage)).queue();
        return true;
    }

    private void addRole(Member member, String cooldownName, int time, Role role, Guild guild) {
        assert role != null;
        guild.addRoleToMember(member, role).queue();
        COOLDOWN_MANAGER.addCooldown(CooldownManager.expCooldown(member, cooldownName), time);
    }

    private static class ExpBoostInfo {
        long roleId;
        String cooldownName;
        int durationInSeconds;
        String successMessage;

        public ExpBoostInfo(long roleId, String cooldownName, int durationInSeconds, String successMessage) {
            this.roleId = roleId;
            this.cooldownName = cooldownName;
            this.durationInSeconds = durationInSeconds;
            this.successMessage = successMessage;
        }
    }
}
