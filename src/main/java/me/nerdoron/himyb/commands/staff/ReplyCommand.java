package me.nerdoron.himyb.commands.staff;

import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;

import java.util.Objects;

public class ReplyCommand extends SlashCommand {
    private static final Logger logger = LoggingHandler.logger(ReplyCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        assert member != null;
        if (!(member.hasPermission(Permission.MESSAGE_MANAGE)))
            return;
        String messageId = Objects.requireNonNull(event.getOption("replyto")).getAsString();
        Message messageToReply = event.getChannel().retrieveMessageById(messageId).complete();
        String message = Objects.requireNonNull(event.getOption("message")).getAsString();
        messageToReply.reply(message).queue();
        TextChannel logsChannel = Objects.requireNonNull(event.getGuild()).getTextChannelById("850447694673739816");
        event.deferReply().setEphemeral(true).setContent("Sent your message.").queue();
        assert logsChannel != null;
        logsChannel.sendMessage(String.format("%s used say command in channel %s.", event.getUser().getName(), event.getChannel().getAsMention())).
                queue();
        logger.info("{} used reply command in channel #{}.", event.getUser().getName(), event.getChannel().getName());

    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("reply", "Make the bot reply to a message")
                .addOption(OptionType.STRING, "replyto",
                        "What message would you like to reply to? (ID ONLY, MAKE SURE YOU ARE IN THE SAME CHANNEL)",
                        true)
                .addOption(OptionType.STRING, "message", "What would you like to say?", true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE));
    }
}
