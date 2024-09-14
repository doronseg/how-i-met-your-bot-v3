package me.nerdoron.himyb.commands.staff;

import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;

import java.util.Objects;

public class SayCommand extends SlashCommand {
    private static final Logger logger = LoggingHandler.logger(SayCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        assert member != null;
        TextChannel logsChannel = Objects.requireNonNull(event.getGuild()).getTextChannelById("850447694673739816");
        assert logsChannel != null;
        if (!member.hasPermission(Permission.MESSAGE_MANAGE)) return;
        String message = Objects.requireNonNull(event.getOption("message")).getAsString();
        if (message.equals("🐶")) {
            event.reply("stop.").setEphemeral(true).queue();
            logsChannel.sendMessage(event.getUser().getAsMention() + " tried to send a zitch dog with say command")
                    .queue();
            return;
        }
        event.getChannel().sendMessage(message).queue();
        event.deferReply().setEphemeral(true).setContent("Sent your message.").queue();
        logsChannel.sendMessage(String.format("%s used say command in channel %s.", event.getUser().getName(), event.getChannel().getAsMention())).
                queue();
        logger.info("{} used say command in channel #{}.", event.getUser().getName(), event.getChannel().getName());

    }

    @Override
    public SlashCommandData getSlash() {
        SlashCommandData say = Commands.slash("say", "Make the bot say something");
        say.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE));
        say.addOption(OptionType.STRING, "message", "What would you like to say?", true);
        return say;
    }
}
