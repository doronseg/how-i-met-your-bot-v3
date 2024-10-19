package me.nerdoron.himyb.modules.fun.brocoins;

import me.nerdoron.himyb.modules.bot.CooldownManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import static me.nerdoron.himyb.Global.COOLDOWN_MANAGER;

public class CheckCrimeCooldowns {
    public static boolean noCooldown(SlashCommandInteractionEvent event) {
        String remainingCrime = COOLDOWN_MANAGER.parseCooldown(CooldownManager.commandID(event));
        Member member = event.getMember();
        assert member != null;
        String remainingArrested = COOLDOWN_MANAGER.parseCooldown(CooldownManager.arrestedID(member));


        if (COOLDOWN_MANAGER.hasTag(CooldownManager.commandID(event), "Success")) {
            event.reply("Don't commit to many crimes! You can attempt a new heist in " + remainingCrime)
                    .setEphemeral(true)
                    .queue();
            return true;
        }

        if (COOLDOWN_MANAGER.hasTag(CooldownManager.commandID(event), "BackOff")) {
            event.reply("There's still too much heat, Try again in " + remainingCrime).setEphemeral(true)
                    .queue();
            return true;
        }

        if (COOLDOWN_MANAGER.hasTag(CooldownManager.arrestedID(member), "Ran")) {
            event.reply("The cops are looking for you! Don't provoke them, Try again in " + remainingArrested).setEphemeral(true)
                    .queue();
            return true;
        }

        if (COOLDOWN_MANAGER.hasTag(CooldownManager.arrestedID(member), "card")) {
            event.reply("You just got out of jail. Lay low for a while. Try again in " + remainingArrested).setEphemeral(true)
                    .queue();
            return true;
        }

        if (COOLDOWN_MANAGER.hasTag(CooldownManager.arrestedID(member), "Bribed")) {
            event.reply(":cop:: I don't think that's a good idea. Try again in " + remainingArrested).setEphemeral(true)
                    .queue();
            return true;
        }
        return false;
    }
}
