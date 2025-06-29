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
import java.util.concurrent.CompletableFuture;

import static me.nerdoron.himyb.Global.COOLDOWN_MANAGER;
import static me.nerdoron.himyb.modules.fun.brocoins.JailHelper.checkIfInJail;

public class GiveItem {

    private static final Logger logger = LoggingHandler.logger(GiveItem.class);

    public Boolean giveItem(Member member, Member memberToTransferTo, String item, SlashCommandInteractionEvent event) {
        boolean memberInJail = checkIfInJail(member);
        boolean memberToTransferToInJail = checkIfInJail(memberToTransferTo);
        int rng = Rng.generateNumber(1, 10);

        if (memberInJail && memberToTransferToInJail) {
            String uid = memberToTransferTo.getId();
            ArrayList<Button> buttons = new ArrayList<>();
            buttons.add(Button.success("CB:" + uid + ":accept", "Accept"));
            buttons.add(Button.danger("CB:" + uid + ":deny", "Deny"));
            final Duration timeout = Duration.ofMinutes(5);

            CompletableFuture<Boolean> future = new CompletableFuture<>();

            event.reply(String.format("%s You have 5 minutes to respond.", memberToTransferTo.getAsMention()))
                    .addEmbeds(ShopHelper.contrabandStartBothInJail(member, item))
                    .addActionRow(buttons)
                    .queue();

            event.getJDA().listenOnce(ButtonInteractionEvent.class)
                    .filter(buttonInteractionEvent -> buttonInteractionEvent.getChannel().getId().equals(event.getChannel().getId()))
                    .timeout(timeout, () -> {
                        event.getHook().editOriginal(String.format("%s failed to respond in time. Aborting action.", memberToTransferTo.getAsMention())).queue();
                        event.getHook().editOriginalEmbeds().queue();
                        event.getHook().editOriginalComponents().queue();
                        future.complete(false);
                    })
                    .subscribe(buttonInteractionEvent -> {
                        handleButtonInteraction(buttonInteractionEvent, member, memberToTransferTo, item, rng, future, event);
                    });

            return future.join(); // Optionally block for synchronous handling
        }

        if (!memberInJail && memberToTransferToInJail) {
            String uid = member.getId();
            ArrayList<Button> buttons = new ArrayList<>();
            buttons.add(Button.success("CB:" + uid + ":proceed", "Proceed"));
            buttons.add(Button.danger("CB:" + uid + ":abort", "Abort"));
            final Duration timeout = Duration.ofSeconds(10);

            CompletableFuture<Boolean> future = new CompletableFuture<>();

            event.reply("You have 10 seconds to respond.")
                    .addEmbeds(ShopHelper.contrabandStartOtherMemberInJail(memberToTransferTo))
                    .addActionRow(buttons)
                    .queue();

            event.getJDA().listenOnce(ButtonInteractionEvent.class)
                    .filter(buttonInteractionEvent -> buttonInteractionEvent.getChannel().getId().equals(event.getChannel().getId()))
                    .timeout(timeout, () -> {
                        event.getHook().editOriginal("You failed to respond in time. Aborting action.").queue();
                        event.getHook().editOriginalEmbeds().queue();
                        event.getHook().editOriginalComponents().queue();
                        future.complete(false);
                    })
                    .subscribe(buttonInteractionEvent -> {
                        handleSmugglingButtonInteraction(buttonInteractionEvent, member, memberToTransferTo, item, rng, future, event);
                    });

            return future.join(); // Optionally block for synchronous handling
        }

        if (memberInJail) {
            event.replyEmbeds(JailHelper.inJailEmbed(member)).queue();
            return false;
        }

        event.reply(memberToTransferTo.getAsMention())
                .addEmbeds(ShopHelper.doneEmbed(member, memberToTransferTo, item)).queue();
        return true;
    }

