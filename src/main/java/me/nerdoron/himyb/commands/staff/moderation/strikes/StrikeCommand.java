package me.nerdoron.himyb.commands.staff.moderation.strikes;

import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.moderation.strikes.StrikeHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

public class StrikeCommand extends SlashCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        assert member != null;

        if (!member.hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("You do not have permission to use this command.").setEphemeral(true).queue();
            return;
        }

        Member target = Objects.requireNonNull(event.getOption("user")).getAsMember();
        String reason = Objects.requireNonNull(event.getOption("reason")).getAsString();
        int amount = event.getOption("amount") != null ? Objects.requireNonNull(event.getOption("amount")).getAsInt() : 1;

        assert target != null;
        if (target.hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("You can't strike a moderator.").setEphemeral(true).queue();
            return;
        }

        StrikeHandler strikeHandler = new StrikeHandler();
        if (strikeHandler.executeStrike(event, target, amount, reason)) {
            event.reply(String.format("Issued %d strike(s) to %s for: `%s`.", amount, target.getAsMention(), reason)).setEphemeral(true).queue();
        } else {
            event.reply("An error has occurred").setEphemeral(true).queue();
        }

    }

    @Override
    public SlashCommandData getSlash() {
        SlashCommandData strike = Commands.slash("strike", "Issue a strike to a user.");
        strike.addOption(OptionType.USER, "user", "Who would you like to strike?", true);
        strike.addOption(OptionType.STRING, "reason", "What's the reason for the strike?", true);
        OptionData amount = new OptionData(OptionType.INTEGER, "amount", "How many strikes would you like to give?", false);
        amount.setMinValue(1);
        amount.setMaxValue(6);
        strike.addOptions(amount);
        strike.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS));
        return strike;
    }
}
