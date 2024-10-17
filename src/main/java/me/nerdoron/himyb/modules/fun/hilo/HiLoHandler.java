package me.nerdoron.himyb.modules.fun.hilo;

import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.fun.texasholdem.THHelper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static me.nerdoron.himyb.Global.*;

public class HiLoHandler {

    private static final Logger logger = LoggerFactory.getLogger(HiLoHandler.class);

    public void startHiLoGame(SlashCommandInteractionEvent event, int bet) {
        Member member = event.getMember();
        assert member != null;
        String uid = member.getId();

        List<HiLoCard> deck = gameDeck();
        List<HiLoCard> table = new LinkedList<>();

        HiLoGame game = new HiLoGame(table, bet, deck);
        HiLoHelper.startHiLoGame(member, game);
        event.getHook().editOriginalEmbeds(HiLoHelper.hiLoInit(bet)).queue();

        ArrayList<Button> preGameButtons = new ArrayList<>();
        preGameButtons.add(Button.success("HL:" + uid + ":start", "Start Game"));
        preGameButtons.add(Button.danger("HL:" + uid + ":quit", "Quit Game"));
        event.getHook().editOriginalComponents(ActionRow.of(preGameButtons)).queue();


        event.getJDA().listenOnce(ButtonInteractionEvent.class)
                .filter(buttonInteractionEvent -> buttonInteractionEvent.getChannel().getId().equals(event.getChannel().getId()))
                .timeout(TIMEOUT_DURATION, () -> {
                    event.getHook().editOriginalEmbeds(THHelper.failureToStart()).queue();
                    event.getHook().editOriginalComponents().queue();
                    COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), 5 * 60);
                    THHelper.endTHGame(member);
                })
                .subscribe(buttonInteractionEvent -> {
                    String buttonId = buttonInteractionEvent.getComponentId();
                    if (!buttonId.contains("HL:")) return;

                    String[] buttonSplit = buttonId.split(":");
                    String userId = buttonSplit[1];
                    String buttonCategory = buttonSplit[2];

                    if (!userId.equals(uid)) {
                        event.deferReply().setContent("You cannot interact with this game!").setEphemeral(true).queue();
                        return;
                    }

                    switch (buttonCategory) {
                        case "start":
                            buttonInteractionEvent.deferEdit().queue();
                            event.getHook().editOriginalComponents().queue();
                            game.setBet(bet);
                            handleFirstRound(event, game, member);
                            break;
                        case "quit":
                            buttonInteractionEvent.deferEdit().queue();
                            event.getHook().editOriginalEmbeds(THHelper.quitGame()).queue();
                            event.getHook().editOriginalComponents().queue();
                            THHelper.endTHGame(member);
                            break;
                    }

                });
    }

    private void handleFirstRound(SlashCommandInteractionEvent event, HiLoGame game, Member member) {
        HiLoCard firstCard = drawCard(game);
        List<HiLoCard> table = game.getTable();
        String uid = member.getId();
        table.add(firstCard);

        HiLoCard newCard = drawCard(game);
        table.add(newCard);
        if (tie(firstCard, newCard)) {
            HiLoHelper.endHiLoGame(member);
            try {
                BROCOINS_SQL.updateCashMultiplier(member, event, game.getWinnings() - game.getBet());
                event.getHook().editOriginalComponents().queue();
                event.getHook().editOriginalEmbeds(HiLoHelper.tie(game.getWinnings(), newCard, firstCard)).queue();
                logger.info("{}(ID:{}) played HiLo, and tied on the first round. Not winning anything.", member.getUser().getName(), uid);
            } catch (SQLException e) {
                MessageEmbed err = HiLoHelper.createErrorEmbed(e.getMessage());
                event.getHook().editOriginalEmbeds(err).queue();
                e.printStackTrace();
            }
            return;
        }

        event.getHook().editOriginalEmbeds(HiLoHelper.hiLoFirst(game.getBet(), firstCard)).queue();
        event.getHook().editOriginalComponents(ActionRow.of(getButtons(uid))).queue();
        game.setNoOfRounds(1);

        gameListener(event, game, member, uid, firstCard, newCard, 1);

    }

    private void handleGame(SlashCommandInteractionEvent event, HiLoGame game, Member member) {
        int noOfRounds = game.getNoOfRounds();
        HiLoCard newCard = drawCard(game);
        List<HiLoCard> table = game.getTable();
        String uid = member.getId();
        table.add(newCard);
        HiLoCard previousCard = table.get(table.size() - 2);


        event.getHook().editOriginalEmbeds(HiLoHelper.hiLoGame(game.getWinnings(), noOfRounds, previousCard, tableCards(noOfRounds))).queue();
        event.getHook().editOriginalComponents(ActionRow.of(getButtons(uid))).queue();

        // game listener
        if (tie(previousCard, newCard)) {
            COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), 5 * 60);
            HiLoHelper.endHiLoGame(member);
            try {
                BROCOINS_SQL.updateCashMultiplier(member, event, game.getWinnings() - game.getBet());
                event.getHook().editOriginalComponents().queue();
                event.getHook().editOriginalEmbeds(HiLoHelper.tie(game.getWinnings(), previousCard, newCard)).queue();
                logger.info("{}(ID:{}) played HiLo, and tied. Winning {}", member.getUser().getName(), uid, game.getWinnings());
            } catch (SQLException e) {
                MessageEmbed err = HiLoHelper.createErrorEmbed(e.getMessage());
                event.getHook().editOriginalEmbeds(err).queue();
                e.printStackTrace();
            }
            return;
        }

        gameListener(event, game, member, uid, previousCard, newCard, noOfRounds);

    }

    private void gameListener(SlashCommandInteractionEvent event, HiLoGame game, Member member, String uid, HiLoCard newCard, HiLoCard nextCard, int noOfRounds) {
        event.getJDA().listenOnce(ButtonInteractionEvent.class)
                .filter(buttonInteractionEvent -> buttonInteractionEvent.getChannel().getId().equals(event.getChannel().getId()))
                .timeout(TIMEOUT_DURATION, () -> {
                    event.getHook().editOriginalEmbeds(THHelper.failureToStart()).queue();
                    event.getHook().editOriginalComponents().queue();
                    COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), 5 * 60);
                    THHelper.endTHGame(member);
                })
                .subscribe(buttonInteractionEvent -> {
                    String buttonId = buttonInteractionEvent.getComponentId();
                    if (!buttonId.contains("HL:")) return;

                    String[] buttonSplit = buttonId.split(":");
                    String userId = buttonSplit[1];
                    String buttonCategory = buttonSplit[2];

                    if (!userId.equals(uid)) {
                        event.deferReply().setContent("You cannot interact with this game!").setEphemeral(true).queue();
                        return;
                    }

                    int bet = game.getBet();
                    int winnings;
                    if (noOfRounds == 1) {
                        winnings = bet * 2;
                    } else {
                        winnings = game.getWinnings() * 2;
                    }

                    switch (buttonCategory) {
                        case "higher":
                            higher(event, game, member, uid, newCard, nextCard, noOfRounds, buttonInteractionEvent, winnings);
                            break;
                        case "lower":
                            lower(event, game, member, uid, newCard, nextCard, noOfRounds, buttonInteractionEvent, winnings);
                            break;
                        case "cash":
                            cashOut(event, game, member, uid, buttonInteractionEvent);
                            break;
                    }
                });
    }

    private void higher(SlashCommandInteractionEvent event, HiLoGame game, Member member, String uid, HiLoCard newCard, HiLoCard nextCard, int noOfRounds, ButtonInteractionEvent buttonInteractionEvent, int winnings) {
        buttonInteractionEvent.deferEdit().queue();
        if (higherOrLower(nextCard, newCard)) {
            game.setWinnings(winnings);
            game.setNoOfRounds(noOfRounds + 1);
            handleGame(event, game, member);
            return;
        }
        try {
            BROCOINS_SQL.updateCashWithoutMultiplier(member, -game.getBet());
            event.getHook().editOriginalComponents().queue();
            COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), 5 * 60);
            event.getHook().editOriginalEmbeds(HiLoHelper.hiLoLose(game.getWinnings(), nextCard, newCard)).queue();
            logger.info("{}(ID:{}) played HiLo, chose higher, and lost {}", member.getUser().getName(), uid, game.getBet());

        } catch (SQLException e) {
            MessageEmbed err = THHelper.createErrorEmbed(e.getMessage());
            event.getHook().editOriginalEmbeds(err).queue();
            e.printStackTrace();
        }
        HiLoHelper.endHiLoGame(member);
    }

    private void lower(SlashCommandInteractionEvent event, HiLoGame game, Member member, String uid, HiLoCard newCard, HiLoCard nextCard, int noOfRounds, ButtonInteractionEvent buttonInteractionEvent, int winnings) {
        buttonInteractionEvent.deferEdit().queue();
        if (!higherOrLower(nextCard, newCard)) {
            game.setWinnings(winnings);
            game.setNoOfRounds(noOfRounds + 1);
            handleGame(event, game, member);
            return;
        }
        try {
            BROCOINS_SQL.updateCashWithoutMultiplier(member, -game.getBet());
            event.getHook().editOriginalComponents().queue();
            COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), 5 * 60);
            event.getHook().editOriginalEmbeds(HiLoHelper.hiLoLose(game.getWinnings(), nextCard, newCard)).queue();
            logger.info("{}(ID:{}) played HiLo, chose lower, and lost {}", member.getUser().getName(), uid, game.getBet());
        } catch (SQLException e) {
            MessageEmbed err = HiLoHelper.createErrorEmbed(e.getMessage());
            event.getHook().editOriginalEmbeds(err).queue();
            e.printStackTrace();
        }
        HiLoHelper.endHiLoGame(member);
    }

    private void cashOut(SlashCommandInteractionEvent event, HiLoGame game, Member member, String uid, ButtonInteractionEvent buttonInteractionEvent) {
        buttonInteractionEvent.deferEdit().queue();
        COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), 5 * 60);
        HiLoHelper.endHiLoGame(member);
        try {
            BROCOINS_SQL.updateCashMultiplier(member, event, game.getWinnings());
            event.getHook().editOriginalComponents().queue();
            event.getHook().editOriginalEmbeds(HiLoHelper.hiLoCashOut(game.getWinnings())).queue();
            logger.info("{}(ID:{}) played HiLo, and won {}", member.getUser().getName(), uid, game.getWinnings());
        } catch (SQLException e) {
            MessageEmbed err = HiLoHelper.createErrorEmbed(e.getMessage());
            event.getHook().editOriginalEmbeds(err).queue();
            e.printStackTrace();
        }
    }

    Boolean higherOrLower(HiLoCard newCard, HiLoCard previousCard) {
        int newCardNumber = newCard.getNumber();
        int previousCardNumber = previousCard.getNumber();
        return newCardNumber > previousCardNumber;
    }

    Boolean tie(HiLoCard newCard, HiLoCard previousCard) {
        int newCardNumber = newCard.getNumber();
        int previousCardNumber = previousCard.getNumber();
        return newCardNumber == previousCardNumber;
    }

    String tableCards(int noOfRounds) {
        StringBuilder tableCards = new StringBuilder();
        for (int i = 0; i < noOfRounds - 1; i++) {
            tableCards.append(HiLoCard.backOfCard.getAsMention()).append(" ");
        }
        return tableCards.toString();
    }


    ArrayList<Button> getButtons(String uid) {
        ArrayList<Button> hiLoButtons = new ArrayList<>();
        hiLoButtons.add(Button.primary("HL:" + uid + ":higher", "Higher"));
        hiLoButtons.add(Button.primary("HL:" + uid + ":lower", "Lower"));
        hiLoButtons.add(Button.danger("HL:" + uid + ":cash", "Cash out"));
        return hiLoButtons;
    }

    List<HiLoCard> gameDeck() {
        List<HiLoCard> deck = new LinkedList<>();
        for (int i = 1; i <= 13; i++) {
            deck.add(new HiLoCard(i, "Club"));
            deck.add(new HiLoCard(i, "Diamond"));
            deck.add(new HiLoCard(i, "Heart"));
            deck.add(new HiLoCard(i, "Spade"));
        }
        Collections.shuffle(deck);
        return deck;
    }

    HiLoCard drawCard(HiLoGame game) {
        List<HiLoCard> deck = game.getDeck();
        HiLoCard hiLoCard = deck.get(0);
        deck.remove(0);

        return hiLoCard;
    }
}
