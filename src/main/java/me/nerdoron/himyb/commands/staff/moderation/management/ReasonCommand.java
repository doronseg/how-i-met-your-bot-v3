package me.nerdoron.himyb.commands.staff.moderation.management;

import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.moderation.modlogs.ModLogHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReasonCommand extends SlashCommand {
    private static final Logger logger = LoggingHandler.logger(ReasonCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        assert member != null;
        if (!member.hasPermission(Permission.BAN_MEMBERS)) return;

        int id = Objects.requireNonNull(event.getOption("id")).getAsInt();
        String reason = Objects.requireNonNull(event.getOption("reason")).getAsString();

        ModLogHandler modLogHandler = new ModLogHandler();

        String modLogDiscordId = modLogHandler.getDiscordMessageIdByModLogId(id);
        if (modLogDiscordId == null) {
            event.reply("Modlog not found").setEphemeral(true).queue();
            return;
        }

        Guild guild = event.getGuild();
        assert guild != null;
        TextChannel modlogsChannel = guild.getTextChannelById(ModLogHandler.modLogChannelId);
        assert modlogsChannel != null;
        Message modLogMessage = modlogsChannel.retrieveMessageById(modLogDiscordId).complete();
        modLogMessage.editMessageEmbeds(updateEmbedReason(modLogMessage.getEmbeds().get(0), reason)).queue(
                success -> {
                    logger.info("{}(ID:{}) Updated mod log message for ID {}", member.getUser().getName(), member.getId(), id);
                    event.reply("Successfully edited this log's reason").setEphemeral(true).queue();
                    modLogHandler.updateModLogReason(id, reason);
                },
                failure -> {
                    logger.error("Failed to edit mod log message: {}", failure.getMessage());
                    event.reply("An error has occurred.").setEphemeral(true).queue();
                }
        );

    }

    private MessageEmbed updateEmbedReason(MessageEmbed originalEmbed, String newReason) {
        EmbedBuilder embedBuilder = new EmbedBuilder(originalEmbed);
        List<MessageEmbed.Field> fields = new ArrayList<>(originalEmbed.getFields());
        embedBuilder.clearFields();
        for (MessageEmbed.Field field : fields) {
            if (Objects.requireNonNull(field.getName()).equalsIgnoreCase("reason")) {
                embedBuilder.addField("Reason", newReason, false);
            } else {
                embedBuilder.addField(field);
            }
        }
        return embedBuilder.build();
    }


    @Override
    public SlashCommandData getSlash() {
        SlashCommandData editLog = Commands.slash("reason", "Edit a modlog's reason");
        editLog.addOption(OptionType.INTEGER, "id", "Modlog ID", true);
        editLog.addOption(OptionType.STRING, "reason", "New reason", true);
        editLog.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS));
        return editLog;
    }
}
