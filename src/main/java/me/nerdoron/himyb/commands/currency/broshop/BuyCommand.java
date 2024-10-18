package me.nerdoron.himyb.commands.currency.broshop;

import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.broshop.InventoryHandler;
import me.nerdoron.himyb.modules.broshop.ShopItem;
import me.nerdoron.himyb.modules.fun.brocoins.JailHelper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

public class BuyCommand extends SlashCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String item = Objects.requireNonNull(event.getOption("item")).getAsString();

        Member member = event.getMember();

        if (JailHelper.checkIfInJail(member)) {
            event.replyEmbeds(JailHelper.inJailEmbed(member)).queue();
            return;
        }

        ShopItem selectedItem = null;
        for (ShopItem shopItem : ShopItem.values()) {
            if (shopItem.getType().equalsIgnoreCase(item)) {
                selectedItem = shopItem;
                break;
            }
        }

        if (selectedItem == null) {
            event.reply("Item not found. Please make sure you have the correct item ID.").setEphemeral(true).queue();
            return;
        }

        InventoryHandler inventoryHandler = new InventoryHandler();
        inventoryHandler.buyItem(event, member, selectedItem);
    }

    @Override
    public SlashCommandData getSlash() {
        SlashCommandData buy = Commands.slash("buy", "Buy an item from the BroShop™.");
        buy.addOption(OptionType.STRING, "item", "What would you like to buy?", true, true);
        return buy;
    }
}