package me.nerdoron.himyb.modules.fun.texasholdem;

import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static me.nerdoron.himyb.Global.*;

public class THHandler {
    private static final Logger logger = LoggingHandler.logger(THHandler.class);

    public void initGame(SlashCommandInteractionEvent event, int blind, int ante, int trips) {
        Member member = event.getMember();
        assert member != null;
        String uid = member.getId();

        // init cards
        List<THCard> deck = gameDeck();
        List<THCard> playerCards = new LinkedList<>();
        List<THCard> dealerCards = new LinkedList<>();
        List<THCard> table = new LinkedList<>();

        // deal cards
        playerCards.add(drawCard(deck));
        dealerCards.add(drawCard(deck));
        playerCards.add(drawCard(deck));
        dealerCards.add(drawCard(deck));

        int bet = blind + ante + trips;


        // start game
        THGame game = new THGame(dealerCards, playerCards, table, deck, bet, blind, ante, trips);
        THHelper.startTHGame(member, game);
        event.getHook().editOriginalEmbeds(THHelper.texasMain(blind, ante, trips)).queue();

        // add buttons
        ArrayList<Button> preGameButtons = new ArrayList<>();
        preGameButtons.add(Button.success("TH:" + uid + ":start", "Start Game"));
        preGameButtons.add(Button.danger("TH:" + uid + ":quit", "Quit Game"));
        event.getHook().editOriginalComponents(ActionRow.of(preGameButtons)).queue();

        // listener
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
                    if (!buttonId.contains("TH:")) return;

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
                            preFlop(member, uid, event, game);
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

    private void preFlop(Member member, String uid, SlashCommandInteractionEvent event, THGame game) {

        // definitions
        List<THCard> playerCards = game.getPlayerCards();
        int blind = game.getBlind();
        int ante = game.getAnte();
        int trips = game.getTrips();
        int bet = game.getBet();

        // buttons
        ArrayList<Button> preFlopButtons = new ArrayList<>();
        preFlopButtons.add(Button.success("TH:" + uid + ":check", "Check"));
        preFlopButtons.add(Button.primary("TH:" + uid + ":three", "Bet 3x"));
        preFlopButtons.add(Button.primary("TH:" + uid + ":four", "Bet 4x"));

        // hook
        event.getHook().editOriginalEmbeds(THHelper.preFlop(playerCards, blind, ante, trips)).queue();
        event.getHook().editOriginalComponents(ActionRow.of(preFlopButtons)).queue();

        // listener
        event.getJDA().listenOnce(ButtonInteractionEvent.class)
                .filter(buttonInteractionEvent -> buttonInteractionEvent.getChannel().getId().equals(event.getChannel().getId()))
                .timeout(TIMEOUT_DURATION, () -> {
                    event.getHook().editOriginalEmbeds(THHelper.THTimeout(bet)).queue();
                    event.getHook().editOriginalComponents().queue();
                    COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), 5 * 60);
                    try {
                        BROCOINS_SQL.updateCashWithoutMultiplier(member, -bet);
                    } catch (SQLException e) {
                        MessageEmbed err = THHelper.createErrorEmbed(e.getMessage());
                        event.getHook().editOriginalEmbeds(err).queue();
                        e.printStackTrace();
                    }
                    THHelper.endTHGame(member);
                })
                .subscribe(buttonInteractionEvent -> {
                    String buttonId = buttonInteractionEvent.getComponentId();
                    if (!buttonId.contains("TH:")) return;

                    String[] buttonSplit = buttonId.split(":");
                    String userId = buttonSplit[1];
                    String buttonCategory = buttonSplit[2];

                    if (!userId.equals(uid)) {
                        event.deferReply().setContent("You cannot interact with this game!").setEphemeral(true).queue();
                        return;
                    }

                    switch (buttonCategory) {
                        case "check":
                            buttonInteractionEvent.deferEdit().queue();
                            flop(member, uid, event, game);
                            break;
                        case "three":
                            buttonInteractionEvent.deferEdit().queue();
                            int threePlay = 3 * ante;
                            game.setBet(bet + threePlay);
                            resolveGame(member, uid, event, game, threePlay);
                            break;
                        case "four":
                            buttonInteractionEvent.deferEdit().queue();
                            int fourPlay = 4 * ante;
                            game.setBet(bet + fourPlay);
                            resolveGame(member, uid, event, game, fourPlay);
                            break;
                    }
                });
    }

    private void flop(Member member, String uid, SlashCommandInteractionEvent event, THGame game) {
        game.dealFlop();
        // definitions
        List<THCard> playerCards = game.getPlayerCards();
        List<THCard> tableCards = game.getTable();
        int blind = game.getBlind();
        int ante = game.getAnte();
        int trips = game.getTrips();
        int bet = game.getBet();


        // buttons
        ArrayList<Button> flopButtons = new ArrayList<>();
        flopButtons.add(Button.success("TH:" + uid + ":check", "Check"));
        flopButtons.add(Button.primary("TH:" + uid + ":two", "Bet 2x"));

        // hook
        event.getHook().editOriginalEmbeds(THHelper.flop(playerCards, tableCards, blind, ante, trips)).queue();
        event.getHook().editOriginalComponents(ActionRow.of(flopButtons)).queue();

        // listener
        event.getJDA().listenOnce(ButtonInteractionEvent.class)
                .filter(buttonInteractionEvent -> buttonInteractionEvent.getChannel().getId().equals(event.getChannel().getId()))
                .timeout(TIMEOUT_DURATION, () -> {
                    event.getHook().editOriginalEmbeds(THHelper.THTimeout(bet)).queue();
                    event.getHook().editOriginalComponents().queue();
                    COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), 5 * 60);
                    try {
                        BROCOINS_SQL.updateCashWithoutMultiplier(member, -bet);
                    } catch (SQLException e) {
                        MessageEmbed err = THHelper.createErrorEmbed(e.getMessage());
                        event.getHook().editOriginalEmbeds(err).queue();
                        e.printStackTrace();
                    }
                    THHelper.endTHGame(member);
                })
                .subscribe(buttonInteractionEvent -> {
                    String buttonId = buttonInteractionEvent.getComponentId();
                    if (!buttonId.contains("TH:")) return;

                    String[] buttonSplit = buttonId.split(":");
                    String userId = buttonSplit[1];
                    String buttonCategory = buttonSplit[2];

                    if (!userId.equals(uid)) {
                        event.deferReply().setContent("You cannot interact with this game!").setEphemeral(true).queue();
                        return;
                    }

                    switch (buttonCategory) {
                        case "check":
                            buttonInteractionEvent.deferEdit().queue();
                            river(member, uid, event, game);
                            break;
                        case "two":
                            buttonInteractionEvent.deferEdit().queue();
                            int twoPlay = 2 * ante;
                            game.setBet(bet + twoPlay);
                            resolveGame(member, uid, event, game, twoPlay);
                            break;
                    }
                });
    }

    private void river(Member member, String uid, SlashCommandInteractionEvent event, THGame game) {
        game.dealRiver();
        // definitions
        List<THCard> playerCards = game.getPlayerCards();
        List<THCard> tableCards = game.getTable();
        int blind = game.getBlind();
        int ante = game.getAnte();
        int trips = game.getTrips();
        int bet = game.getBet();


        // buttons
        ArrayList<Button> riverButtons = new ArrayList<>();
        riverButtons.add(Button.primary("TH:" + uid + ":one", "Bet 1x"));
        riverButtons.add(Button.danger("TH:" + uid + ":fold", "Fold"));


        // hook
        event.getHook().editOriginalEmbeds(THHelper.river(playerCards, tableCards, blind, ante, trips)).queue();
        event.getHook().editOriginalComponents(ActionRow.of(riverButtons)).queue();

        // listener
        event.getJDA().listenOnce(ButtonInteractionEvent.class)
                .filter(buttonInteractionEvent -> buttonInteractionEvent.getChannel().getId().equals(event.getChannel().getId()))
                .timeout(TIMEOUT_DURATION, () -> {
                    event.getHook().editOriginalEmbeds(THHelper.THTimeout(bet)).queue();
                    event.getHook().editOriginalComponents().queue();
                    COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), 5 * 60);
                    try {
                        BROCOINS_SQL.updateCashWithoutMultiplier(member, -bet);
                    } catch (SQLException e) {
                        MessageEmbed err = THHelper.createErrorEmbed(e.getMessage());
                        event.getHook().editOriginalEmbeds(err).queue();
                        e.printStackTrace();
                    }
                    THHelper.endTHGame(member);
                })
                .subscribe(buttonInteractionEvent -> {
                    String buttonId = buttonInteractionEvent.getComponentId();
                    if (!buttonId.contains("TH:")) return;

                    String[] buttonSplit = buttonId.split(":");
                    String userId = buttonSplit[1];
                    String buttonCategory = buttonSplit[2];

                    if (!userId.equals(uid)) {
                        event.deferReply().setContent("You cannot interact with this game!").setEphemeral(true).queue();
                        return;
                    }

                    switch (buttonCategory) {
                        case "fold":
                            buttonInteractionEvent.deferEdit().queue();
                            event.getHook().editOriginalEmbeds(THHelper.fold(playerCards, tableCards, bet)).queue();
                            event.getHook().editOriginalComponents().queue();
                            COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), 5 * 60);
                            try {
                                BROCOINS_SQL.updateCashWithoutMultiplier(member, -bet);
                            } catch (SQLException e) {
                                MessageEmbed err = THHelper.createErrorEmbed(e.getMessage());
                                event.getHook().editOriginalEmbeds(err).queue();
                                e.printStackTrace();
                            }
                            THHelper.endTHGame(member);
                            break;
                        case "one":
                            buttonInteractionEvent.deferEdit().queue();
                            game.setBet(bet + ante);
                            resolveGame(member, uid, event, game, bet + ante);
                            break;
                    }
                });
    }

    private void resolveGame(Member member, String uid, SlashCommandInteractionEvent event, THGame game, int play) {

        List<THCard> playerCards = game.getPlayerCards();
        List<THCard> tableCards = game.getTable();
        List<THCard> dealerCards = game.getDealerCards();
        int blind = game.getBlind();
        int ante = game.getAnte();
        int trips = game.getTrips();
        int bet = game.getBet();


        String result = THHandEvaluator.compareHands(playerCards, dealerCards, tableCards);
        THHandRank playerRank = THHandEvaluator.evaluateHand(playerCards);
        THHandRank dealerRank = THHandEvaluator.evaluateHand(dealerCards);

        switch (result) {
            case "player":
                event.getHook().editOriginalComponents().queue();
                int blindReturn = calculateBlindsReturn(playerCards) * blind;
                int tripsReturn = calculateTripsReturn(playerCards) * trips;
                game.setBet(tripsReturn + blindReturn + bet);
                try {
                    BROCOINS_SQL.updateCashMultiplier(member, event, bet);
                    logger.info("{}(ID:{}) played poker, and won {}", member.getUser().getName(), uid, bet);
                    event.getHook().editOriginalEmbeds(THHelper.revealDealerCards(playerCards, tableCards, dealerCards, blind, ante, trips, play)).queue();
                    event.getHook().editOriginalEmbeds(THHelper.playerWin(playerCards, tableCards, dealerCards, rankAsString(dealerRank), rankAsString(playerRank), blindReturn, tripsReturn, game.getBet())).queueAfter(5, TimeUnit.SECONDS);
                    return;
                } catch (SQLException e) {
                    MessageEmbed err = THHelper.createErrorEmbed(e.getMessage());
                    event.getHook().editOriginalEmbeds(err).queue();
                    e.printStackTrace();
                }
                break;
            case "dealer":
                event.getHook().editOriginalComponents().queue();
                try {
                    BROCOINS_SQL.updateCashWithoutMultiplier(member, -bet);
                    logger.info("{}(ID:{}) played poker, and lost {}", member.getUser().getName(), uid, bet);
                    event.getHook().editOriginalEmbeds(THHelper.revealDealerCards(playerCards, tableCards, dealerCards, blind, ante, trips, play)).queue();
                    event.getHook().editOriginalEmbeds(THHelper.playerLose(playerCards, tableCards, dealerCards, rankAsString(dealerRank), rankAsString(playerRank), bet)).queueAfter(5, TimeUnit.SECONDS);
                    return;
                } catch (SQLException e) {
                    MessageEmbed err = THHelper.createErrorEmbed(e.getMessage());
                    event.getHook().editOriginalEmbeds(err).queue();
                    e.printStackTrace();
                }
                break;
            case "tie":
                event.getHook().editOriginalComponents().queue();
                logger.info("{}(ID:{}) played poker, and tied.", member.getUser().getName(), uid);
                event.getHook().editOriginalEmbeds(THHelper.revealDealerCards(playerCards, tableCards, dealerCards, blind, ante, trips, play)).queue();
                event.getHook().editOriginalEmbeds(THHelper.tie(playerCards, tableCards, dealerCards, rankAsString(dealerRank), rankAsString(playerRank))).queueAfter(5, TimeUnit.SECONDS);
                break;
        }
    }

    List<THCard> gameDeck() {
        List<THCard> deck = new LinkedList<>();
        for (int i = 1; i <= 13; i++) {
            deck.add(new THCard(i, "Club"));
            deck.add(new THCard(i, "Diamond"));
            deck.add(new THCard(i, "Heart"));
            deck.add(new THCard(i, "Spade"));
        }
        Collections.shuffle(deck);
        return deck;
    }

    THCard drawCard(List<THCard> deck) {
        THCard thCard = deck.get(0);
        deck.remove(0);
        return thCard;
    }

    private int calculateBlindsReturn(List<THCard> hand) {
        THHandRank THHandRank = THHandEvaluator.evaluateHand(hand);
        switch (THHandRank) {
            case ROYAL_FLUSH:
                return 500;
            case STRAIGHT_FLUSH:
                return 50;
            case FOUR_OF_A_KIND:
                return 10;
            case FULL_HOUSE:
                return 3;
            default:
                return 1;
        }
    }

    private int calculateTripsReturn(List<THCard> hand) {
        THHandRank THHandRank = THHandEvaluator.evaluateHand(hand);
        switch (THHandRank) {
            case ROYAL_FLUSH:
                return 50;
            case STRAIGHT_FLUSH:
                return 40;
            case FOUR_OF_A_KIND:
                return 30;
            case FULL_HOUSE:
                return 8;
            case FLUSH:
                return 7;
            case STRAIGHT:
                return 5;
            case THREE_OF_A_KIND:
                return 3;
            case TWO_PAIR:
                return 2;
            default:
                return 1;
        }
    }

    private String rankAsString(THHandRank THHandRank) {
        switch (THHandRank) {
            case ROYAL_FLUSH:
                return "Royal Flush";
            case STRAIGHT_FLUSH:
                return "Straight Flush";
            case FOUR_OF_A_KIND:
                return "Four of a Kind";
            case FULL_HOUSE:
                return "Full House";
            case FLUSH:
                return "Flush";
            case STRAIGHT:
                return "Straight";
            case THREE_OF_A_KIND:
                return "Three of a Kind";
            case TWO_PAIR:
                return "Two Pair";
            case ONE_PAIR:
                return "Pair";
            default:
                return "High Card";
        }
    }
}
