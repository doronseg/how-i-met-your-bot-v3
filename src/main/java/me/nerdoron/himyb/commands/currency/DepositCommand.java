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

public class DepositCommand extends SlashCommand {
    private static final Logger logger = LoggingHandler.logger(DepositCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        int deposit = Objects.requireNonNull(event.getOption("amount")).getAsInt();

        if (!BROCOINS_SQL.hasAccount(member)) {
            event.reply("You never had any BroCoins :(\nTry earning some cash!").setEphemeral(true).queue();
            return;
        }
        if (deposit > BROCOINS_SQL.getBroCash(member)) {
            event.reply("You can't deposit more cash than you have!").setEphemeral(true).queue();
            return;
        }
        if (deposit < 0) {
            event.reply("You can't a deposit a negative number. Try /withdraw instead!").setEphemeral(true).queue();
            return;
        }
        if (deposit == 0) {
            event.reply("You can't deposit 0 BroCoins!").setEphemeral(true).queue();
            return;
        }

        try {
            BROCOINS_SQL.updateCash(member, -deposit);
            BROCOINS_SQL.updateBank(member, deposit);
            event.replyEmbeds(doneEmbed(member, deposit)).setEphemeral(true).queue();
            assert member != null;
            logger.info("{}(ID:{}) deposited {} coins into his account.", member.getEffectiveName(), member.getId(), deposit);

        } catch (SQLException e) {
            assert member != null;
            logger.error("{} Tried to deposit cash, but an error has occurred.", member.getEffectiveName());
            e.printStackTrace();
            event.reply("An error has occurred. Please try again").setEphemeral(true).queue();
        }

    }

    private MessageEmbed doneEmbed(Member member, int deposit) {
        return new EmbedBuilder()
                .setTitle(broCoin.getAsMention() + " Deposit successful.")
                .setDescription("Successfully deposited " + deposit + " " + broCoin.getAsMention() + " into your account.")
                .addField("Bank Balance", BROCOINS_SQL.getBroBank(member) + " " + broCoin.getAsMention(), true)
                .addField("Cash Balance", BROCOINS_SQL.getBroCash(member) + " " + broCoin.getAsMention(), true)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    @Override
    public SlashCommandData getSlash() {
        SlashCommandData deposit = Commands.slash("deposit", "Deposit cash into your bank account.");
        deposit.addOption(OptionType.INTEGER, "amount", "How much would you like to deposit?", true);
        return deposit;
    }
}
