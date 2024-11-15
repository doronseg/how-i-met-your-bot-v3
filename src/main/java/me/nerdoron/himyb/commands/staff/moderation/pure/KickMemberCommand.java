package me.nerdoron.himyb.commands.staff.moderation.pure;

import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.moderation.punishments.KickHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

public class KickMemberCommand extends SlashCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        assert member != null;
        if (!member.hasPermission(Permission.BAN_MEMBERS)) return;

        Member target = Objects.requireNonNull(event.getOption("user")).getAsMember();
        String reason = Objects.requireNonNull(event.getOption("reason")).getAsString();

        assert target != null;
        if (target.hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("You can't kick a moderator.").setEphemeral(true).queue();
            return;
        }

        Guild guild = event.getGuild();
        assert guild != null;
        KickHandler kickHandler = new KickHandler();

        kickHandler.KickMember(target, member, guild, reason).thenAccept(success -> {
            if (success) {
                event.reply(String.format("Successfully kicked %s.", target.getAsMention()))
                        .setEphemeral(true)
                        .queue();
            } else {
                event.reply("Error. Could not kick the user.")
                        .setEphemeral(true)
                        .queue();
            }
        }).exceptionally(ex -> {
            event.reply("An unexpected error occurred while kicking the user.")
                    .setEphemeral(true)
                    .queue();
            return null;
        });

    }

    @Override
    public SlashCommandData getSlash() {
        SlashCommandData kick = Commands.slash("kick", "Kick a user.");
        kick.addOption(OptionType.USER, "user", "Who would you like to kick?", true);
        kick.addOption(OptionType.STRING, "reason", "What's the reason for the kick?", true);
        kick.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS));
        return kick;
    }
}
