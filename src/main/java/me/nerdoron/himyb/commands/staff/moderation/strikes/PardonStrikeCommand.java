package me.nerdoron.himyb.commands.staff.moderation.strikes;

import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.moderation.strikes.StrikeHandler;
import me.nerdoron.himyb.modules.moderation.strikes.StrikeSQL;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

public class PardonStrikeCommand extends SlashCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        assert member != null;
        if (!member.hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("You do not have permission to pardon strikes.").setEphemeral(true).queue();
            return;
        }
        Member target = Objects.requireNonNull(event.getOption("user")).getAsMember();
        String reason = Objects.requireNonNull(event.getOption("reason")).getAsString();
        int strikesToPardon = Objects.requireNonNull(event.getOption("amount")).getAsInt();

        if (target == null) {
            event.reply("Invalid member.").setEphemeral(true).queue();
            return;
        }

        int currentStrikes = StrikeSQL.getStrikes(target);
        if (currentStrikes < strikesToPardon) {
            event.reply("The target doesn't have enough strikes to pardon.").setEphemeral(true).queue();
            return;
        }

        StrikeHandler strikeHandler = new StrikeHandler();
        strikeHandler.pardonStrike(event, target, strikesToPardon, reason);


    }

    @Override
    public SlashCommandData getSlash() {
        SlashCommandData pardon = Commands.slash("pardon", "Pardon strikes from a user.");
        pardon.addOption(OptionType.USER, "user", "Who would you like to pardon?", true);
        pardon.addOption(OptionType.STRING, "reason", "What's the reason for the pardon?", true);
        OptionData amount = new OptionData(OptionType.INTEGER, "amount", "How many strikes would you like to pardon?", true);
        amount.setMinValue(1);
        amount.setMaxValue(5);
        pardon.addOptions(amount);
        pardon.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS));
        return pardon;
    }
}
