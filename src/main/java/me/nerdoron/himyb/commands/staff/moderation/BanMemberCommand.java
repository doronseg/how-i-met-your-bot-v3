package me.nerdoron.himyb.commands.staff.moderation;

import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.moderation.punishments.BanHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

public class BanMemberCommand extends SlashCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        assert member != null;
        if (!member.hasPermission(Permission.BAN_MEMBERS)) return;

        Member target = Objects.requireNonNull(event.getOption("user")).getAsMember();
        String reason = Objects.requireNonNull(event.getOption("reason")).getAsString();

        assert target != null;
        if (target.hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("You can't ban a moderator.").setEphemeral(true).queue();
            return;
        }

        Guild guild = event.getGuild();
        assert guild != null;
        BanHandler banHandler = new BanHandler();

        banHandler.BanMember(target, member, guild, reason).thenAccept(success -> {
            if (success) {
                event.reply(String.format("Successfully banned %s.", target.getAsMention()))
                        .setEphemeral(true)
                        .queue();
            } else {
                event.reply("Error. Could not ban the user.")
                        .setEphemeral(true)
                        .queue();
            }
        }).exceptionally(ex -> {
            event.reply("An unexpected error occurred while banning the user.")
                    .setEphemeral(true)
                    .queue();
            return null;
        });

    }

    @Override
    public SlashCommandData getSlash() {
        SlashCommandData ban = Commands.slash("ban", "Ban a user.");
        ban.addOption(OptionType.USER, "user", "Who would you like to ban?", true);
        ban.addOption(OptionType.STRING, "reason", "What's the reason for the ban?", true);
        ban.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS));
        return ban;
    }
}
