package me.nerdoron.himyb.modules.moderation.strikes;

import me.nerdoron.himyb.modules.moderation.modlogs.ModLogHandler;
import me.nerdoron.himyb.modules.moderation.modlogs.ModLogType;
import me.nerdoron.himyb.modules.moderation.punishments.BanHandler;
import me.nerdoron.himyb.modules.moderation.punishments.KickHandler;
import me.nerdoron.himyb.modules.moderation.punishments.MuteHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StrikeHandler {

    private static final Logger logger = LoggerFactory.getLogger(StrikeHandler.class);

    public boolean executeStrike(SlashCommandInteractionEvent event, Member member, int amount, String reason) {
        int strikes = StrikeSQL.getStrikes(member);
        int newStrikes = strikes + amount;

        if (StrikeSQL.addStrike(member, amount)) {
            String finalReason = String.format("`[%d -> %d strikes]` %s", strikes, newStrikes, reason);
            executePunishment(event, newStrikes, member, finalReason);
            return true;
        } else {
            return false;
        }
    }

    public void pardonStrike(SlashCommandInteractionEvent event, Member member, int amount, String reason) {
        int strikes = StrikeSQL.getStrikes(member);
        int newStrikeCount = strikes - amount;
        if (newStrikeCount < 0) newStrikeCount = 0;

        boolean updated = StrikeSQL.removeStrike(member, amount);
        if (!updated) {
            event.reply("Failed to pardon strikes, please try again later.").setEphemeral(true).queue();
            return;
        }

        User mod = event.getUser();
        Guild guild = event.getGuild();
        assert guild != null;
        TextChannel modLogChannel = guild.getTextChannelById(ModLogHandler.modLogChannelId);
        assert modLogChannel != null;

        modLogChannel.sendMessage("Loading...").queue(message -> {
            String messageId = message.getId();
            MessageEmbed embed = ModLogHandler.generateLog(
                    ModLogType.PARDON, mod, member.getUser(), reason, 0, amount, messageId);
            message.editMessage("\u200E").queue();
            message.editMessageEmbeds(embed).queue();
        });
        sendDM(member, String.format("You've been pardoned from %d strike(s) in how i met your discord for: `%s`", amount, reason));
        event.reply(String.format("Successfully pardoned %d strike(s) from %s for: `%s`", amount, member.getEffectiveName(), reason))
                .setEphemeral(true)
                .queue();
    }

    public void executePunishment(SlashCommandInteractionEvent event, int strikes, Member member, String reason) {
        MuteHandler muteHandler = new MuteHandler();
        BanHandler banHandler = new BanHandler();
        KickHandler kickHandler = new KickHandler();
        User mod = event.getUser();
        Guild guild = event.getGuild();
        assert guild != null;
        TextChannel modLogChannel = guild.getTextChannelById(ModLogHandler.modLogChannelId);
        assert modLogChannel != null;

        switch (strikes) {
            case 1:
                modLogChannel.sendMessage("Loading...").queue(message -> {
                    String messageId = message.getId();
                    MessageEmbed embed = ModLogHandler.generateLog(
                            ModLogType.STRIKE, mod, member.getUser(), reason, 0, 0, messageId);
                    message.editMessageEmbeds(embed).queue();
                });
                sendDM(member, String.format("You've been striked in how i met your discord for: `%s`", reason));

                logger.info("{}(ID:{}) was issued a strike for {}", member.getUser().getName(), member.getId(), reason);
                break;
            case 2:
                muteHandler.muteMember(member, mod, 6, guild, reason);
                sendDM(member, String.format("You've been muted in how i met your discord for 6 hours for: `%s`", reason));
                break;
            case 3:
                kickHandler.kickMember(member, mod, guild, reason);
                break;
            case 4:
                muteHandler.muteMember(member, mod, 12, guild, reason);
                sendDM(member, String.format("You've been muted in how i met your discord for 12 hours for: `%s`", reason));
                break;
            case 5:
                muteHandler.muteMember(member, mod, 24, guild, reason);
                sendDM(member, String.format("You've been muted in how i met your discord for 24 hours for: `%s`", reason));
                break;
            case 6:
                banHandler.banMember(member, mod, guild, reason);
                break;
        }
    }

    private void sendDM(Member member, String message) {
        member.getUser().openPrivateChannel().queue(privateChannel -> {
            privateChannel.sendMessage(message).queue();
        });
    }


}
