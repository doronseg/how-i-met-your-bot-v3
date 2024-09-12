package me.nerdoron.himyb.commands.fun.currency;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.fun.brocoins.JailHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;

public class CrimeCommand extends SlashCommand {
    private static final Logger logger = LoggingHandler.logger(CrimeCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();

        if (Global.COOLDOWN_MANAGER.hasCooldown(CooldownManager.commandID(event))) {
            String remaining = Global.COOLDOWN_MANAGER.parseCooldown(CooldownManager.commandID(event));
            event.reply("A day hasn't passed since you last claimed your daily batch. " + remaining + " remaining.")
                    .setEphemeral(true)
                    .queue();
            return;
        }
        if (Global.JAIL_CHECKER.checkIfInJail(member)) {
            event.replyEmbeds(JailHandler.inJailEmbed(member)).queue();
            return;
        }


    }

    @Override
    public SlashCommandData getSlash() {
        return null;
    }
}
