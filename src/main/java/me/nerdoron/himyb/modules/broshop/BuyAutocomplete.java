package me.nerdoron.himyb.modules.broshop;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class BuyAutocomplete extends ListenerAdapter {

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("buy") && event.getFocusedOption().getName().equals("item")) {
            ArrayList<Command.Choice> options = new ArrayList<>();
            for (String type : ShopItem.allItemTypes()) {
                options.add(new Command.Choice(type, type));
            }
            event.replyChoices(options).queue();
        }
    }
}