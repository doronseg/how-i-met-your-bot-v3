package me.nerdoron.himyb.modules.broshop;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ShopButtons extends ListenerAdapter {
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();
        if (!buttonId.contains("SHOP:")) return;

        String[] buttonSplit = buttonId.split(":");
        String userId = buttonSplit[1];
        String buttonCategory = buttonSplit[2];
        Message message = event.getMessage();

        if (!userId.equals(event.getUser().getId())) {
            event.deferReply().setEphemeral(true).setContent("You can not interact with this menu!").queue();
            return;
        }

        switch (buttonCategory) {
            case "main":
                event.deferEdit().queue();
                message.editMessageEmbeds(ShopHelper.shopMain()).queue();
                break;
            case "xp":
                event.deferEdit().queue();
                message.editMessageEmbeds(ShopHelper.shopXp()).queue();
                break;
            case "coin":
                event.deferEdit().queue();
                message.editMessageEmbeds(ShopHelper.shopCoin()).queue();
                break;
            case "roles":
                event.deferEdit().queue();
                message.editMessageEmbeds(ShopHelper.roleEmbed()).queue();
                break;
            case "items":
                event.deferEdit().queue();
                message.editMessageEmbeds(ShopHelper.itemEmbed()).queue();
                break;
        }

    }

}
