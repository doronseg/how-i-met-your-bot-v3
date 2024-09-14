package me.nerdoron.himyb.modules.useful.tickets;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;

public class TicketButtonHandler extends ListenerAdapter {

    private final EnumSet<Permission> perms;

    public TicketButtonHandler() {
        this.perms = EnumSet.of(Permission.MESSAGE_HISTORY, Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Member member = event.getMember();
        assert member != null;
        Guild guild = event.getGuild();
        assert guild != null;

        String buttonId = event.getComponentId();
        if (buttonId.contains("TICKET:")) {
            String[] buttonSplit = buttonId.split(":");
            String buttonCategory = buttonSplit[2];
            Role modRole = Objects.requireNonNull(event.getGuild()).getRoleById("850439278717829190");
            assert modRole != null;
            String categoryId = "850458148723621888";
            Button closeButton = Button.danger("closeTicket", "Close this ticket");

            switch (buttonCategory) {
                case "ADMIN":
                    String adminName = "admin-" + member.getEffectiveName();
                    TextChannel adminChannel = Objects.requireNonNull(guild.getCategoryById(categoryId))
                            .createTextChannel(adminName)
                            .addPermissionOverride(guild.getPublicRole(), new ArrayList<>(), perms)
                            .addPermissionOverride(member, perms, new ArrayList<>())
                            .complete();

                    event.deferReply().setEphemeral(true)
                            .setContent(String.format("Created an admin ticket for you, %s", adminChannel.getAsMention())).queue();
                    adminChannel.sendMessage(member.getAsMention())
                            .addEmbeds(Panels.adminWelcome)
                            .addActionRow(closeButton)
                            .queue();
                    break;
                case "STAFF":
                    String staffName = "ticket-" + member.getEffectiveName();
                    TextChannel staffChannel = Objects.requireNonNull(guild.getCategoryById(categoryId))
                            .createTextChannel(staffName)
                            .addPermissionOverride(guild.getPublicRole(), new ArrayList<>(), perms)
                            .addPermissionOverride(modRole, perms, new ArrayList<>())
                            .addPermissionOverride(member, perms, new ArrayList<>())
                            .complete();

                    event.deferReply().setEphemeral(true)
                            .setContent(String.format("Created an admin ticket for you, %s", staffChannel.getAsMention())).queue();
                    staffChannel.sendMessage(member.getAsMention())
                            .addEmbeds(Panels.generalWelcome)
                            .addActionRow(closeButton)
                            .queue();
                    break;
            }
        } else if (buttonId.contains("closeTicket")) {
            TextChannel channel = event.getChannel().asTextChannel();
            if (!channel.getName().startsWith("ticket-") && !channel.getName().startsWith("admin-")) {
                event.reply("This isn't a ticket channel!").queue();
                return;
            }
            channel.getManager().clearOverridesAdded().queue();
            channel.getManager()
                    .putPermissionOverride(event.getGuild().getPublicRole(), null,
                            Arrays.asList(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND))
                    .queue(
                            (__) -> {
                                TicketClosingHandler.sendTranscript(event);
                                event.reply("Closing ticket.").queue();
                            });
        }
    }


}
