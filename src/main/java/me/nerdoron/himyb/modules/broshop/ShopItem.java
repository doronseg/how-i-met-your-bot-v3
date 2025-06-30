package me.nerdoron.himyb.modules.broshop;

import java.util.ArrayList;
import java.util.List;

public enum ShopItem {
    // XP Boosters
    XP_BOOSTER1("X1", "The Novice (24 Hours)", 1000),
    XP_BOOSTER2("X2", "The Novice (48 Hours)", 1200),
    XP_BOOSTER3("X3", "The Apprentice (24 Hours)", 2000),
    XP_BOOSTER4("X4", "The Apprentice (48 Hours)", 2400),
    XP_BOOSTER5("X5", "THE GRANDMASTER (1 HOUR)", 5000),
    // COIN MULTIPLIERS
    COIN_MULTIPLIER1("C1", "The Noob (24 Hours)", 1000),
    COIN_MULTIPLIER2("C2", "The Noob (48 Hours)", 1200),
    COIN_MULTIPLIER3("C3", "The Pro (24 Hours)", 2000),
    COIN_MULTIPLIER4("C4", "The Pro (48 Hours)", 2400),
    COIN_MULTIPLIER5("C5", "THE GAMBLER (1 HOUR)", 5000),
    // ROLES
    CUSTOM_ROLE1("R1", "Custom Role (1 Month)", 1000),
    CUSTOM_ROLE2("R2", "Custom Role (3 Months)", 3000),
    CUSTOM_ROLE3("R3", "Custom Role (6 Months)", 6000),
    DISPLAY_ROLE("R4", "Display Role Separately", 2000),
    //ITEMS
    COP_BRIBE("I1", "Cop Bribe (24 Hours)", 500),
    LOTTERY_CARD("I2", "Lottery Card", 1000000),
    JAIL_CARD("I3", "Get out of jail free card", 250);


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
