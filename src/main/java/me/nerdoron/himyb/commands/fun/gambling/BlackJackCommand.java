package me.nerdoron.himyb.commands.fun.gambling;

import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.fun.blackjack.BJHandler;
import me.nerdoron.himyb.modules.fun.blackjack.BJHelper;
import me.nerdoron.himyb.modules.fun.brocoins.JailHelper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

import static me.nerdoron.himyb.Global.BROCOINS_SQL;
import static me.nerdoron.himyb.Global.COOLDOWN_MANAGER;


public class BlackJackCommand extends SlashCommand {
    final BJHandler bjHandler = new BJHandler();

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
            event.reply("You have already played a round of blackjack. Please try again in " + time).setEphemeral(true)
                    .queue();
            return;
        }
        if (BJHelper.hasOngoingBjGame(member)) {
            event.reply("You already have an ongoing game!").setEphemeral(true).queue();
            return;
        }

        int bet = Objects.requireNonNull(event.getOption("bet")).getAsInt();
        if (bet > BROCOINS_SQL.getBroCash(event.getMember())) {
            event.reply("You don't have enough cash!").setEphemeral(true).queue();
            return;
        }

        event.replyEmbeds(BJHelper.blackJackMain(bet)).queue();
        try {
            bjHandler.startBJGame(event, bet);
        } catch (InterruptedException e) {
            MessageEmbed err = BJHelper.createErrorEmbed(e.getMessage());
            event.getHook().editOriginalEmbeds(err).queue();
        }
    }


    @Override
    public SlashCommandData getSlash() {
        SlashCommandData cmd = Commands.slash("blackjack", "Play a round of blackjack with the bot as the dealer");
        OptionData bet = new OptionData(OptionType.INTEGER, "bet", "How many BroCoins you wanna bet", true);
        bet.setMinValue(25);
        cmd.addOptions(bet);
        return cmd;
    }
}
