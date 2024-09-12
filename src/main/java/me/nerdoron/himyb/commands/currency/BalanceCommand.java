package me.nerdoron.himyb.commands.currency;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

import static me.nerdoron.himyb.Global.BROCOINS_SQL;
import static me.nerdoron.himyb.Global.broCoin;

public class BalanceCommand extends SlashCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (event.getInteraction().getOptions().isEmpty()) {
            if (!BROCOINS_SQL.hasAccount(member)) {
                event.reply("You never had any BroCoins :(\nTry earning some cash!").queue();
                return;
            }
            assert member != null;
            event.replyEmbeds(bankEmbed(member)).queue();
            return;
        }
        Member memberToCheck = Objects.requireNonNull(event.getInteraction().getOption("user")).getAsMember();
        if (!BROCOINS_SQL.hasAccount(memberToCheck)) {
            assert memberToCheck != null;
            event.reply(memberToCheck.getAsMention() + " never had any BroCoins :(\nTell them to earn some cash!").queue();
            return;
        }
        assert memberToCheck != null;
        event.replyEmbeds(bankEmbed(memberToCheck)).queue();

    }

    private MessageEmbed bankEmbed(Member member) {
        return new EmbedBuilder()
                .setTitle(broCoin.getAsMention() + " Balance")
                .setDescription("Here's " + member.getAsMention() + "'s  current balance:")
                .addField("Bank Balance", BROCOINS_SQL.getBroBank(member) + " " + broCoin.getAsMention(), true)
                .addField("Cash Balance", BROCOINS_SQL.getBroCash(member) + " " + broCoin.getAsMention(), true)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    @Override
    public SlashCommandData getSlash() {
        SlashCommandData balance = Commands.slash("balance", "Check your cash and bank balance.");
        balance.addOption(OptionType.USER, "user", "Check someone else's balance", false);
        return balance;
    }
}
