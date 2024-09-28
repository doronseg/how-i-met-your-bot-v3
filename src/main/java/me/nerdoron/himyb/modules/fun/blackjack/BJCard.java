package me.nerdoron.himyb.modules.fun.blackjack;

import net.dv8tion.jda.api.entities.emoji.CustomEmoji;

import static net.dv8tion.jda.api.entities.emoji.Emoji.fromCustom;

public class BJCard {
    static final CustomEmoji backOfCard = fromCustom("back_of_card", 1289554822068179034L, false);
    final String[] clubs = new String[]{"997163265601835090", "997163146118697082", "997163183745806376", "997163191756918894", "997163200271360021", "997163208475410462", "997163217883246633", "997163227391733770", "997163239114821662", "997163255258693732", "997163279686303844", "997169401725993122", "997163302562045962"};
    final String[] diamonds = new String[]{"997163268672077824", "997163148228448327", "997163185616453672", "997163193623400489", "997163202301411398", "997163210958438500", "997163219804242000", "997163230474551366", "997163241174204497", "997163257863340103", "997163286938259608", "997169417945366578", "997163304365608992"};
    final String[] hearts = new String[]{"997163270840528946", "997163151579676723", "997163187482919093", "997163195506622605", "997163203932995727", "997163213227565196", "997163222052384788", "997163232567509032", "997163249210495006", "997163260522532925", "997163293443641434", "997169432277307452", "997163306622128280"};
    final String[] spades = new String[]{"997163273151582258", "997163144218689616", "997163189441671278", "997163197436002305", "997163206143397918", "997163215337312326", "997163224304717834", "997163234677235762", "997163252528185374", "997163263768920124", "997163300393582743", "997163181434740846", "997163314318671901"};
    private final int number;
    private final String type;

    public BJCard(int number, String type) {
        this.number = number;
        this.type = type;
    }

    public int getNumber() {
        return Math.min(number, 10);
    }


    public String getCard() {
        switch (type) {
            case "club":
                return "<:Card:" + clubs[number - 1] + ">";
            case "diamond":
                return "<:Card:" + diamonds[number - 1] + ">";
            case "heart":
                return "<:Card:" + hearts[number - 1] + ">";
            case "spade":
                return "<:Card:" + spades[number - 1] + ">";
            default:
                return "(" + type + ")";
        }
    }
}
