package me.nerdoron.himyb.modules.useful.suggestions;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuggestCommandAutoComplete extends ListenerAdapter {

    final List<String> types = new ArrayList<>(Arrays.asList("server", "bot"));

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {

        if (event.getName().equals("suggest") && event.getFocusedOption().getName().equals("type")) {
            ArrayList<Command.Choice> options = new ArrayList<>();
            for (String type : types) {
                options.add(new Command.Choice(type, type));
            }

            event.replyChoices(options).queue();
        }

    }
}
