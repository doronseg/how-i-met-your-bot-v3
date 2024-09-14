package me.nerdoron.himyb.modules.useful.tickets;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.ArrayList;

public class MessageHistoryRetriever {
    final ArrayList<Message> output = new ArrayList<>();
    Boolean isReady = false;

    public MessageHistoryRetriever() {
    }

    public void getHistory(TextChannel channel) {
        int messages = 500;

        channel.getIterableHistory()
                .takeAsync(messages)
                .thenApplyAsync((msgs) -> {
                    ArrayList<Message> msgList = new ArrayList<>(msgs);
                    for (Message message : msgList) {
                        if (!message.getAuthor().isBot()) {
                            output.add(message);
                        }
                    }
                    isReady = true;
                    return 0;
                });
    }
}
