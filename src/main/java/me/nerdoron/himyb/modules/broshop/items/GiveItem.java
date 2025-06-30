package me.nerdoron.himyb.modules.broshop.items;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.bot.Rng;
import me.nerdoron.himyb.modules.broshop.InventoryHandler;
import me.nerdoron.himyb.modules.broshop.ShopHelper;
import me.nerdoron.himyb.modules.fun.brocoins.ArrestHandler;
import me.nerdoron.himyb.modules.fun.brocoins.JailHelper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.function.Consumer;

import static me.nerdoron.himyb.Global.COOLDOWN_MANAGER;

public class GiveItem {

    private static final Logger logger = LoggingHandler.logger(GiveItem.class);

    public void giveItem(Member member, Member memberToTransferTo, String item, SlashCommandInteractionEvent event, Consumer<Boolean> callback) {
        boolean memberInJail = JailHelper.checkIfInJail(member);
        boolean memberToTransferInJail = JailHelper.checkIfInJail(memberToTransferTo);
        int rng = Rng.generateNumber(1, 10);

        // Both in jail
        if (memberInJail && memberToTransferInJail) {
            String uid = memberToTransferTo.getId();
            ArrayList<Button> buttons = new ArrayList<>();
            buttons.add(Button.success("CB:" + uid + ":accept", "Accept"));
            buttons.add(Button.danger("CB:" + uid + ":deny", "Deny"));

            event.reply(String.format("%s you have  minutes to respond.", memberToTransferTo.getAsMention()))
                    .addEmbeds(ShopHelper.contrabandStartBothInJail(member, item))
                    .addActionRow(buttons)
                    .queue(message -> {
                        event.getJDA().listenOnce(ButtonInteractionEvent.class)
                                .filter(buttonEvent -> buttonEvent.getChannel().getId().equals(event.getChannel().getId()))
                                .timeout(Duration.ofMinutes(5), () -> {
                                    message.editOriginal(String.format("%s failed to respond in time. Aborting action.", member.getAsMention())).setEmbeds().setComponents().queue();
                                    callback.accept(false);
                                })
                                .subscribe(e -> {
                                    try {
                                        handleButtonInteraction(e, event, member, memberToTransferTo, item, rng, callback, message.getId());
                                    } catch (SQLException ex) {
                                        event.getHook().editOriginal("Error.").setEmbeds().setComponents();
                                        callback.accept(false);
                                        logger.error("Error occurred while trying to smuggle, {}", ex.getMessage());
                                    }
                                });
                    });
            return;
        }

        // Target in jail
        if (!memberInJail && memberToTransferInJail) {
            String uid = member.getId();
            ArrayList<Button> buttons = new ArrayList<>();
            buttons.add(Button.success("CB:" + uid + ":proceed", "Proceed"));
            buttons.add(Button.danger("CB:" + uid + ":abort", "Abort"));

            event.reply("You have 10 seconds to respond.")
                    .addEmbeds(ShopHelper.contrabandStartOtherMemberInJail(memberToTransferTo))
                    .addActionRow(buttons)
                    .queue(sentMessage -> {
                        String sentMessageId = sentMessage.getId();

                        event.getJDA().listenOnce(ButtonInteractionEvent.class)
                                .filter(buttonEvent -> buttonEvent.getChannel().getId().equals(event.getChannel().getId()))
                                .timeout(Duration.ofSeconds(10), () -> {
                                    sentMessage.editOriginal("You failed to respond in time. Aborting action.").setEmbeds().setComponents().queue();
                                    logger.info("timeout");
                                })
                                .subscribe(e -> {
                                    logger.info("subscribe");
                                    try {
                                        handleSmugglingInteraction(e, event, member, memberToTransferTo, item, rng, callback, sentMessageId);
                                    } catch (SQLException ex) {
                                        event.getHook().editOriginal("Error.").setEmbeds().setComponents();
                                        callback.accept(false);
                                        logger.error("Error occurred while trying to smuggle, {}", ex.getMessage());
                                    }
                                });
                    });
            return;
        }

        //Member in jail, target not
        if (memberInJail) {
            event.replyEmbeds(JailHelper.inJailEmbed(member)).queue();
            callback.accept(false);
            return;
        }

        // Regular
        event.reply(memberToTransferTo.getAsMention()).addEmbeds(ShopHelper.doneEmbed(member, memberToTransferTo, item))
                .addEmbeds(ShopHelper.doneEmbed(member, memberToTransferTo, item))
                .queue();
        callback.accept(true);
    }


