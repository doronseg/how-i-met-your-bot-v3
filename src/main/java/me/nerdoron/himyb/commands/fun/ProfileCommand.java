package me.nerdoron.himyb.commands.fun;

import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.fun.profile.ProfileHelper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

public class ProfileCommand extends SlashCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (event.getInteraction().getOptions().isEmpty()) {
            event.replyEmbeds(ProfileHelper.userProfile(member)).queue();
            return;
        }
        Member memberToCheck = Objects.requireNonNull(event.getInteraction().getOption("user")).getAsMember();
        assert memberToCheck != null;
        event.replyEmbeds(ProfileHelper.userProfile(memberToCheck)).queue();
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("profile", "Check a user's profile with how i met your bot.")
                .addOption(OptionType.USER, "user", "who would you like to see?", false);
    }
}
