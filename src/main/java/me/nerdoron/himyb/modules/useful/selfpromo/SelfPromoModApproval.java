package me.nerdoron.himyb.modules.useful.selfpromo;

import me.nerdoron.himyb.Global;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class SelfPromoModApproval extends ListenerAdapter {

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (!event.getModalId().equals("selfpromo-modal")) return;
        Guild guild = event.getGuild();
        assert guild != null;
        TextChannel modApproval = guild.getTextChannelById("991281346699870238");
        assert modApproval != null;


        String link = Objects.requireNonNull(event.getValue("selfpromo-link")).getAsString();
        String description = Objects.requireNonNull(event.getValue("selfpromo-desc")).getAsString();
        String additional = Objects.requireNonNull(event.getValue("selfpromo-additional")).getAsString();

        MessageEmbed modApprovalEmbed = new EmbedBuilder()
                .setTitle("Self Promotion Link Submission")
                .setDescription("Sent by: " + event.getUser().getAsMention() + "\nID:"
                        + event.getUser().getId())
                .addField("Link of promotion", link, false)
                .addField("Description of promotion", description, false)
                .addField("Additional information", additional, false)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();

        modApproval.sendMessageEmbeds(modApprovalEmbed).queue();
        event.deferReply().setContent("Sent your link for review!").setEphemeral(true).queue();
    }

}
