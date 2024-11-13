package me.nerdoron.himyb.commands.staff.moderation;

import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.moderation.punishments.MuteHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

public class UnmuteMemberCommand extends SlashCommand {

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        assert member != null;
        if (!member.hasPermission(Permission.BAN_MEMBERS)) return;

        Member target = Objects.requireNonNull(event.getOption("user")).getAsMember();
        String reason = Objects.requireNonNull(event.getOption("reason")).getAsString();

        assert target != null;
        Guild guild = event.getGuild();
        assert guild != null;
        MuteHandler muteHandler = new MuteHandler();

        muteHandler.unMuteMember(target, member.getUser(), guild, reason).thenAccept(success -> {
            if (success) {
                event.reply(String.format("Successfully unmuted %s.", target.getAsMention()))
                        .setEphemeral(true)
                        .queue();
            } else {
                event.reply("Error. Could not unmute the user.")
                        .setEphemeral(true)
                        .queue();
            }
        }).exceptionally(ex -> {
            event.reply("An unexpected error occurred while unmuting the user.")
                    .setEphemeral(true)
                    .queue();
            return null;
        });
    }

    @Override
    public SlashCommandData getSlash() {
        SlashCommandData unmute = Commands.slash("unmute", "Unmute a user.");
        unmute.addOption(OptionType.USER, "user", "Who would you like to unmute?", true);
        unmute.addOption(OptionType.STRING, "reason", "What's the reason for unmuting?", true);
        unmute.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS));
        return unmute;
    }
}
