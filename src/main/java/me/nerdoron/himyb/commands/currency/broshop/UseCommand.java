package me.nerdoron.himyb.commands.currency.broshop;

import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.broshop.InventoryHandler;
import me.nerdoron.himyb.modules.broshop.ShopItem;
import me.nerdoron.himyb.modules.broshop.items.BribeCops;
import me.nerdoron.himyb.modules.broshop.items.CoinBoost;
import me.nerdoron.himyb.modules.broshop.items.ExpBoost;
import me.nerdoron.himyb.modules.broshop.items.JailCard;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.sql.SQLException;
import java.util.Objects;

public class UseCommand extends SlashCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String item = Objects.requireNonNull(event.getOption("item")).getAsString();

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
        Member member = event.getMember();
        assert member != null;
        try {
            if (inventoryHandler.getItemAmount(member, item) == 0) {
                event.reply("You don't have that in your inventory. Try buying it first!").setEphemeral(true).queue();
                return;
            }
            item = item.toUpperCase();
            if (item.startsWith("X")) {
                // XP
                ExpBoost expBoost = new ExpBoost();
                if (expBoost.useExpBoost(item, member, event)) {
                    inventoryHandler.removeFromInventory(member, item);
                }
            } else if (item.startsWith("C")) {
                CoinBoost coinBoost = new CoinBoost();
                if (coinBoost.useCoinBoost(item, member, event)) {
                    inventoryHandler.removeFromInventory(member, item);
                }
            } else if (item.startsWith("I")) {
                switch (item) {
                    case "I1":
                        BribeCops bribeCops = new BribeCops();
                        if (bribeCops.useBribe(member, event)) {
                            inventoryHandler.removeFromInventory(member, item);
                        }
                        break;
                    case "I2":
                        event.reply("You can't use a lottery card. Wait patiently until the lottery is rolled.").queue();
                        break;
                    case "I3":
                        JailCard jailCard = new JailCard();
                        if (jailCard.useJailCard(member, event)) {
                            inventoryHandler.removeFromInventory(member, item);
                        }
                        break;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public SlashCommandData getSlash() {
        SlashCommandData buy = Commands.slash("use", "Use an item from your inventory.");
        buy.addOption(OptionType.STRING, "item", "Which item would you like to use?", true, true);
        return buy;
    }
}
