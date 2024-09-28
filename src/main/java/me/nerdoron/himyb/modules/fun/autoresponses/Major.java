package me.nerdoron.himyb.modules.fun.autoresponses;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class Major extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;
        if (event.getMessage().getContentDisplay().toLowerCase().contains("major ")) {
            String content = event.getMessage().getContentDisplay();
            String[] split = content.split("(?i)\\bmajor\\b ");
            if (split.length == 0) return;
            String after = split[1];
            event.getMessage().reply("Major " + after + " <:tedsalute:884824742266290268>").queue();
        } else if (event.getMessage().getContentDisplay().toLowerCase().contains("corporal ")) {
            String content = event.getMessage().getContentDisplay();
            String[] split = content.split("(?i)\\bcorporal\\b ");
            if (split.length == 0) return;
            String after = split[1];
            event.getMessage().reply("Corporal " + after + " <:tedsalute:884824742266290268>").queue();
        } else if (event.getMessage().getContentDisplay().toLowerCase().contains("private ")) {
            String content = event.getMessage().getContentDisplay();
            String[] split = content.split("(?i)\\bprivate\\b ");
            if (split.length == 0) return;
            String after = split[1];
            event.getMessage().reply("Private " + after + " <:tedsalute:884824742266290268>").queue();
        } else if (event.getMessage().getContentDisplay().toLowerCase().contains("general ")) {
            String content = event.getMessage().getContentDisplay();
            String[] split = content.split("(?i)\\bgeneral\\b ");
            if (split.length == 0) return;
            String after = split[1];
            event.getMessage().reply("General " + after + " <:tedsalute:884824742266290268>").queue();
        } else if (event.getMessage().getContentDisplay().toLowerCase().contains("colonel ")) {
            String content = event.getMessage().getContentDisplay();
            String[] split = content.split("(?i)\\bcolonel\\b ");
            if (split.length == 0) return;
            String after = split[1];
            event.getMessage().reply("General " + after + " <:tedsalute:884824742266290268>").queue();
        }

    }

}
