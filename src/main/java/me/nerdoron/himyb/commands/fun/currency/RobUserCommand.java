package me.nerdoron.himyb.commands.fun.currency;

import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.bot.Rng;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.fun.brocoins.ArrestHandler;
import me.nerdoron.himyb.modules.fun.brocoins.CurrencyHelper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.Objects;

import static me.nerdoron.himyb.Global.*;
import static me.nerdoron.himyb.modules.bot.CooldownManager.commandID;

public class RobUserCommand extends SlashCommand {
    private static final Logger logger = LoggingHandler.logger(RobUserCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String remainingArrested = COOLDOWN_MANAGER.parseCooldown("arrested");
        String remainingRobUser = COOLDOWN_MANAGER.parseCooldown(CooldownManager.commandID(event));

        if (COOLDOWN_MANAGER.hasTag("arrested", "Ran")) {
            event.reply("The cops are looking for you! Don't provoke them, Try again in " + remainingArrested).setEphemeral(true)
                    .queue();
            return;
        }

        if (COOLDOWN_MANAGER.hasTag("arrested", "Bribed")) {
            event.reply(":cop:: I don't think that's a good idea. Try again in " + remainingArrested).setEphemeral(true)
                    .queue();
            return;
        }
        if (COOLDOWN_MANAGER.hasTag(CooldownManager.commandID(event), "success")) {
            event.reply("Don't commit too many robberies! You can attempt a new robbery in " + remainingRobUser)
                    .setEphemeral(true)
                    .queue();
            return;
        }
        if (COOLDOWN_MANAGER.hasTag(CooldownManager.commandID(event), "fail")) {
            event.reply("Don't push your luck! You can attempt a new robbery in " + remainingRobUser)
                    .setEphemeral(true)
                    .queue();
            return;
        }

        Member member = event.getMember();
        assert member != null;
        Member memberToRob = Objects.requireNonNull(event.getInteraction().getOption("user")).getAsMember();
        assert memberToRob != null;
        if (!BROCOINS_SQL.hasAccount(memberToRob)) {
            event.reply(memberToRob.getAsMention() + " never had any BroCoins :(\nTell them to earn some cash!").setEphemeral(true).queue();
            return;
        }
        if (memberToRob.getUser().isBot()) {
            event.reply("You can't rob a bot!").setEphemeral(true).queue();
            return;
        }


        int chance = Rng.generateNumber(1, 100);
        if (chance < 31) {
            int amount = BROCOINS_SQL.getBroCash(memberToRob);
            if (amount <= 0) {
                event.reply("They don't have any cash!").queue();
                return;
            }
            int robRng = Rng.generateNumber(3, 6);
            logger.info(String.valueOf(amount));
            int reward = amount / robRng;
            logger.info(String.valueOf(reward));
            try {
                BROCOINS_SQL.updateCash(member, reward);
                BROCOINS_SQL.updateCash(memberToRob, -reward);
                logger.info("{}(ID:{}) won {} coins while robbing {}(ID:{}).", member.getEffectiveName(), member.getId(), reward, memberToRob.getEffectiveName(), memberToRob.getId());
                event.reply(memberToRob.getAsMention()).addEmbeds(CurrencyHelper.successfulRobEmbed(memberToRob, reward)).queue();
                COOLDOWN_MANAGER.addCooldown(commandID(event), "success", HOUR_IN_SECONDS / 2);
            } catch (SQLException e) {
                event.reply("An error has occurred. Please try again").setEphemeral(true).queue();
                logger.error("{}(ID:{}) Tried to rob someone, but an error has occurred.", member.getEffectiveName(), member.getId());
                e.printStackTrace();
            }
            return;
        }
        if (chance > 31 && chance < 76) {
            ArrestHandler.initialArrest(event, "ROBBERY", 3 * HOUR_IN_SECONDS);
            return;
        }
        event.reply(memberToRob.getAsMention()).addEmbeds(CurrencyHelper.unsuccessfulRobEmbed(memberToRob)).queue();
        COOLDOWN_MANAGER.addCooldown(commandID(event), "fail", HOUR_IN_SECONDS);
        logger.error("{}(ID:{}) Tried to rob someone, but was unsuccessful.", member.getEffectiveName(), member.getId());

    }

    @Override
    public SlashCommandData getSlash() {
        SlashCommandData rob = Commands.slash("rob", "Try to rob a user.");
        rob.addOption(OptionType.USER, "user", "Who are you trying to rob?", true);
        return rob;
    }
}
