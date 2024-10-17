package me.nerdoron.himyb.commands.currency.broshop;

import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.broshop.ShopHelper;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.Objects;

public class BroShopCommand extends SlashCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String uid = Objects.requireNonNull(event.getMember()).getId();
        event.replyEmbeds(ShopHelper.shopMain()).queue();
        event.getHook().editOriginalComponents(ActionRow.of(ShopHelper.getShopButtons(uid))).queue();
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("broshop", "Open the BroShop™.");
    }

}
