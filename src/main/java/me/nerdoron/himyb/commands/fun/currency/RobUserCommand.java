package me.nerdoron.himyb.commands.fun.currency;

import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;

import static me.nerdoron.himyb.Global.COOLDOWN_MANAGER;

public class RobUserCommand extends SlashCommand {
    private static final Logger logger = LoggingHandler.logger(RobUserCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String remainingArrested = COOLDOWN_MANAGER.parseCooldown("arrested");

        if (COOLDOWN_MANAGER.hasTag("arrested", "Ran")) {
            event.reply("The cops are looking for you! Don't provoke them, Try again in " + remaining).setEphemeral(true)
                    .queue();
            return;
        }

        if (COOLDOWN_MANAGER.hasTag("arrested", "Bribed")) {
            event.reply(":cop:: I don't think that's a good idea. Try again in " + remaining).setEphemeral(true)
                    .queue();
            return;
        }
    }

    @Override
    public SlashCommandData getSlash() {
        SlashCommandData rob = Commands.slash("rob", "Try to rob a user.");
        rob.addOption(OptionType.USER, "user", "Who are you trying to rob?", false);
        return rob;
    }
}
