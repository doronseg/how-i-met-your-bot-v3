package me.nerdoron.himyb.modules.useful.birthdays;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class BirthdayModal {


    final TextInput dayInput = TextInput
            .create("birthday-day", "What day were you born?", TextInputStyle.SHORT)
            .setRequired(true)
            .setRequiredRange(1, 31)
            .setPlaceholder("Number between 1-31")
            .build();
    final TextInput monthInput = TextInput
            .create("birthday-month", "What month were you born?", TextInputStyle.SHORT)
            .setRequired(true)
            .setRequiredRange(1, 12)
            .setPlaceholder("Number between 1-12")
            .build();

    public final Modal bdayModal = Modal.create("birthday-modal", "Set your birthday")
            .addComponents(ActionRow.of(monthInput), ActionRow.of(dayInput))
            .build();


}
