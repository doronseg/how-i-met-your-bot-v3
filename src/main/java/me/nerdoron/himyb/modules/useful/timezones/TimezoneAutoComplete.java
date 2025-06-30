package me.nerdoron.himyb.modules.useful.timezones;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

public class TimezoneAutoComplete extends ListenerAdapter {

    private static List<String> searchTimezones(String query) {
        String lower = query.toLowerCase();
        return ZoneId.getAvailableZoneIds().stream()
                .filter(tz -> tz.toLowerCase().contains(lower))
                .sorted()
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (!event.getName().equals("settimezone")) return;

        String input = event.getFocusedOption().getValue();

        List<Command.Choice> choices = searchTimezones(input).stream()
                .map(tz -> new Command.Choice(tz, tz))
                .collect(Collectors.toList());

        event.replyChoices(choices).queue();
    }

}
