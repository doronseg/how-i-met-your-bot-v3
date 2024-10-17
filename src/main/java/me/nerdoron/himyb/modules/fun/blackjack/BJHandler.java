package me.nerdoron.himyb.modules.fun.blackjack;

import me.nerdoron.himyb.Global;
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

import static me.nerdoron.himyb.Global.*;

public class BJHandler {
    private static final Logger logger = LoggingHandler.logger(BJHandler.class);

    public void startBJGame(SlashCommandInteractionEvent event, int bet) throws InterruptedException {
        Member member = event.getMember();
        assert member != null;
        String uid = member.getId();

        // Define cards
        List<BJCard> deck = new LinkedList<>();
        List<BJCard> botHand = new LinkedList<>();
        List<BJCard> userHand = new LinkedList<>();

        // Define buttons
        ArrayList<Button> buttons = new ArrayList<>();
        buttons.add(Button.primary("BJ:" + uid + ":hit", "Hit"));
        buttons.add(Button.success("BJ:" + uid + ":stand", "Stand"));
        buttons.add(Button.danger("BJ:" + uid + ":dd", "Double Down"));

        // Shuffle cards
        for (int i = 1; i <= 13; i++) {
            deck.add(new BJCard(i, "club"));
            deck.add(new BJCard(i, "diamond"));
            deck.add(new BJCard(i, "heart"));
            deck.add(new BJCard(i, "spade"));
        }
        Collections.shuffle(deck);

        // Add cards to hands
        userHand.add(drawCard(deck));
        userHand.add(drawCard(deck));
        botHand.add(drawCard(deck));
        botHand.add(drawCard(deck));

        // Start game phase 1
        MessageEmbed phaseOne = BJHelper.blackJackGame(bet, userHand, botHand, false);
        BlackjackGame game = new BlackjackGame();
        BJHelper.startNewBjGame(member, game);
        Thread.sleep(3 * Global.SECONDS_IN_MS);
        event.getHook().editOriginalEmbeds(phaseOne).queue();
        if (win(userHand) || win(botHand)) {
            try {
                MessageEmbed resolveGame = BJHelper.resolveGame(bet, userHand, botHand, deck, member);
                event.getHook().editOriginalEmbeds(resolveGame).queue();
                COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), 5 * 60);
            } catch (SQLException e) {
                MessageEmbed err = BJHelper.createErrorEmbed(e.getMessage());
                event.getHook().editOriginalEmbeds(err).queue();
            }
        } else {
            event.getHook().editOriginalComponents(ActionRow.of(buttons)).queue();
            bjGame(event, bet, member, uid, userHand, deck, botHand);
        }
    }

    // handle blackjack game
    private void bjGame(SlashCommandInteractionEvent event, int bet, Member member, String uid, List<BJCard> userHand, List<BJCard> deck, List<BJCard> botHand) {
        event.getJDA().listenOnce(ButtonInteractionEvent.class)
                .filter(buttonInteractionEvent -> buttonInteractionEvent.getChannel().getId().equals(event.getChannel().getId()))
                .timeout(TIMEOUT_DURATION, () -> {
                    event.getHook().editOriginalEmbeds(BJHelper.blackJackTimeOut(bet)).queue();
                    event.getHook().editOriginalComponents().queue();
                    COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), 5 * 60);
                    BJHelper.endBjGame(member);
                    try {
                        BROCOINS_SQL.updateCashWithoutMultiplier(member, -bet);
                        logger.info("{}(ID:{}) timed out on a Blackjack game and lost {}", member.getUser().getName(), member.getId(), bet);
                    } catch (SQLException e) {
                        logger.error("Error updating cash for {}: {}", member.getUser().getName(), e.getMessage());
                    }
                })
                .subscribe(buttonInteractionEvent -> {
                    String buttonId = buttonInteractionEvent.getComponentId();
                    if (!buttonId.contains("BJ:")) return;

                    String[] buttonSplit = buttonId.split(":");
                    String userId = buttonSplit[1];
                    String buttonCategory = buttonSplit[2];

                    if (!userId.equals(uid)) {
                        event.deferReply().setContent("You cannot interact with this game!").setEphemeral(true).queue();
                        return;
                    }
                    switch (buttonCategory) {
                        case "hit":
                            handleHit(event, bet, buttonInteractionEvent, userHand, deck, botHand, member);
                            break;
                        case "stand":
                            handleStand(event, bet, buttonInteractionEvent, userHand, botHand, deck, member);
                            break;
                        case "dd":
                            handleDd(event, bet, buttonInteractionEvent, userHand, deck, botHand, member);
                            break;
                    }
                });
    }

    // handle stand
    private void handleStand(SlashCommandInteractionEvent event, int bet, ButtonInteractionEvent buttonInteractionEvent, List<BJCard> userHand, List<BJCard> botHand, List<BJCard> deck, Member member) {
        try {
            MessageEmbed resolveGame = BJHelper.resolveGame(bet, userHand, botHand, deck, member);
            event.getHook().editOriginalEmbeds(resolveGame).queue();
            event.getHook().editOriginalComponents().queue();
            COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), 5 * 60);
        } catch (SQLException e) {
            MessageEmbed err = BJHelper.createErrorEmbed(e.getMessage());
            event.getHook().editOriginalEmbeds(err).queue();
        }
        buttonInteractionEvent.deferEdit().queue();
    }

    // handle hit
    private void handleHit(SlashCommandInteractionEvent event, int bet, ButtonInteractionEvent buttonInteractionEvent, List<BJCard> userHand, List<BJCard> deck, List<BJCard> botHand, Member member) {
        userHand.add(drawCard(deck));
        if (win(userHand)) {
            try {
                MessageEmbed resolveGame = BJHelper.resolveGame(bet, userHand, botHand, deck, member);
                event.getHook().editOriginalEmbeds(resolveGame).queue();
                event.getHook().editOriginalComponents().queue();
                COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), 5 * 60);
                return;
            } catch (SQLException e) {
                MessageEmbed err = BJHelper.createErrorEmbed(e.getMessage());
                event.getHook().editOriginalEmbeds(err).queue();
            }
        } else if (bust(userHand)) {
            try {
                MessageEmbed resolveGame = BJHelper.resolveGame(bet, userHand, botHand, deck, member);
                event.getHook().editOriginalEmbeds(resolveGame).queue();
                event.getHook().editOriginalComponents().queue();
                COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), 5 * 60);
                return;
            } catch (SQLException e) {
                MessageEmbed err = BJHelper.createErrorEmbed(e.getMessage());
                event.getHook().editOriginalEmbeds(err).queue();
            }
        }
        MessageEmbed phaseTwo = BJHelper.blackJackGame(bet, userHand, botHand, false);
        event.getHook().editOriginalEmbeds(phaseTwo).queue();
        bjGame(event, bet, member, member.getId(), userHand, deck, botHand);
        buttonInteractionEvent.deferEdit().queue();
    }

    // handle double down
    private void handleDd(SlashCommandInteractionEvent event, int bet, ButtonInteractionEvent buttonInteractionEvent, List<BJCard> userHand, List<BJCard> deck, List<BJCard> botHand, Member member) {
        if (bet * 2 > BROCOINS_SQL.getBroCash(event.getMember())) {
            buttonInteractionEvent.reply("You don't have enough cash!").setEphemeral(true).queue();
            bjGame(event, bet, member, member.getId(), userHand, deck, botHand);
            return;
        }
        bet *= 2;
        userHand.add(drawCard(deck));
        if (win(userHand)) {
            try {
                MessageEmbed resolveGame = BJHelper.resolveGame(bet, userHand, botHand, deck, member);
                event.getHook().editOriginalEmbeds(resolveGame).queue();
                event.getHook().editOriginalComponents().queue();
                COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), 5 * 60);
                return;
            } catch (SQLException e) {
                MessageEmbed err = BJHelper.createErrorEmbed(e.getMessage());
                event.getHook().editOriginalEmbeds(err).queue();
            }
        } else if (bust(userHand)) {
            try {
                MessageEmbed resolveGame = BJHelper.resolveGame(bet, userHand, botHand, deck, member);
                event.getHook().editOriginalEmbeds(resolveGame).queue();
                event.getHook().editOriginalComponents().queue();
                COOLDOWN_MANAGER.addCooldown(CooldownManager.commandID(event), 5 * 60);
                return;
            } catch (SQLException e) {
                MessageEmbed err = BJHelper.createErrorEmbed(e.getMessage());
                event.getHook().editOriginalEmbeds(err).queue();
            }
        }
        MessageEmbed phaseTwo = BJHelper.blackJackGame(bet, userHand, botHand, false);
        event.getHook().editOriginalEmbeds(phaseTwo).queue();
        bjGame(event, bet, member, member.getId(), userHand, deck, botHand);
        buttonInteractionEvent.deferEdit().queue();
    }

    private Boolean bust(List<BJCard> userHand) {
        int value = BJHelper.getHandValue(userHand);
        return value > 21;
    }

    private boolean win(List<BJCard> userHand) {
        int value = BJHelper.getHandValue(userHand);
        return value == 21;
    }

    public BJCard drawCard(List<BJCard> deck) {
        BJCard bjCard = deck.get(0);
        deck.remove(0);
        return bjCard;
    }
}