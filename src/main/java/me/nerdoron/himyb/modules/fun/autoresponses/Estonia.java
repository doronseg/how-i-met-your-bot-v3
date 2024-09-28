package me.nerdoron.himyb.modules.fun.autoresponses;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;

public class Estonia extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if ((event.getMessage().getContentDisplay().toLowerCase().contains("estonian"))
                || (event.getMessage().getContentDisplay().toLowerCase().contains("estonia"))) {
            if (event.getAuthor().isBot())
                return;
            FileUpload file = FileUpload.fromData(new File("videos/estonia.mp4"));
            event.getChannel().sendMessage("\uD83C\uDDEA\uD83C\uDDEA").addFiles(file).queue();
        }
    }

}

