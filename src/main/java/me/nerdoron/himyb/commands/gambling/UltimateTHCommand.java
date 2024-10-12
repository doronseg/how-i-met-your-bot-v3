package me.nerdoron.himyb.commands.gambling;

import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.fun.brocoins.JailHelper;
import me.nerdoron.himyb.modules.fun.texasholdem.THHandler;
import me.nerdoron.himyb.modules.fun.texasholdem.THHelper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

import static me.nerdoron.himyb.Global.BROCOINS_SQL;
import static me.nerdoron.himyb.Global.COOLDOWN_MANAGER;

public class UltimateTHCommand extends SlashCommand {
    final THHandler thHandler = new THHandler();

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        assert member != null;
        if (JailHelper.checkIfInJail(member)) {
            event.replyEmbeds(JailHelper.inJailEmbed(member)).queue();
            return;
        }
        if (COOLDOWN_MANAGER.hasCooldown(CooldownManager.commandID(event))) {
            String time = COOLDOWN_MANAGER.parseCooldown(CooldownManager.commandID(event));
            event.reply("You have already played a round of Ultimate Texas Hold'em. Please try again in " + time).setEphemeral(true)
                    .queue();
            return;
        }

        if (THHelper.hasOnGoingThGame(member)) {
            event.reply("You already have an ongoing game!").setEphemeral(true).queue();
            return;
        }

        int bet = Objects.requireNonNull(event.getOption("bet")).getAsInt();
        int trips;
        if (event.getOptions().size() == 1) {
            trips = 0;
        } else {
            trips = Objects.requireNonNull(event.getOption("trips")).getAsInt();
        }

        if (bet + trips > BROCOINS_SQL.getBroCash(event.getMember())) {
            event.reply("You don't have enough cash!").setEphemeral(true).queue();
            return;
        }

        int ante = bet / 2;
        int blind = bet / 2;

        event.replyEmbeds(THHelper.texasStarting()).queue();
        thHandler.initGame(event, blind, ante, trips);

    }


    @Override
    public SlashCommandData getSlash() {
        SlashCommandData cmd = Commands.slash("ultimatepoker", "Play a round of ultimate Texas Hold'em with the bot as the dealer");
        OptionData bet = new OptionData(OptionType.INTEGER, "bet", "How many BroCoins do you want to enter with?", true);
        OptionData trips = new OptionData(OptionType.INTEGER, "trips", "How many BroCoins do you want to bet on trips? (Optional)", false);
        bet.setMinValue(20);
        cmd.addOptions(bet, trips);
        return cmd;
    }
}
