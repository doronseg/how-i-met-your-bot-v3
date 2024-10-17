package me.nerdoron.himyb.modules.fun.brocoins;

import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.bot.Rng;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;

import static me.nerdoron.himyb.Global.*;

public class ArrestHandler {
    private static final Logger logger = LoggingHandler.logger(ArrestHandler.class);

    public static void initialArrest(SlashCommandInteractionEvent event, String charge, int potentialTime) {
        Member member = event.getMember();
        assert member != null;
        String uid = event.getMember().getId();
        int rng = Rng.generateNumber(7, 15);
        int chance = Rng.generateNumber(1, 100);

        ArrayList<Button> buttons = new ArrayList<>();
        buttons.add(Button.primary("ARREST:" + uid + ":cooperate", "\uD83D\uDC65 Cooperate with arrest"));
        buttons.add(Button.danger("ARREST:" + uid + ":run", "\uD83D\uDCA8 Try to run"));
        buttons.add(Button.danger("ARREST:" + uid + ":bribe", "\uD83D\uDCB8 Try to bribe them (50% of current cash)"));

        event.reply(String.format("You have %d seconds to respond!", rng))
                .addEmbeds(JailHelper.arrestedEmbed(charge))
                .addActionRow(buttons)
                .queue();
        final Duration timeout = Duration.ofSeconds(rng);
        event.getJDA().listenOnce(ButtonInteractionEvent.class)
                .filter(buttonEvent -> buttonEvent.getChannel().getIdLong() == event.getChannel().getIdLong())
                .filter(buttonEvent -> buttonEvent.getComponentId().contains("ARREST:" + uid))
                .timeout(timeout, () -> {
                    event.getHook().editOriginal("").queue();
                    event.getHook().editOriginalEmbeds(JailHelper.timeoutEmbed(potentialTime)).queue();
                    event.getHook().editOriginalComponents().queue();
                    JailHelper.jailMember(member, potentialTime, charge);
                    logger.info("{}(ID:{}) was arrested but timed out he was jailed for {} hours.", member.getUser().getName(), uid, potentialTime / HOUR_IN_SECONDS);
                })
                .subscribe(buttonEvent -> {
                    // handle button interaction
                    String buttonId = buttonEvent.getComponentId();
                    if (!buttonId.contains("ARREST:")) return;
                    String[] buttonSplit = buttonId.split(":");
                    String userId = buttonSplit[1];
                    String buttonCategory = buttonSplit[2];

                    if (!userId.equals(event.getUser().getId())) {
                        event.deferReply().setEphemeral(true).setContent("You can not interact with this menu!").queue();
                        return;
                    }

                    switch (buttonCategory) {
                        case "cooperate":
                            event.getHook().editOriginal("").queue();
                            event.getHook().editOriginalEmbeds(JailHelper.cooperateEmbed(potentialTime)).queue();
                            event.getHook().editOriginalComponents().queue();
                            JailHelper.jailMember(member, potentialTime, charge);
                            buttonEvent.deferEdit().queue();
                            logger.info("{}(ID:{}) was arrested and cooperated out he was jailed for {} hours.", member.getUser().getName(), uid, potentialTime / HOUR_IN_SECONDS);
                            break;
                        case "run":
                            if (chance < 51) {
                                event.getHook().editOriginal("").queue();
                                event.getHook().editOriginalEmbeds(JailHelper.successRunEmbed()).queue();
                                event.getHook().editOriginalComponents().queue();
                                COOLDOWN_MANAGER.addCooldown("arrested", "Ran", HOUR_IN_SECONDS / 2);
                                buttonEvent.deferEdit().queue();
                                logger.info("{}(ID:{}) was arrested and managed to run!", member.getUser().getName(), uid);
                                break;
                            }
                            // fail
                            event.getHook().editOriginal("").queue();
                            event.getHook().editOriginalEmbeds(JailHelper.failedRunEmbed(potentialTime * 2)).queue();
                            event.getHook().editOriginalComponents().queue();
                            JailHelper.jailMember(member, potentialTime * 2, "RESISTING");
                            buttonEvent.deferEdit().queue();
                            logger.info("{}(ID:{}) was arrested and failed to outrun the cops out he was jailed for {} hours.", member.getUser().getName(), uid, potentialTime / HOUR_IN_SECONDS);
                            break;
                        case "bribe":
                            if (chance > 51) {
                                // success
                                int broCash = BROCOINS_SQL.getBroCash(member);
                                if (broCash < 35) {
                                    event.getHook().editOriginal("").queue();
                                    event.getHook().editOriginalEmbeds(JailHelper.bribeTooLow(potentialTime, broCash)).queue();
                                    event.getHook().editOriginalComponents().queue();
                                    JailHelper.jailMember(member, potentialTime, charge);
                                    buttonEvent.deferEdit().queue();
                                    logger.info("{}(ID:{}) was arrested, tried to bribe the cops but he didn't have enough cash. He was jailed for {} hours.", member.getUser().getName(), uid, potentialTime / HOUR_IN_SECONDS);
                                    break;
                                }
                                try {
                                    BROCOINS_SQL.updateCashWithoutMultiplier(member, -(broCash / 2));
                                    event.getHook().editOriginal("").queue();
                                    event.getHook().editOriginalEmbeds(JailHelper.successBribeEmbed(broCash / 2)).queue();
                                    event.getHook().editOriginalComponents().queue();
                                    COOLDOWN_MANAGER.addCooldown("arrested", "Bribed", HOUR_IN_SECONDS / 4);
                                    buttonEvent.deferEdit().queue();
                                    logger.info("{}(ID:{}) was arrested and bribed the cops with {} coins.", member.getUser().getName(), uid, broCash / 2);
                                    break;
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            //fail
                            event.getHook().editOriginal("").queue();
                            event.getHook().editOriginalEmbeds(JailHelper.failedBribeEmbed(potentialTime * 2)).queue();
                            event.getHook().editOriginalComponents().queue();
                            JailHelper.jailMember(member, potentialTime * 2, "BRIBERY");
                            buttonEvent.deferEdit().queue();
                            logger.info("{}(ID:{}) was arrested and the cops didn't take his bribe. he was jailed for {} hours.", member.getUser().getName(), uid, potentialTime / HOUR_IN_SECONDS);

                            break;
                    }
                });
    }
}
