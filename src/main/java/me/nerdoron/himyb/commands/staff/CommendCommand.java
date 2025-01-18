package me.nerdoron.himyb.commands.staff;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.fun.broofthemonth.BroOfTheMonthSQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

public class CommendCommand extends SlashCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        Member target = Objects.requireNonNull(event.getOption("member")).getAsMember();
        String reason = Objects.requireNonNull(event.getOption("reason")).getAsString();
        assert member != null;
        assert target != null;
        if (!member.hasPermission(Permission.KICK_MEMBERS)) {
            event.reply("I'm sorry, but only Moderators can execute this command").setEphemeral(true).queue();
            return;
        }
        if (member == target) {
            event.reply("You can not commend yourself!").setEphemeral(true).queue();
        }

        BroOfTheMonthSQL broOfTheMonthSQL = new BroOfTheMonthSQL();
        broOfTheMonthSQL.incrementCommendation(target.getId());

        TextChannel modlogs = Objects.requireNonNull(event.getGuild()).getTextChannelById("850454179319250994");
        assert modlogs != null;

        modlogs.sendMessageEmbeds(new EmbedBuilder()
                .setTitle("\uD83D\uDD4A️ User Commended")
                .setDescription(String.format("%s was commended by %s!", target.getAsMention(), member.getAsMention()))
                .addField("Reason", reason, false)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build()).queue();
        event.reply(String.format("Successfully commended %s!", target.getAsMention())).setEphemeral(true).queue();

    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("commend", "Commend a user (Affects Bro Of The Month Status)")
                .addOption(OptionType.MENTIONABLE, "member", "Who do you want to commend?", true)
                .addOption(OptionType.STRING, "reason", "Why would you like to commend them?", true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.KICK_MEMBERS));
    }
}
