package me.nerdoron.himyb.modules.broshop;

import me.nerdoron.himyb.Global;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.sql.SQLException;
import java.util.ArrayList;

import static me.nerdoron.himyb.modules.broshop.ShopItem.*;

public class ShopHelper {

    public static MessageEmbed shopMain() {
        return new EmbedBuilder()
                .setTitle(Global.broCoin.getAsMention() + " BroShop™")
                .setDescription("Welcome to the BroShop™. Please select a category.")
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed shopXp() {
        return new EmbedBuilder()
                .setTitle("\uD83D\uDCC8 XP Boosters")
                .setDescription("A list of all available XP Boosters you can buy. Prices are TBD." +
                        "\nTo buy an item, use `/buy X1|X2|X3|X4|X5`.\nBoosters are not stackable.")
                .addField(String.format("`%s` %s", XP_BOOSTER1.getType(), XP_BOOSTER1.getName()),
                        String.format("Grants an additional experience point for every message sent for 24 hours.\n%d %s", XP_BOOSTER1.getPrice(), Global.broCoin.getAsMention()),
                        false)
                .addField(String.format("`%s` %s", XP_BOOSTER2.getType(), XP_BOOSTER2.getName()),
                        String.format("Grants an additional experience point for every message sent for 48 hours.\n%d %s", XP_BOOSTER2.getPrice(), Global.broCoin.getAsMention()),
                        false)
                .addField(String.format("`%s` %s", XP_BOOSTER3.getType(), XP_BOOSTER3.getName()),
                        String.format("Grants two additional experience points for every message sent for 24 hours.\n%d %s", XP_BOOSTER3.getPrice(), Global.broCoin.getAsMention()),
                        false)
                .addField(String.format("`%s` %s", XP_BOOSTER4.getType(), XP_BOOSTER4.getName()),
                        String.format("Grants two additional experience points for every message sent for 48 hours.\n%d %s", XP_BOOSTER4.getPrice(), Global.broCoin.getAsMention()),
                        false)
                .addField(String.format("`%s` %s", XP_BOOSTER5.getType(), XP_BOOSTER5.getName()),
                        String.format("Grants *FIVE* additional experience points for every message sent for **1 hour**.\n%d %s", XP_BOOSTER5.getPrice(), Global.broCoin.getAsMention()),
                        false)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed shopCoin() {
        return new EmbedBuilder()
                .setTitle(Global.broCoin.getAsMention() + " Coin Multipliers")
                .setDescription("A list of all available Coin multiplier you can buy. Prices are TBD." +
                        "\nTo buy an item, use `/buy C1|C2|C3|C4|C5`.\nMultipliers are not stackable.")
                .addField(String.format("`%s` %s", COIN_MULTIPLIER1.getType(), COIN_MULTIPLIER1.getName()),
                        String.format("Puts a 125%% multiplier on all earned coins for 24 hours.\n%d %s", COIN_MULTIPLIER1.getPrice(), Global.broCoin.getAsMention()),
                        false)
                .addField(String.format("`%s` %s", COIN_MULTIPLIER2.getType(), COIN_MULTIPLIER2.getName()),
                        String.format("Puts a 125%% multiplier on all earned coins for 48 hours.\n%d %s", COIN_MULTIPLIER2.getPrice(), Global.broCoin.getAsMention()),
                        false)
                .addField(String.format("`%s` %s", COIN_MULTIPLIER3.getType(), COIN_MULTIPLIER3.getName()),
                        String.format("Puts a 150%% multiplier on all earned coins for 24 hours.\n%d %s", COIN_MULTIPLIER3.getPrice(), Global.broCoin.getAsMention()),
                        false)
                .addField(String.format("`%s` %s", COIN_MULTIPLIER4.getType(), COIN_MULTIPLIER4.getName()),
                        String.format("Puts a 150%% multiplier on all earned coins for 48 hours.\n%d %s", COIN_MULTIPLIER4.getPrice(), Global.broCoin.getAsMention()),
                        false)
                .addField(String.format("`%s` %s", COIN_MULTIPLIER5.getType(), COIN_MULTIPLIER5.getName()),
                        String.format("Puts a *250%%* multiplier on all earned coins **1 hour**.\n%d %s", COIN_MULTIPLIER5.getPrice(), Global.broCoin.getAsMention()),
                        false)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed roleEmbed() {
        return new EmbedBuilder()
                .setTitle("✨ Custom Roles")
                .setColor(Global.embedColor)
                .setDescription("A list of all available custom roles you can buy. Prices are TBD." +
                        "\nTo buy an item, use `/buy R1|R2|R3|R4`.")
                .addField(String.format("`%s` %s", CUSTOM_ROLE1.getType(), CUSTOM_ROLE1.getName()),
                        String.format("Grants you a cosmetic custom role for a month.\n%d %s", CUSTOM_ROLE1.getPrice(), Global.broCoin.getAsMention()),
                        false)
                .addField(String.format("`%s` %s", CUSTOM_ROLE2.getType(), CUSTOM_ROLE2.getName()),
                        String.format("Grants you a cosmetic custom role for three months.\n%d %s", CUSTOM_ROLE2.getPrice(), Global.broCoin.getAsMention()),
                        false)
                .addField(String.format("`%s` %s", CUSTOM_ROLE3.getType(), CUSTOM_ROLE3.getName()),
                        String.format("Grants you a cosmetic custom role for six months.\n%d %s", CUSTOM_ROLE3.getPrice(), Global.broCoin.getAsMention()),
                        false)
                .addField(String.format("`%s` %s", DISPLAY_ROLE.getType(), DISPLAY_ROLE.getName()),
                        String.format("Display your custom role separately, could be any role, whether given from the item shop, or from supporter rewards.\n%d %s", DISPLAY_ROLE.getPrice(), Global.broCoin.getAsMention()),
                        false)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }


    public static MessageEmbed itemEmbed() {
        return new EmbedBuilder()
                .setTitle("\uD83D\uDCBC Items")
                .setColor(Global.embedColor)
                .setDescription("A list of all available items you can buy. Prices are TBD." +
                        "\nTo buy an item, use `/buy I1|I2|I3`.")
                .addField(String.format("`%s` %s", COP_BRIBE.getType(), COP_BRIBE.getName()),
                        String.format("Cops have a 50%% chance to look the other way when you're commiting crimes.\n%d %s", COP_BRIBE.getPrice(), Global.broCoin.getAsMention()),
                        false)
                .addField(String.format("`%s` %s", LOTTERY_CARD.getType(), LOTTERY_CARD.getName()),
                        String.format("Buy an entry to this month's lottery.\n%d %s", LOTTERY_CARD.getPrice(), Global.broCoin.getAsMention()),
                        false)
                .addField(String.format("`%s` %s", JAIL_CARD.getType(), JAIL_CARD.getName()),
                        String.format("If you get arrested, use this card to get out of jail\n%d %s", JAIL_CARD.getPrice(), Global.broCoin.getAsMention()),
                        false)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed inventoryEmbed(Member member) throws SQLException {
        InventoryHandler inventoryHandler = new InventoryHandler();
        return new EmbedBuilder()
                .setTitle("\uD83D\uDCBC Inventory")
                .setDescription("Your inventory, to use an item use `/use [ID]`.")
                .addField("\uD83D\uDCC8 XP Boosters",
                        String.format("`X1` **24-Hour Novice Booster** - %d\n" +
                                        "`X2` - **48-Hour Novice Booster** - %d\n" +
                                        "`X3` **24-Hour Apprentice Booster** - %d\n" +
                                        "`X4` **48-Hour Apprentice Booster** - %d\n" +
                                        "`X5` **Grandmaster Booster** - %d",
                                inventoryHandler.getItemAmount(member, "X1"), inventoryHandler.getItemAmount(member, "X2"),
                                inventoryHandler.getItemAmount(member, "X3"), inventoryHandler.getItemAmount(member, "X4"),
                                inventoryHandler.getItemAmount(member, "X5")), false)
                .addField(Global.broCoin.getAsMention() + " Coin Multipliers",
                        String.format("`C1` **24-Hour Noob Multipliers** - %d\n" +
                                        "`C2` **48-Hour Noob Multipliers** - %d\n" +
                                        "`C3` **24-Hour Pro Multipliers** - %d\n" +
                                        "`C4` **48-Hour Pro Multipliers** - %d\n" +
                                        "`C5` - **Gambler Multipliers** - %d",
                                inventoryHandler.getItemAmount(member, "C1"), inventoryHandler.getItemAmount(member, "C2"),
                                inventoryHandler.getItemAmount(member, "C3"), inventoryHandler.getItemAmount(member, "C4"),
                                inventoryHandler.getItemAmount(member, "C5")), false)
                .addField("\uD83D\uDCBC Items",
                        String.format("`I1` **24-Hour Cop Bribe** - %d\n" +
                                        "`I2` **Lottery Cards** (Unusable) - %d\n" +
                                        "`I3` - **Get Out Of Jail Free Cards** - %d",
                                inventoryHandler.getItemAmount(member, "I1"), inventoryHandler.getItemAmount(member, "I2"), inventoryHandler.getItemAmount(member, "I3")),
                        false)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed purchaseLog(Member member, ShopItem shopItem, String id) {
        return new EmbedBuilder()
                .setTitle("BroShop Purchase Made")
                .setDescription(String.format("%s `(ID:%s)` purchased one of item `%s`.", member.getAsMention(), member.getId(), shopItem.getName()))
                .addField("Purchase ID", id, false)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed boostRedeemed(Member member, String item) {
        return new EmbedBuilder()
                .setTitle("Inventory Item Redeemed")
                .setDescription(String.format("%s `(ID:%s)` redeemed boost %s", member.getAsMention(), member.getId(), item))
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed cardRedeemed(Member member, String item) {
        return new EmbedBuilder()
                .setTitle("Inventory Item Redeemed")
                .setDescription(String.format("%s `(ID:%s)` redeemed %s", member.getAsMention(), member.getId(), item))
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed boostEnd(Member member, String item) {
        return new EmbedBuilder()
                .setTitle("Booster Ended")
                .setDescription(String.format("Boost %s ended for %s `(ID:%s)`.", item, member.getAsMention(), member.getId()))
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed createErrorEmbed(String errorMessage) {
        return new EmbedBuilder()
                .setTitle("Error")
                .setDescription("An error has occurred, don't worry though, you didn't lose any BroCoins. Error message: `" + errorMessage + "`.")
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }


    public static ArrayList<Button> getShopButtons(String uid) {
        ArrayList<Button> shopButtons = new ArrayList<>();
        shopButtons.add(Button.secondary("SHOP:" + uid + ":main", "\uD83D\uDD2E Main Menu"));
        shopButtons.add(Button.secondary("SHOP:" + uid + ":xp", "\uD83D\uDCC8 XP Boosters"));
        shopButtons.add(Button.secondary("SHOP:" + uid + ":coin", "\uD83E\uDE99 BroCoin Boosters"));
        shopButtons.add(Button.secondary("SHOP:" + uid + ":roles", "✨ Custom Roles"));
        shopButtons.add(Button.secondary("SHOP:" + uid + ":items", "\uD83D\uDCBC Items"));
        return shopButtons;
    }


}
