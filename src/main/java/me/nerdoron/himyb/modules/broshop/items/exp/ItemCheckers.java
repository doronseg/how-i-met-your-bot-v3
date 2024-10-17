package me.nerdoron.himyb.modules.broshop.items.exp;

import me.nerdoron.himyb.modules.broshop.ShopHelper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ItemCheckers {

    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public void checkForItems(Guild guild) {
        scheduledExecutorService.scheduleAtFixedRate(() -> {

        }, 0, 1, TimeUnit.MINUTES);
    }

    private void removeRoleBasedOnCooldown(String identifier, Member member, Guild guild) {
        Role roleToRemove = null;

        switch (identifier) {
            case "novice":
                roleToRemove = guild.getRoleById(1135324348027240578L);
                break;
            case "apprentice":
                roleToRemove = guild.getRoleById(1135324427739996160L);
                break;
            case "grandmaster":
                roleToRemove = guild.getRoleById(1135324489589203095L);
                break;
        }

        if (roleToRemove != null) {
            guild.removeRoleFromMember(member, roleToRemove).queue(
                    success -> Objects.requireNonNull(guild.getTextChannelById("1296434268238516274")).sendMessageEmbeds(ShopHelper.expBoostEnd(member, identifier)).queue()
            );
        }

    }

}
