package me.nerdoron.himyb.commands.currency.broshop;

import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.broshop.InventoryHandler;
import me.nerdoron.himyb.modules.broshop.ShopItem;
import me.nerdoron.himyb.modules.broshop.items.GiveItem;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.Objects;

public class GiveItemCommand extends SlashCommand {
    private static final Logger logger = LoggingHandler.logger(GiveItem.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        assert member != null;
        Member memberToTransferTo = Objects.requireNonNull(event.getOption("user")).getAsMember();
        assert memberToTransferTo != null;
        String item = Objects.requireNonNull(event.getOption("item")).getAsString();

        ShopItem selectedItem = null;
        for (ShopItem shopItem : ShopItem.values()) {
            if (shopItem.getType().equalsIgnoreCase(item)) {
                selectedItem = shopItem;
                break;
            }
        }

        if (memberToTransferTo.getUser().isBot()) {
            event.reply("You can't give items to a bot!").setEphemeral(true).queue();
            return;
        }

        if (selectedItem == null) {
            event.reply("Item not found. Please make sure you have the correct item ID.").setEphemeral(true).queue();
            return;
        }

        InventoryHandler inventoryHandler = new InventoryHandler();
        try {
            if (inventoryHandler.getItemAmount(member, item) == 0) {
                event.reply("You don't have that in your inventory. Try buying it first!").setEphemeral(true).queue();
                return;
            }

            item = item.toUpperCase();
            GiveItem giveItem = new GiveItem();
            if (giveItem.giveItem(member, memberToTransferTo, selectedItem.getName(), event)) {
                inventoryHandler.removeFromInventory(member, item);
                inventoryHandler.addToInventory(memberToTransferTo, item);
                logger.info("{} (ID:{}) gave {} to {}", member.getUser().getName(), member.getId(), item, memberToTransferTo.getUser().getName());
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SlashCommandData getSlash() {
        SlashCommandData giveitem = Commands.slash("give-item", "Give cash to another user.");
        giveitem.addOption(OptionType.STRING, "item", "Which item would you like to give?", true, true);
        giveitem.addOption(OptionType.USER, "user", "who would you like to give to?", true);
        return giveitem;
    }
}
