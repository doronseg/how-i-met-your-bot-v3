package me.nerdoron.himyb.modules.useful.tickets;

import me.nerdoron.himyb.modules.bot.Database;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;

public class GenerateTranscript {


    public static String generate(TextChannel channel) {
        MessageHistoryRetriever retriever = new MessageHistoryRetriever();
        retriever.getHistory(channel);

        while (!retriever.isReady) {
            try {
                //noinspection BusyWait
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }

        ArrayList<Message> messages = retriever.output;
        Collections.reverse(messages);
        StringBuilder builder = new StringBuilder();

        for (Message message : messages) {
            String tag = message.getAuthor().getAsTag();
            String content = message.getContentRaw();
            OffsetDateTime time = message.getTimeCreated();
            String parsed = parseTime(time);
            ArrayList<String> messageIdsFromDb = TicketFileMonitor.getFromLinker(message.getId());

            if (messageIdsFromDb.isEmpty()) {
                builder.append(parsed).append(tag).append(": ").append(content).append("\n");
                continue;
            }
            ArrayList<String> files = new ArrayList<>();
            for (String s : messageIdsFromDb) {
                files.add("⛓️[" + s + "] ; ");
                deleteFromLinker(s);
            }

            builder.append(parsed).append(tag).append(": ").append(content).append(files).append("\n");
        }
        return builder.toString();
    }

    public static String numberHelper(long N) {
        if (N < 10) {
            return "0" + N;
        } else {
            return "" + N;
        }
    }

    public static String parseTime(OffsetDateTime time) {
        return "[" + numberHelper(time.getHour()) + ":" + numberHelper(time.getMinute()) + ":"
                + numberHelper(time.getSecond()) + "] ";
    }

    private static void deleteFromLinker(String messageId) {
        final Connection con = Database.connect();
        assert con != null;
        try {
            String SQL = "DELETE from filemonitor where messageId=?";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, messageId);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
