package me.nerdoron.himyb.commands.gambling;

import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.fun.brocoins.JailHelper;
import me.nerdoron.himyb.modules.fun.hilo.HiLoHandler;
import me.nerdoron.himyb.modules.fun.hilo.HiLoHelper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

import static me.nerdoron.himyb.Global.BROCOINS_SQL;
import static me.nerdoron.himyb.Global.COOLDOWN_MANAGER;

public class HiLoCommand extends SlashCommand {
    final HiLoHandler hiLoHandler = new HiLoHandler();

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
            event.reply("You have already played a round of Hi-Lo. Please try again in " + time).setEphemeral(true)
                    .queue();
            return;
        }
        if (HiLoHelper.hasOnGoingHiLoGame(member)) {
            event.reply("You already have an ongoing game!").setEphemeral(true).queue();
            return;
        }

        int bet = Objects.requireNonNull(event.getOption("bet")).getAsInt();
        if (bet > BROCOINS_SQL.getBroCash(member)) {
            event.reply("You don't have enough cash!").queue();
            return;
        }


        event.replyEmbeds(HiLoHelper.hiLoStarting()).queue();
        hiLoHandler.startHiLoGame(event, bet);

    }

    @Override
    public SlashCommandData getSlash() {
        SlashCommandData cmd = Commands.slash("hilo", "Play a round of Hi-Lo");
        OptionData bet = new OptionData(OptionType.INTEGER, "bet", "How many BroCoins do you want to enter with?", true);
        bet.setMinValue(25);
        cmd.addOptions(bet);
        return cmd;
    }
}
