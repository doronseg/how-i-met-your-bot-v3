package me.nerdoron.himyb.modules.broshop.items;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.broshop.ShopHelper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;

import java.util.Objects;

import static me.nerdoron.himyb.Global.COOLDOWN_MANAGER;

public class BribeCops {
    private static final Logger logger = LoggingHandler.logger(CoinBoost.class);

    public Boolean useBribe(Member member, SlashCommandInteractionEvent event) {
        if (COOLDOWN_MANAGER.hasCooldown(CooldownManager.bribeCooldown(member))) {
            String remaining = COOLDOWN_MANAGER.parseCooldown(CooldownManager.bribeCooldown(member));
            event.reply(String.format("You already have an active cop bribe. Try again in %s.", remaining)).queue();
            return false;
        }

        event.reply(":cop:: Thank you for your contribution. I'll try to make sure we don't jail you.").setEphemeral(true).queue();
        COOLDOWN_MANAGER.addCooldown(CooldownManager.bribeCooldown(member), Global.DAY_IN_SECONDS);
        Objects.requireNonNull(Objects.requireNonNull(event.getGuild()).getTextChannelById("1296434268238516274")).sendMessageEmbeds(ShopHelper.cardRedeemed(member, "bribe card.")).queue();
        logger.info("{} (ID:{}) redeemed {}.", member.getUser().getName(), member.getId(), "bribe card");
        return true;
    }

}
