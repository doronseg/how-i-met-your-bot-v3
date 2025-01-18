package me.nerdoron.himyb.commands.staff;

import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.fun.broofthemonth.BroOfTheMonthSQL;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

import static me.nerdoron.himyb.Global.TIMEOUT_DURATION;

public class ResetCommand extends SlashCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        assert member != null;
        if (!member.hasPermission(Permission.ADMINISTRATOR)) return;
        event.reply("Are you sure you want to reset stats? Reply `yes/no`. This action will time-out in 5 minutes if there's no response").queue();
        event.getJDA().listenOnce(MessageReceivedEvent.class)
                .filter(messageEvent -> messageEvent.getChannel().equals(event.getChannel()))
                .filter(messageEvent -> messageEvent.getAuthor().equals(member))
                .filter(messageEvent -> Objects.requireNonNull(messageEvent.getMember()).hasPermission(Permission.ADMINISTRATOR))
                .timeout(TIMEOUT_DURATION, () -> {
                    event.getHook().editOriginal("Time out. Nothing was reset.").queue();
                    return;
                }).subscribe(messageEvent -> {
                    if (messageEvent.getMessage().getContentRaw().equalsIgnoreCase("yes")) {
                        BroOfTheMonthSQL broOfTheMonthSQL = new BroOfTheMonthSQL();
                        broOfTheMonthSQL.resetStats();
                        event.getHook().editOriginal("Successfully reset all stats").queue();
                    } else if (messageEvent.getMessage().getContentRaw().equalsIgnoreCase("no")) {
                        event.getHook().editOriginal("Action cancelled.").queue();
                    }
                });
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("resetbotm", "Reset bro of the month stats")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR));
    }
}
