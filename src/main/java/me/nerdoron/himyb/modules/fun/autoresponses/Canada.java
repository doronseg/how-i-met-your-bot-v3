package me.nerdoron.himyb.modules.fun.autoresponses;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;

public class Canada extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if ((event.getMessage().getContentDisplay().toLowerCase().contains("canadian"))
                || (event.getMessage().getContentDisplay().toLowerCase().contains("canada"))) {
            if (event.getAuthor().isBot())
                return;
            FileUpload file = FileUpload.fromData(new File("videos/ohcanada.mp4"));
            event.getChannel().sendMessage("\uD83C\uDDE8\uD83C\uDDE6").addFiles(file).queue();
        }
    }

}

