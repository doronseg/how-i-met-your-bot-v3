package me.nerdoron.himyb.commands.staff.moderation.management;

import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.moderation.management.RaidModeHandler;
import me.nerdoron.himyb.modules.moderation.modlogs.ModLogHandler;
import me.nerdoron.himyb.modules.moderation.modlogs.ModLogType;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

public class RaidModeCommand extends SlashCommand {

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();
        assert member != null;

        if (!member.hasPermission(Permission.BAN_MEMBERS)) return;

        boolean currentState = RaidModeHandler.isRaidMode();

        if (event.getOption("state") == null) {
            RaidModeHandler.setRaidMode(!currentState);
            event.reply(String.format("Raid mode has been %s.", currentState ? "disabled" : "enabled")).queue();
        } else {
            boolean newState = Objects.requireNonNull(event.getOption("state")).getAsBoolean();
            RaidModeHandler.setRaidMode(newState);
            event.reply(String.format("Raid mode has been %s.", newState ? "enabled" : "disabled")).queue();
        }


        assert guild != null;
        TextChannel modLogChannel = guild.getTextChannelById(ModLogHandler.modLogChannelId);
        if (modLogChannel != null) {
            //TODO: Fix
            modLogChannel.sendMessage("< @&850439278717829190>")
                    .queue(message -> {
                        String messageId = message.getId();
                        MessageEmbed embed = ModLogHandler.generateLog(
                                ModLogType.RAID_MODE,
                                member.getUser(),
                                member.getUser(),
                                String.format("Raid mode has been %s.", RaidModeHandler.isRaidMode() ? "enabled" : "disabled"),
                                0, 0, messageId);
                        message.editMessageEmbeds(embed).queue();
                    });
        }
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("raidmode", "Enable or disable raid mode.")
                .addOption(OptionType.BOOLEAN, "state", "Set raid mode to enabled or disabled.", false)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS));
    }
}
