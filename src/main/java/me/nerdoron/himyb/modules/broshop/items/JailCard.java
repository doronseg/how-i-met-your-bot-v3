package me.nerdoron.himyb.modules.broshop.items;

import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.broshop.ShopHelper;
import me.nerdoron.himyb.modules.fun.brocoins.JailHelper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;

import java.util.Objects;

public class JailCard {
    private static final Logger logger = LoggingHandler.logger(CoinBoost.class);

    public Boolean useJailCard(Member member, SlashCommandInteractionEvent event) {
        if (!JailHelper.checkIfInJail(member)) {
            event.reply("You're not jailed. You can't use this card.").setEphemeral(true).queue();
            return false;
        }
        JailHelper.unJailMember(member);
        event.reply("Successfully redeemed Jail Card. You're no longer jailed.").setEphemeral(true).queue();
        Objects.requireNonNull(Objects.requireNonNull(event.getGuild()).getTextChannelById("1296434268238516274")).sendMessageEmbeds(ShopHelper.cardRedeemed(member, "get out of jail free card.")).queue();
        logger.info("{} (ID:{}) redeemed {}.", member.getUser().getName(), member.getId(), "get out of jail card");
        return true;

    }

}
