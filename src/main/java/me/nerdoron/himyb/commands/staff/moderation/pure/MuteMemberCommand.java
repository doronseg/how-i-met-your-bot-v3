package me.nerdoron.himyb.commands.staff.moderation.pure;

import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.moderation.punishments.MuteHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

public class MuteMemberCommand extends SlashCommand {

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        assert member != null;
        if (!member.hasPermission(Permission.BAN_MEMBERS)) return;

        Member target = Objects.requireNonNull(event.getOption("user")).getAsMember();
        String reason = Objects.requireNonNull(event.getOption("reason")).getAsString();
        int timeInHours = Objects.requireNonNull(event.getOption("timeinhours")).getAsInt();

        assert target != null;
        if (target.hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("You can't mute a moderator.").setEphemeral(true).queue();
            return;
        }
        MuteHandler muteHandler = new MuteHandler();
        Guild guild = event.getGuild();
        assert guild != null;
        if (target.getRoles().contains(guild.getRoleById(MuteHandler.muteRoleId))) {
            event.reply(String.format("%s is already muted!", target.getAsMention())).setEphemeral(true).queue();
            return;
        }

        muteHandler.muteMember(target, member.getUser(), timeInHours, guild, reason).thenAccept(success -> {
            if (success) {
                event.reply(String.format("Successfully muted %s for %d hours.", target.getAsMention(), timeInHours))
                        .setEphemeral(true)
                        .queue();
            } else {
                event.reply("Error. Could not mute the user.")
                        .setEphemeral(true)
                        .queue();
            }
        }).exceptionally(ex -> {
            event.reply("An unexpected error occurred while muting the user.")
                    .setEphemeral(true)
                    .queue();
            return null;
        });

    }

    @Override
    public SlashCommandData getSlash() {
        SlashCommandData mute = Commands.slash("mute", "Mute a user.");
        mute.addOption(OptionType.USER, "user", "Who would you like to mute?", true);
        OptionData time = new OptionData(OptionType.INTEGER, "timeinhours", "For how long (time in hours) would you like to mute this user?", true);
        time.setMinValue(0);
        time.setMaxValue(56);
        mute.addOptions(time);
        mute.addOption(OptionType.STRING, "reason", "What's the reason for the mute?", true);
        mute.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS));
        return mute;
    }

}
