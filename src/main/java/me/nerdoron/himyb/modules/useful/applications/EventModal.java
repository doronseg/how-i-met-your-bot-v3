package me.nerdoron.himyb.modules.useful.applications;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class EventModal {
    final TextInput typesOfEvents = TextInput
            .create("event-type", "What types of events are you able to host?", TextInputStyle.PARAGRAPH)
            .setRequired(true)
            .setMinLength(10)
            .setMaxLength(1000)
            .setPlaceholder(
                    "I can host events in various games, and I can also host watchalongs.")
            .build();

    final TextInput howOften = TextInput
            .create("event-often", "How often are you able to host events?", TextInputStyle.PARAGRAPH)
            .setRequired(true)
            .setMinLength(10)
            .setMaxLength(1000)
            .setPlaceholder("I can host events three times a week.")
            .build();

    final TextInput irlCommitments = TextInput
            .create("event-irl", "What commitments would affect your activity?",
                    TextInputStyle.PARAGRAPH)
            .setRequired(true)
            .setMinLength(10)
            .setMaxLength(1000)
            .setPlaceholder("I go to school every day, from 8 AM to 2:30 PM GMT")
            .build();

    final TextInput whyPick = TextInput
            .create("event-picked", "Why should we pick you over other candidates?",
                    TextInputStyle.PARAGRAPH)
            .setRequired(true)
            .setMinLength(10)
            .setMaxLength(1000)
            .setPlaceholder("No hints here. Good luck!")
            .build();

    public final Modal modal = Modal
            .create("application-event", "Apply for event manager.").addComponents(ActionRow.of(typesOfEvents),
                    ActionRow.of(howOften), ActionRow.of(irlCommitments), ActionRow.of(whyPick))
            .build();

}
