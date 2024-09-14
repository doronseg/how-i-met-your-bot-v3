package me.nerdoron.himyb.modules.useful.tickets;

import me.nerdoron.himyb.Global;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class TicketClosingHandler {

    @SuppressWarnings("WriteOnlyObject")
    static void sendTranscript(ButtonInteractionEvent event) {
        TextChannel channel = event.getChannel().asTextChannel();
        String transcript = GenerateTranscript.generate(channel);
        File file = new File(channel.getName() + ".txt");
        AtomicBoolean delete = new AtomicBoolean(false);

        channel.getHistoryFromBeginning(1).queue(
                messageHistory -> {
                    Message message = messageHistory.getRetrievedHistory().get(0);
                    User ticketAuthor = message.getMentions().getUsers().get(0);
                    MessageEmbed embed = new EmbedBuilder()
                            .setColor(Global.embedColor)
                            .setDescription(
                                    "**Transcript from " + ticketAuthor.getAsMention() + "'s ticket**" + "\n" +
                                            "TicketID: " + channel.getName() + "\n" +
                                            "Closed by: " + event.getUser().getAsMention() + " `(" + event.getUser().getId() + ")`\n" +
                                            "Closed at: " + getAsTimeThing(event.getTimeCreated()))
                            .build();
                    try {
                        writeToFile(file, transcript.split("\n"));
                        FileUpload f = FileUpload.fromData(file);
                        if (channel.getName().startsWith("ticket-")) {
                            TextChannel ticketTranscriptChannel = Objects.requireNonNull(event.getGuild()).getTextChannelById("991294991517360200");
                            assert ticketTranscriptChannel != null;
                            ticketTranscriptChannel.sendMessageEmbeds(embed).addFiles(f).queue();
                        } else if (channel.getName().startsWith("admin")) {
                            TextChannel adminTranscriptChannel = Objects.requireNonNull(event.getGuild()).getTextChannelById("991376488010109008");
                            assert adminTranscriptChannel != null;
                            adminTranscriptChannel.sendMessageEmbeds(embed).addFiles(f).queue();
                        }
                        channel.delete().queue();
                        delete.set(file.delete());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private static void writeToFile(File file, String[] lines) throws IOException {
        FileWriter writer = new FileWriter(file);
        for (String line : lines) {
            writer.write(line);
            writer.write("\n");
        }
        writer.close();
    }

    private static String getAsTimeThing(OffsetDateTime time) {
        return "<t:" + time.toEpochSecond() + ":" + "f" + ">";
    }

}
