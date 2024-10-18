package me.nerdoron.himyb.modules.broshop.items;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.bot.Rng;
import me.nerdoron.himyb.modules.broshop.ShopHelper;
import me.nerdoron.himyb.modules.fun.brocoins.ArrestHandler;
import me.nerdoron.himyb.modules.fun.brocoins.JailHelper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.nerdoron.himyb.Global.COOLDOWN_MANAGER;
import static me.nerdoron.himyb.modules.fun.brocoins.JailHelper.checkIfInJail;

public class GiveItem {

    private static final Logger logger = LoggingHandler.logger(GiveItem.class);

    public Boolean giveItem(Member member, Member memberToTransferTo, String item, SlashCommandInteractionEvent event) {
        boolean memberInJail = checkIfInJail(member);
        boolean memberToTransferToInJail = checkIfInJail(memberToTransferTo);
        int rng = Rng.generateNumber(1, 10);

        // both in jail
        if (memberInJail && memberToTransferToInJail) {
            String uid = memberToTransferTo.getId();
            ArrayList<Button> buttons = new ArrayList<>();
            buttons.add(Button.success("CB:" + uid + ":accept", "Accept"));
            buttons.add(Button.danger("CB:" + uid + ":deny", "Deny"));
            final Duration timeout = Duration.ofMinutes(5);
            AtomicBoolean success = new AtomicBoolean(false);

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
                        success.set(false);
                    })
                    .subscribe(buttonInteractionEvent -> {
                        String buttonId = buttonInteractionEvent.getComponentId();
                        if (!buttonId.contains("CB:")) return;

                        String[] buttonSplit = buttonId.split(":");
                        String userId = buttonSplit[1];
                        String buttonCategory = buttonSplit[2];

                        if (!userId.equals(buttonInteractionEvent.getUser().getId())) {
                            return;
                        }

                        switch (buttonCategory) {
                            case "accept":
                                buttonInteractionEvent.deferEdit().queue();
                                event.getHook().editOriginalComponents().queue();
                                if (rng <= 5) {
                                    event.getHook().editOriginal(memberToTransferTo.getAsMention()).queue();
                                    event.getHook().editOriginalEmbeds((ShopHelper.jailContrabandSuccess(member, memberToTransferTo, item))).queue();
                                    success.set(true);
                                    break;
                                }
                                int extraTime = 2 * Global.HOUR_IN_SECONDS;
                                String memberCooldown = CooldownManager.jailID(member);
                                String memberToTransferToCooldown = CooldownManager.jailID(memberToTransferTo);
                                COOLDOWN_MANAGER.extendJailTime(memberCooldown, "CONTRABAND_DEAL", member, extraTime);
                                COOLDOWN_MANAGER.extendJailTime(memberToTransferToCooldown, "CONTRABAND_DEAL", memberToTransferTo, extraTime);
                                event.getHook().editOriginalComponents().queue();
                                event.getHook().editOriginalEmbeds(ShopHelper.extendJailTime(extraTime)).queue();
                                event.getHook().editOriginal(String.format("%s %s", member.getAsMention(), memberToTransferTo.getAsMention())).queue();
                                logger.info("{} (ID:{}) tried to give an item to {}, but failed, and both user's prison time was extended by 2 hours.", member.getUser().getName(), member.getId(), memberToTransferTo.getUser().getName());
                                success.set(false);
                                break;
                            case "deny":
                                buttonInteractionEvent.deferEdit().queue();
                                event.getHook().editOriginalComponents().queue();
                                event.getHook().editOriginalEmbeds().queue();
                                event.getHook().editOriginal(String.format("%s aborted the transfer.", memberToTransferTo.getAsMention())).queue();
                                success.set(false);
                                break;
                        }
                    });
            return success.get();
        }

        // contraband smuggling
        if (!memberInJail && memberToTransferToInJail) {
            final Duration timeout = Duration.ofSeconds(10);

            String uid = member.getId();
            ArrayList<Button> buttons = new ArrayList<>();
            buttons.add(Button.success("CB:" + uid + ":proceed", "Proceed"));
            buttons.add(Button.danger("CB:" + uid + ":abort", "Abort"));

            event.reply("You have 10 seconds to respond.")
                    .addEmbeds(ShopHelper.contrabandStartOtherMemberInJail(memberToTransferTo))
                    .addActionRow(buttons)
                    .queue();
            AtomicBoolean success = new AtomicBoolean(false);

            event.getJDA().listenOnce(ButtonInteractionEvent.class)
                    .filter(buttonInteractionEvent -> buttonInteractionEvent.getChannel().getId().equals(event.getChannel().getId()))
                    .timeout(timeout, () -> {
                        event.getHook().editOriginal("You failed to respond in time. Aborting action.").queue();
                        event.getHook().editOriginalEmbeds().queue();
                        event.getHook().editOriginalComponents().queue();
                        success.set(false);
                    })
                    .subscribe(buttonInteractionEvent -> {
                        String buttonId = buttonInteractionEvent.getComponentId();
                        if (!buttonId.contains("CB:")) return;

                        String[] buttonSplit = buttonId.split(":");
                        String userId = buttonSplit[1];
                        String buttonCategory = buttonSplit[2];

                        if (!userId.equals(uid)) {
                            return;
                        }

                        switch (buttonCategory) {
                            case "proceed":
                                buttonInteractionEvent.deferEdit().queue();
                                event.getHook().editOriginalComponents().queue();
                                if (rng < 3) {
                                    event.getHook().editOriginal(memberToTransferTo.getAsMention()).queue();
                                    event.getHook().editOriginalEmbeds(ShopHelper.contrabandSuccess(member, memberToTransferTo, item)).queue();
                                    success.set(true);
                                }
                                ArrestHandler.initialArrest(event, "CONTRABAND_SMUGGLE", 3 * Global.HOUR_IN_SECONDS);
                                logger.info("{} (ID:{}) tried to give an item to {}, but failed, and was arrested.", member.getUser().getName(), member.getId(), memberToTransferTo.getUser().getName());
                                success.set(false);
                                break;
                            case "abort":
                                buttonInteractionEvent.deferEdit().queue();
                                event.getHook().editOriginalComponents().queue();
                                event.getHook().editOriginalEmbeds().queue();
                                event.getHook().editOriginal("You aborted the transfer.").queue();
                                success.set(false);
                                break;
                        }
                    });
            return success.get();
        }

        if (memberInJail) {
            event.replyEmbeds(JailHelper.inJailEmbed(member)).queue();
            return false;
        }

        event.reply(memberToTransferTo.getAsMention())
                .addEmbeds(ShopHelper.doneEmbed(member, memberToTransferTo, item)).queue();
        return true;
    }
}