    private void handleButtonInteraction(ButtonInteractionEvent e, SlashCommandInteractionEvent event, Member member, Member memberToTransferTo, String item, int rng, Consumer<Boolean> callback, String messageId) throws SQLException {
        String[] parts = e.getComponentId().split(":");
        String userId = parts[1];
        String action = parts[2];

        if (!e.getUser().getId().equals(userId)) return;

        e.deferEdit().queue();

        if (action.equalsIgnoreCase("accept")) {
            if (rng <= 5) {
                e.getMessage().editMessage(memberToTransferTo.getAsMention())
                        .setEmbeds(ShopHelper.jailContrabandSuccess(member, memberToTransferTo, item))
                        .setComponents()
                        .queue();
                callback.accept(true);
            } else {
                extendJailTime(member, memberToTransferTo, item, event);
                callback.accept(false);
            }
        } else if (action.equalsIgnoreCase("deny")) {
            e.getMessage().editMessage(String.format("%s aborted the transfer.", memberToTransferTo.getAsMention()))
                    .setEmbeds()
                    .setComponents()
                    .queue();
            callback.accept(false);
        }
    }

    private void handleSmugglingInteraction(ButtonInteractionEvent e, SlashCommandInteractionEvent event, Member member, Member target,
                                            String item, int rng, Consumer<Boolean> callback, String messageId) throws SQLException {
        InventoryHandler inventoryHandler = new InventoryHandler();
        String[] parts = e.getComponentId().split(":");
        String userId = parts[1];
        String action = parts[2];

        if (!e.getUser().getId().equals(userId)) return;

        e.deferEdit().queue();

        if ("proceed".equalsIgnoreCase(action)) {
            if (rng < 3) {
                e.getMessage().editMessage(target.getAsMention())
                        .setEmbeds(ShopHelper.contrabandSuccess(member, target, item))
                        .setComponents()
                        .queue();
                callback.accept(true);
            } else {
                inventoryHandler.removeFromInventory(member, item);
                ArrestHandler.initialArrest(event, "CONTRABAND_SMUGGLE", 3 * Global.HOUR_IN_SECONDS);
                callback.accept(false);
            }
        } else if ("abort".equalsIgnoreCase(action)) {
            e.getMessage().editMessage("You aborted the transfer.")
                    .setEmbeds()
                    .setComponents()
                    .queue();
            callback.accept(false);
        }
    }

    private void extendJailTime(Member member, Member target, String item, SlashCommandInteractionEvent event) throws SQLException {
        InventoryHandler inventoryHandler = new InventoryHandler();
        int extra = 2 * Global.HOUR_IN_SECONDS;
        String memberCooldown = CooldownManager.jailID(member);
        String targetCooldown = CooldownManager.jailID(target);

        COOLDOWN_MANAGER.extendJailTime(memberCooldown, "CONTRABAND_DEAL", member, extra);
        COOLDOWN_MANAGER.extendJailTime(targetCooldown, "CONTRABAND_DEAL", target, extra);

        event.getHook().editOriginal(String.format("%s %s", member.getAsMention(), target.getAsMention()))
                .setEmbeds(ShopHelper.extendJailTime(extra))
                .setComponents()
                .queue();
        inventoryHandler.removeFromInventory(member, item);
    }


}
