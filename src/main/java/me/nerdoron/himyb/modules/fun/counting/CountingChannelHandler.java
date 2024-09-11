package me.nerdoron.himyb.modules.fun.counting;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CountingChannelHandler extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Member member = event.getMember();
        assert member != null;
        if (member.getUser().isBot()) {
            event.getMessage().delete().queue();
            return;
        }
        if (!(event.getChannel().getId().equals("900090953233231964")))
            return;

        String uid = member.getId();
        String messageContent = event.getMessage().getContentDisplay();
        int numberContent;
        try {
            numberContent = Integer.parseInt(messageContent);
        } catch (NumberFormatException e) {
            event.getMessage().delete().queue();
            return;
        }

        event.getChannel().getHistory().retrievePast(2).map(messages -> messages.get(1)).queue(message -> {

            String oldMessageContent = message.getContentDisplay();
            int oldNumberContent;

            try {
                oldNumberContent = Integer.parseInt(oldMessageContent);
            } catch (NumberFormatException e) {
                event.getMessage().delete().queue();
                return;
            }

            if (!(numberContent == oldNumberContent + 1)) {
                event.getMessage().delete().queue();
                return;
            }

            if (message.getAuthor().getId().equals(uid)) {
                event.getMessage().delete().queue();
                return;
            }

            if (!message.getStickers().isEmpty()) {
                event.getMessage().delete().queue();
            }
        });


    }
}
