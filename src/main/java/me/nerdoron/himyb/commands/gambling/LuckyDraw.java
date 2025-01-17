package me.nerdoron.himyb.commands.gambling;

import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.fun.brocoins.JailHelper;
import me.nerdoron.himyb.modules.fun.luckydraw.LDHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.sql.SQLException;
import java.util.Objects;

import static me.nerdoron.himyb.Global.BROCOINS_SQL;
import static me.nerdoron.himyb.Global.COOLDOWN_MANAGER;

public class LuckyDraw extends SlashCommand {

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
            event.reply(String.format("Chill out bucko! I know it's tempting... %s remaining.", time)).setEphemeral(true)
                    .queue();
            return;
        }
        int bet = Objects.requireNonNull(event.getOption("bet")).getAsInt();
        int number = Objects.requireNonNull(event.getOption("number")).getAsInt();
        if (bet > BROCOINS_SQL.getBroCash(event.getMember())) {
            event.reply("You don't have enough cash!").setEphemeral(true).queue();
            return;
        }

        LDHandler ldHandler = new LDHandler();
        try {
            ldHandler.LDGameHandler(event, bet, number);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public SlashCommandData getSlash() {
        SlashCommandData ld = Commands.slash("luckydraw",
                "Bet on a game of Lucky Draw. Ultimate High risk, high reward.");
        OptionData bet = new OptionData(OptionType.INTEGER, "bet", "How much do you want to bet?", true);
        OptionData number = new OptionData(OptionType.INTEGER, "number",
                "Which number would you like to place your bet on?", true);
        bet.setMinValue(15);
        number.setMinValue(1);
        number.setMaxValue(999);
        ld.addOptions(bet, number);
        return ld;
    }
}
