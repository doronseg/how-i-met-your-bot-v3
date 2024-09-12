package me.nerdoron.himyb.commands.staff;

import me.nerdoron.himyb.modules.bot.SlashCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class ReviveCommand extends SlashCommand {

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!(event.isFromGuild())) {
            event.deferReply().setEphemeral(true).setContent("This command can only be executed in the server.")
                    .queue();
            return;
        }
        Member member = event.getMember();

        assert member != null;
        if (!member.hasPermission(Permission.MESSAGE_MANAGE))
            return;
        event.getChannel().sendMessage("<@&900487372251213955>").queue();
        event.getChannel().sendMessage("<@&900487372251213955>").queue();
        event.deferReply().setEphemeral(true).setContent("Reviving chat...").queue();

    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("revive", "Revive chat.");
    }
}
