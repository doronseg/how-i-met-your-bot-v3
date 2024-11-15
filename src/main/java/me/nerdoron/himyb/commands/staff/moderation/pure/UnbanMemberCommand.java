package me.nerdoron.himyb.commands.staff.moderation.pure;

import me.nerdoron.himyb.modules.bot.LoggingHandler;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.moderation.punishments.BanHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class UnbanMemberCommand extends SlashCommand {
    private static final Logger logger = LoggingHandler.logger(UnbanMemberCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        assert member != null;
        if (!member.hasPermission(Permission.BAN_MEMBERS)) return;

        String targetId = Objects.requireNonNull(event.getOption("userid")).getAsString();
        String reason = Objects.requireNonNull(event.getOption("reason")).getAsString();

        Guild guild = event.getGuild();
        assert guild != null;
        event.getJDA().retrieveUserById(targetId).queue(user -> {
            BanHandler banHandler = new BanHandler();
            isUserBanned(guild, targetId).thenAccept(isBanned -> {
                if (isBanned) {
                    banHandler.UnbanMember(user, member, guild, reason).thenAccept(success -> {
                        if (success) {
                            event.reply(String.format("Successfully unbanned %s.", user.getName()))
                                    .setEphemeral(true)
                                    .queue();
                        } else {
                            event.reply("Error. Could not unban the user.")
                                    .setEphemeral(true)
                                    .queue();
                        }
                    }).exceptionally(ex -> {
                        event.reply("An unexpected error occurred while unbanning the user.")
                                .setEphemeral(true)
                                .queue();
                        return null;
                    });
                } else {
                    event.reply("This user isn't banned!").setEphemeral(true).queue();
                }
            });
        }, error -> {
            if (error instanceof ErrorResponseException) {
                event.reply("Invalid user ID or user does not exist.")
                        .setEphemeral(true)
                        .queue();
                logger.error("Failed to retrieve user by ID {}: {}", targetId, error.getMessage());
            }
        });

    }

    private CompletableFuture<Boolean> isUserBanned(Guild guild, String userId) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();

        guild.retrieveBanList().queue(bans -> {
            boolean isBanned = bans.stream().anyMatch(ban -> ban.getUser().getId().equals(userId));
            result.complete(isBanned);
        }, error -> {
            if (error instanceof ErrorResponseException) {
                logger.error("Error retrieving ban list: {}", error.getMessage());
            }
            result.complete(false);
        });
        return result;
    }

    @Override
    public SlashCommandData getSlash() {
        SlashCommandData unban = Commands.slash("unban", "Unban a user.");
        unban.addOption(OptionType.STRING, "userid", "Provide the id of the member you'd like to unban.", true);
        unban.addOption(OptionType.STRING, "reason", "What's the reason for the unban?.", true);
        unban.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS));
        return unban;
    }
}
