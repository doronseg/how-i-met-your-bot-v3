package me.nerdoron.himyb.modules.useful.tickets;

import me.nerdoron.himyb.modules.bot.Database;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class TicketFileMonitor extends ListenerAdapter {
    static final Logger logger = LoggingHandler.logger(TicketFileMonitor.class);
    String monitorId = "1001811762070421534";

    private static void putToLinker(String messageId, String linkerId) {
        final Connection con = Database.connect();
        assert con != null;
        try {
            String SQL = "INSERT into filemonitor(messageid, attachment) values(?,?)";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, messageId);
            ps.setString(2, linkerId);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static ArrayList<String> getFromLinker(String messageId) {
        final Connection con = Database.connect();
        ArrayList<String> messageIds = new ArrayList<>();
        assert con != null;
        try {
            String SQL = "SELECT attachment from filemonitor where messageid=?";
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, messageId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String result = rs.getString(1);
                messageIds.add(result);
            }
            ps.close();
            return messageIds;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("WriteOnlyObject")
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        TextChannel monitorChannel = event.getGuild().getTextChannelById(monitorId);
        assert monitorChannel != null;
        Member member = event.getMember();
        AtomicBoolean delete = new AtomicBoolean(false);
        assert member != null;
        if (!event.isFromGuild()) return;
        Message message = event.getMessage();
        if (message.getAttachments().isEmpty()) return;
        if (!event.getChannel().getName().startsWith("ticket-") && !event.getChannel().getName().startsWith("admin-"))
            return;
        if (event.getChannel().getName().contains("transcripts")) return;

        for (Message.Attachment attachment : message.getAttachments()) {
            attachment.getProxy().downloadToFile(new File("./" + attachment.getFileName())).whenComplete(
                    (temp, err) -> {
                        if (temp == null) {
                            err.printStackTrace();
                            return;
                        }
                        FileUpload file = FileUpload.fromData(temp);

                        monitorChannel.sendMessage("From: " + member.getEffectiveName() + "\nIn channel: " + message.getChannel().getName()).addFiles(file).queue(
                                (msgInMonitor) -> {
                                    putToLinker(message.getId(), msgInMonitor.getAttachments().get(0).getProxyUrl());
                                    delete.set(temp.delete());
                                },
                                (error) -> {
                                    error.printStackTrace();
                                    delete.set(temp.delete());
                                });
                    });
        }
    }

}
