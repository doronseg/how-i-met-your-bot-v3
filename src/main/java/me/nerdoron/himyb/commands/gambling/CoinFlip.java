package me.nerdoron.himyb.commands.gambling;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.bot.Rng;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.fun.brocoins.JailHelper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;

import java.io.File;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static me.nerdoron.himyb.Global.BROCOINS_SQL;
import static me.nerdoron.himyb.Global.COOLDOWN_MANAGER;

public class CoinFlip extends SlashCommand {
    private static final Logger logger = LoggingHandler.logger(CoinFlip.class);

    @SuppressWarnings("LoggingSimilarMessage")
    @Override
    public void execute(SlashCommandInteractionEvent event) {

        String type = Objects.requireNonNull(event.getOption("type")).getAsString();
        int bet = Objects.requireNonNull(event.getOption("bet")).getAsInt();
        int rng = Rng.generateNumber(1, 2);
        Member member = event.getMember();
        assert member != null;
        if (JailHelper.checkIfInJail(member)) {
            event.replyEmbeds(JailHelper.inJailEmbed(member)).queue();
            return;
        }
        if (COOLDOWN_MANAGER.hasCooldown(CooldownManager.commandID(event))) {
            String remaining = COOLDOWN_MANAGER.parseCooldown(CooldownManager.commandID(event));
            event.reply("Don't pull a Kira and get addicted to gambling! You can do it again in " + remaining)
                    .setEphemeral(true).queue();
            return;
        }
        String result = "";
        if (rng == 1) result = "Heads";
        if (rng == 2) result = "Tails";

        if (bet > BROCOINS_SQL.getBroCash(event.getMember())) {
            event.reply("You don't have enough cash!").setEphemeral(true).queue();
            return;
        }

        File video = getHeadsOrTails(rng);
        FileUpload file = FileUpload.fromData(video);
        event.reply(
                        String.format("You bet %d %s on %s Let's see if you won..", bet, Global.broCoin.getAsMention(), result))
                .addFiles(file)
                .queue();


        if (type.equals(result)) {
            // win
            try {
                BROCOINS_SQL.updateCash(event.getMember(), bet);
                event.getHook().editOriginal(String.format("%s bet %d %s on a coin flip, won, and doubled their bet!", member.getAsMention(), bet, Global.broCoin.getAsMention())).queueAfter(10, TimeUnit.SECONDS);
                logger.info("{}(ID:{}) won a coin flip while betting {}.", event.getUser().getAsTag(), event.getMember().getId(), bet);
                COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), Global.HOUR_IN_SECONDS / 2);
            } catch (SQLException e) {
                logger.error("{}(ID:{}) Tried to flip a coin, but an error has occurred.", member.getUser().getName(), member.getId());
                e.printStackTrace();
                event.reply("An error has occurred. Don't worry, you haven't lost your bet.").setEphemeral(true).queue();
            }
        } else {
            try {
                BROCOINS_SQL.updateCash(event.getMember(), -(bet));
                event.getHook().editOriginal((String.format("%s bet %d %s on a coin flip, lost, and lost their bet!", member.getAsMention(), bet, Global.broCoin.getAsMention()))).queueAfter(10, TimeUnit.SECONDS);
                logger.info("{}(ID:{}) lost a coin flip while betting {}.", event.getUser().getAsTag(), event.getMember().getId(), bet);
                Global.COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), Global.HOUR_IN_SECONDS / 2);
            } catch (SQLException e) {
                logger.error("{}(ID:{}) Tried to flip a coin, but an error has occurred.", member.getUser().getName(), member.getId());
                e.printStackTrace();
                event.reply("An error has occurred. Please try again").setEphemeral(true).queue();
            }
        }
    }

    @Override
    public SlashCommandData getSlash() {
        SlashCommandData coinflip = Commands.slash("coinflip", "Bet on a coin flip.");
        OptionData bet = new OptionData(OptionType.INTEGER, "bet", "How much do you want to bet?", true);
        bet.setMinValue(15);
        OptionData heads_tails = new OptionData(OptionType.STRING, "type", "Heads or tails?", true);
        heads_tails.addChoice("heads", "Heads");
        heads_tails.addChoice("tails", "Tails");

        coinflip.addOptions(bet, heads_tails);
        return coinflip;
    }


    private File getHeadsOrTails(int rng) {
        if (rng == 1) {
            // Heads
            return new File("videos/coinflip1.mp4");
        } else
            //tails
            return new File("videos/coinflip2.mp4");

    }
}
