package me.nerdoron.himyb.commands.staff;

import me.nerdoron.himyb.modules.bot.Database;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.useful.birthdays.BirthdayChecks;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class RemoveBirthdayCommand extends SlashCommand {
    BirthdayChecks birthdayChecks = new BirthdayChecks();

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = Objects.requireNonNull(event.getOption("member")).getAsMember();

        if (!Objects.requireNonNull(event.getMember()).getPermissions().contains(Permission.KICK_MEMBERS)) {
            event.reply("I'm Sorry but only Moderators can execute this command").queue();
            return;
        }

        assert member != null;
        if (!birthdayChecks.hasBirthday(member.getId())) {
            event.deferReply().setEphemeral(true).setContent("Doest have a birthday set!").queue();
            return;
        }

        Connection con = Database.connect();
        String statement = "DELETE FROM birthday WHERE uid = ?";
        try {
            assert con != null;
            PreparedStatement ps = con.prepareStatement(statement);
            ps.setString(1, member.getId());
            ps.execute();
            event.reply("The birthday of " + member.getAsMention() + "was deleted from the database").setEphemeral(true)
                    .queue();
        } catch (SQLException ex) {
            event.reply("There was an error in the DB").setEphemeral(true).queue();
            ex.printStackTrace();
        }
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("removebirthday", "Removes a birthday of a user")
                .addOption(OptionType.MENTIONABLE, "member", "Who to remove the birthday from", true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.KICK_MEMBERS));
    }
}