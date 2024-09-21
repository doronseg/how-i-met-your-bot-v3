package me.nerdoron.himyb.modules.fun.counting;

import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CountingEditing extends ListenerAdapter {
    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        if (event.getChannel().getId().equals("900090953233231964")) {
            event.getMessage().delete().queue();
        }
    }
}
