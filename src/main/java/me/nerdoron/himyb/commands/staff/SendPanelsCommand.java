package me.nerdoron.himyb.commands.staff;

import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.useful.tickets.Panels;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;

public class SendPanelsCommand extends SlashCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        assert member != null;
        String uid = member.getId();

        ArrayList<Button> buttons = new ArrayList<>();
        buttons.add(Button.primary("TICKET:" + uid + ":STAFF", "\uD83D\uDCC7 Create a ticket"));
        buttons.add(Button.danger("TICKET:" + uid + ":ADMIN", "\uD83D\uDCC7 Create an Admin ticket"));

        event.getChannel().sendMessageEmbeds(Panels.AdminStaff)
                .addActionRow(buttons)
                .queue();
        event.deferReply().setContent("Done.").setEphemeral(true
        ).queue();


    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("panels", "Send the ticket panels")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR));
    }
}
