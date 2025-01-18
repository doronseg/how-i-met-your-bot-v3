package me.nerdoron.himyb.modules.broshop;

import java.util.ArrayList;
import java.util.List;

public enum ShopItem {
    // XP Boosters
    XP_BOOSTER1("X1", "The Novice (24 Hours)", 1000000000),
    XP_BOOSTER2("X2", "The Novice (48 Hours)", 2000000000),
    XP_BOOSTER3("X3", "The Apprentice (24 Hours)", 30000000),
    XP_BOOSTER4("X4", "The Apprentice (48 Hours)", 4000000),
    XP_BOOSTER5("X5", "THE GRANDMASTER (1 HOUR)", 500000000),
    // COIN MULTIPLIERS
    COIN_MULTIPLIER1("C1", "The Noob (24 Hours)", 60000000),
    COIN_MULTIPLIER2("C2", "The Noob (48 Hours)", 700000000),
    COIN_MULTIPLIER3("C3", "The Pro (24 Hours)", 80000000),
    COIN_MULTIPLIER4("C4", "The Pro (48 Hours)", 900000000),
    COIN_MULTIPLIER5("C5", "THE GAMBLER (1 HOUR)", 100000000),
    // ROLES
    CUSTOM_ROLE1("R1", "Custom Role (1 Month)", 1100000000),
    CUSTOM_ROLE2("R2", "Custom Role (3 Months)", 1200000000),
    CUSTOM_ROLE3("R3", "Custom Role (6 Months)", 1300000000),
    DISPLAY_ROLE("R4", "Display Role Separately", 1400000000),
    //ITEMS
    COP_BRIBE("I1", "Cop Bribe (24 Hours)", 1500000000),
    LOTTERY_CARD("I2", "Lottery Card", 1600000000),
    JAIL_CARD("I3", "Get out of jail free card", 1700000000);


    private final String type;
    private final String name;
    private final int price;

    ShopItem(String type, String name, int price) {
        this.type = type;
        this.name = name;
        this.price = price;
    }

    public static List<String> allItemTypes() {
        List<String> itemTypes = new ArrayList<>();
        for (ShopItem item : ShopItem.values()) {
            itemTypes.add(item.getType());
        }
        return itemTypes;
    }

    public int getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
