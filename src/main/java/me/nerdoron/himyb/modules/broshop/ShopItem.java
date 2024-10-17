package me.nerdoron.himyb.modules.broshop;

import java.util.ArrayList;
import java.util.List;

public enum ShopItem {
    // XP Boosters
    XP_BOOSTER1("X1", "The Novice (24 Hours)", 10),
    XP_BOOSTER2("X2", "The Novice (48 Hours)", 20),
    XP_BOOSTER3("X3", "The Apprentice (24 Hours)", 30),
    XP_BOOSTER4("X4", "The Apprentice (48 Hours)", 40),
    XP_BOOSTER5("X5", "THE GRANDMASTER (1 HOUR)", 50),
    // COIN MULTIPLIERS
    COIN_MULTIPLIER1("C1", "The Noob (24 Hours)", 60),
    COIN_MULTIPLIER2("C2", "The Noob (48 Hours)", 70),
    COIN_MULTIPLIER3("C3", "The Pro (24 Hours)", 80),
    COIN_MULTIPLIER4("C4", "The Pro (48 Hours)", 90),
    COIN_MULTIPLIER5("C5", "THE GAMBLER (1 HOUR)", 100),
    // ROLES
    CUSTOM_ROLE1("R1", "Custom Role (1 Month)", 110),
    CUSTOM_ROLE2("R2", "Custom Role (3 Months)", 120),
    CUSTOM_ROLE3("R3", "Custom Role (6 Months)", 130),
    DISPLAY_ROLE("R4", "Display Role Separately", 140),
    //ITEMS
    COP_BRIBE("I1", "Cop Bribe (24 Hours)", 150),
    LOTTERY_CARD("I2", "Lottery Card", 160),
    JAIL_CARD("I3", "Get out of jail free card", 170);


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
