package me.nerdoron.himyb.commands.currency;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.Objects;

import static me.nerdoron.himyb.Global.BROCOINS_SQL;
import static me.nerdoron.himyb.Global.broCoin;

public class GiveCashCommand extends SlashCommand {
    private static final Logger logger = LoggingHandler.logger(TransferCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        Member memberToTransferTo = Objects.requireNonNull(event.getOption("user")).getAsMember();
        int transfer = Objects.requireNonNull(event.getOption("amount")).getAsInt();

        if (!BROCOINS_SQL.hasAccount(member)) {
            event.reply("You never had any BroCoins :(\nTry earning some cash!").setEphemeral(true).queue();
            return;
        }
        if (transfer > BROCOINS_SQL.getBroCash(member)) {
            event.reply("You can't give more cash than you have in your wallet!").setEphemeral(true).queue();
            return;
        }
        if (transfer < 0) {
            event.reply("You can't a give a negative number!").setEphemeral(true).queue();
            return;
        }
        if (transfer == 0) {
            event.reply("You can't give 0 BroCoins!").setEphemeral(true).queue();
            return;
        }
        assert memberToTransferTo != null;
        if (memberToTransferTo.getUser().isBot()) {
            event.reply("You can't give cash to a bot!").setEphemeral(true).queue();
            return;
        }

        try {
            BROCOINS_SQL.updateCash(member, -transfer);
            BROCOINS_SQL.updateCash(memberToTransferTo, transfer);
            assert member != null;
            event.reply(memberToTransferTo.getAsMention()).addEmbeds(doneEmbed(member, memberToTransferTo, transfer)).queue();
            logger.info("{}(ID:{}) gave {} coins cash to {}(ID:{}).", member.getUser().getName(), member.getId(), transfer, memberToTransferTo.getUser().getName(), memberToTransferTo.getId());
        } catch (SQLException e) {
            assert member != null;
            logger.error("{} Tried to transfer cash, but an error has occurred.", member.getUser().getName());
            e.printStackTrace();
            event.reply("An error has occurred. Please try again").setEphemeral(true).queue();
        }

    }

    private MessageEmbed doneEmbed(Member member, Member memberToTransferTo, int transfer) {
        return new EmbedBuilder()
                .setTitle(broCoin.getAsMention() + " Cash transaction successful.")
                .setDescription(member.getAsMention() + " gave " + transfer + " " + broCoin.getAsMention() + " in cash to " + memberToTransferTo.getAsMention() + ".")
                .addField("Your Cash Balance", BROCOINS_SQL.getBroCash(member) + " " + broCoin.getAsMention(), true)
                .addField(memberToTransferTo.getUser().getName() + "'s Cash Balance", BROCOINS_SQL.getBroCash(memberToTransferTo) + " " + broCoin.getAsMention(), true)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    @Override
    public SlashCommandData getSlash() {
        SlashCommandData deposit = Commands.slash("give-cash", "Give cash to another user.");
        deposit.addOption(OptionType.INTEGER, "amount", "How much would you like to give?", true);
        deposit.addOption(OptionType.USER, "user", "who would you like to give to?", true);
        return deposit;
    }
}