    private void handleButtonInteraction(ButtonInteractionEvent buttonInteractionEvent, Member member, Member memberToTransferTo,
                                         String item, int rng, CompletableFuture<Boolean> future, SlashCommandInteractionEvent event) {
        String buttonId = buttonInteractionEvent.getComponentId();
        if (!buttonId.contains("CB:")) return;

        String[] buttonSplit = buttonId.split(":");
        String userId = buttonSplit[1];
        String buttonCategory = buttonSplit[2];

        if (!userId.equals(buttonInteractionEvent.getUser().getId())) {
            return;
        }

        switch (buttonCategory.toLowerCase()) {
            case "accept":
                buttonInteractionEvent.deferEdit().queue();
                event.getHook().editOriginalComponents().queue();
                if (rng <= 5) {
                    event.getHook().editOriginal(memberToTransferTo.getAsMention()).queue();
                    event.getHook().editOriginalEmbeds(ShopHelper.jailContrabandSuccess(member, memberToTransferTo, item)).queue();
                    future.complete(true);
                } else {
                    extendJailTime(member, memberToTransferTo, item, future, event);
                }
                break;

            case "deny":
                buttonInteractionEvent.deferEdit().queue();
                event.getHook().editOriginalComponents().queue();
                event.getHook().editOriginalEmbeds().queue();
                event.getHook().editOriginal(String.format("%s aborted the transfer.", memberToTransferTo.getAsMention())).queue();
                future.complete(false);
                break;
        }
    }

    private void handleSmugglingButtonInteraction(ButtonInteractionEvent buttonInteractionEvent, Member member,
                                                  Member memberToTransferTo, String item, int rng, CompletableFuture<Boolean> future, SlashCommandInteractionEvent event) {
        String buttonId = buttonInteractionEvent.getComponentId();
        if (!buttonId.contains("CB:")) return;

        String[] buttonSplit = buttonId.split(":");
        String userId = buttonSplit[1];
        String buttonCategory = buttonSplit[2];

        if (!userId.equals(buttonInteractionEvent.getUser().getId())) {
            return;
        }

        switch (buttonCategory.toLowerCase()) {
            case "proceed":
                buttonInteractionEvent.deferEdit().queue();
                event.getHook().editOriginalComponents().queue();
                if (rng < 3) {
                    event.getHook().editOriginal(memberToTransferTo.getAsMention()).queue();
                    event.getHook().editOriginalEmbeds(ShopHelper.contrabandSuccess(member, memberToTransferTo, item)).queue();
                    future.complete(true);
                } else {
                    ArrestHandler.initialArrest(event, "CONTRABAND_SMUGGLE", 3 * Global.HOUR_IN_SECONDS);
                    removeItemFromInventory(member, item);
                    future.complete(false);
                }
                break;

            case "abort":
                buttonInteractionEvent.deferEdit().queue();
                event.getHook().editOriginalComponents().queue();
                event.getHook().editOriginalEmbeds().queue();
                event.getHook().editOriginal("You aborted the transfer.").queue();
                future.complete(false);
                break;
        }
    }

    private void extendJailTime(Member member, Member memberToTransferTo, String item, CompletableFuture<Boolean> future, SlashCommandInteractionEvent event) {
        int extraTime = 2 * Global.HOUR_IN_SECONDS;
        String memberCooldown = CooldownManager.jailID(member);
        String memberToTransferToCooldown = CooldownManager.jailID(memberToTransferTo);
        COOLDOWN_MANAGER.extendJailTime(memberCooldown, "CONTRABAND_DEAL", member, extraTime);
        COOLDOWN_MANAGER.extendJailTime(memberToTransferToCooldown, "CONTRABAND_DEAL", memberToTransferTo, extraTime);
        event.getHook().editOriginalComponents().queue();
        event.getHook().editOriginalEmbeds(ShopHelper.extendJailTime(extraTime)).queue();
        event.getHook().editOriginal(String.format("%s %s", member.getAsMention(), memberToTransferTo.getAsMention())).queue();
        removeItemFromInventory(member, item);
        future.complete(false);
    }

    private void removeItemFromInventory(Member member, String item) {
        InventoryHandler inventoryHandler = new InventoryHandler();
        try {
            inventoryHandler.removeFromInventory(member, item);
        } catch (SQLException e) {
            logger.error("Error removing item from inventory: {}", e.getMessage(), e);
        }
    }
}
