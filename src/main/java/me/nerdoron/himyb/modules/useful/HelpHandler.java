package me.nerdoron.himyb.modules.useful;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

public class HelpHandler extends ListenerAdapter {

    // help embed
    public static final MessageEmbed mainMenu = new EmbedBuilder()
            .setTitle("🔮 Help Menu")
            .setDescription(
                    "Hello! I'm how i met your bot. You can use the buttons below to view all of my functions, but for now, here is some information about me.")
            .addField("Bot Version", String.format("[%s](%s)", Global.botVersion, Global.botGithubLink), true)
            .addField("Library",
                    String.format("[%s](%s)", Global.jdaVersion, Global.jdaGithubLink),
                    true)
            .addField("Prefix", "`/`", true)
            .setColor(Global.embedColor)
            .setFooter(Global.footertext, Global.footerpfp)
            .build();

    // get all commands under chosen category
    @NotNull
    private static String getCategory(String buttonCategory) {
        StringBuilder embedDescription = new StringBuilder("A list of all commands under the `" + buttonCategory + "` category\n\n");
        for (SlashCommand command : Global.COMMANDS_HANDLER.commands) {
            if (!command.getCategory().equals(buttonCategory)) continue;

            StringBuilder options = new StringBuilder(" ");
            for (OptionData option : command.getSlash().getOptions()) {
                options.append("[").append(option.getName()).append("] ");
            }

            String commandName = command.getSlash().getName() + options;
            commandName = commandName.trim();

            embedDescription.append("`/").append(commandName).append("` - ");
            embedDescription.append(command.getSlash().getDescription()).append("\n");
        }
        return embedDescription.toString();
    }

    // handle button interaction
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();
        if (!buttonId.contains("HELP:")) return;
        String[] buttonSplit = buttonId.split(":");
        String userId = buttonSplit[1];
        String buttonCategory = buttonSplit[2];
        Message message = event.getMessage();

        if (!userId.equals(event.getUser().getId())) {
            event.deferReply().setEphemeral(true).setContent("You can not interact with this menu!").queue();
            return;
        }

        if (!message.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) return;

        event.deferEdit().queue();

        if (buttonCategory.equals("main")) {
            message.editMessageEmbeds(mainMenu).queue();
            return;
        }

        String embedDescription = getCategory(buttonCategory);
        EmbedBuilder emb = new EmbedBuilder();
        emb.setTitle(Global.COMMANDS_HANDLER.getCategoryDetailedName(buttonCategory));
        emb.setDescription(embedDescription);
        emb.setColor(Global.embedColor);
        emb.setFooter(Global.footertext, Global.footerpfp);
        message.editMessageEmbeds(emb.build()).queue();
    }


}
