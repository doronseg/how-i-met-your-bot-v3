package me.nerdoron.himyb.modules.useful.applications;

import me.nerdoron.himyb.Global;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class EventManagerApplicationHandler extends ListenerAdapter {

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (!event.getModalId().equals("application-event")) return;

        String typesOfEvents = Objects.requireNonNull(event.getValue("event-type")).getAsString();
        String howOften = Objects.requireNonNull(event.getValue("event-often")).getAsString();
        String irlCommitments = Objects.requireNonNull(event.getValue("event-irl")).getAsString();
        String whyPick = Objects.requireNonNull(event.getValue("event-picked")).getAsString();

        MessageEmbed application = new EmbedBuilder()
                .setTitle("Event Manager Application")
                .setDescription("Application sent by: " + event.getUser().getAsMention() + "\nID:"
                        + event.getUser().getId())
                .addField("What types of events are you able to host?", typesOfEvents, false)
                .addField("How often are you able to host events?", howOften, false)
                .addField("What IRL commitments would affect your activity?", irlCommitments, false)
                .addField("Why should we pick you over other candidates?", whyPick, false)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();

        Objects.requireNonNull(Objects.requireNonNull(event.getGuild()).getTextChannelById("983357026463805510")).sendMessageEmbeds(application).queue();
        event.deferReply().setEphemeral(true).setContent("Sent your application!").queue();
    }

}
