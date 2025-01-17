package me.nerdoron.himyb.commands.useful;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

public class SuggestCommand extends SlashCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String suggestion = Objects.requireNonNull(event.getOption("suggestion")).getAsString();
        String type = Objects.requireNonNull(event.getOption("type")).getAsString();
        User user = event.getUser();
        Emoji checkEmoji = Emoji.fromUnicode("✅");
        Emoji xEmoji = Emoji.fromUnicode("❌");

        if (!(event.isFromGuild())) {
            event.deferReply().setEphemeral(true).setContent("This command can only be executed in the server.")
                    .queue();
            return;
        }

        MessageEmbed suggestionEmbed = new EmbedBuilder()
                .setAuthor(user.getAsTag() + "'s suggestion", null, user.getAvatarUrl())
                .setDescription(suggestion)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();

        Guild guild = event.getGuild();
        assert guild != null;
        switch (type) {
            case "server":
                TextChannel server = guild.getTextChannelById("857560927134285824");
                assert server != null;
                server.sendMessageEmbeds(suggestionEmbed).queue((message) -> {
                    message.addReaction(checkEmoji).queue();
                    message.addReaction(xEmoji).queue();
                });
                event.deferReply().setEphemeral(true)
                        .setContent("Suggestion sent! Remember that sending troll suggestions might result in a strike")
                        .queue();
                break;

            case "bot":
                TextChannel bot = guild.getTextChannelById("869853782949908481");
                assert bot != null;
                bot.sendMessageEmbeds(suggestionEmbed).queue((message) -> {
                    message.addReaction(checkEmoji).queue();
                    message.addReaction(xEmoji).queue();
                });
                event.deferReply().setEphemeral(true)
                        .setContent("Suggestion sent! Remember that sending troll suggestions might result in a strike")
                        .queue();
                break;
            default:
                event.deferReply().setEphemeral(true)
                        .setContent("Unknown type. Available types: `server`, `bot`").queue();
                break;
        }
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("suggest", "Send a suggestion")
                .addOption(OptionType.STRING, "type", "Select the type of suggestion", true, true)
                .addOption(OptionType.STRING, "suggestion", "Describe your suggestion", true);
    }

}
