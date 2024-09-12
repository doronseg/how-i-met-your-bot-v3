package me.nerdoron.himyb.commands.useful;

import me.nerdoron.himyb.modules.bot.BotCommandsHandler;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;

import static me.nerdoron.himyb.modules.useful.HelpHandler.mainMenu;

public class HelpCommand extends SlashCommand {
    private final BotCommandsHandler COMMANDS_HANDLER;

    public HelpCommand(BotCommandsHandler commandsHandler) {
        COMMANDS_HANDLER = commandsHandler;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String uid = event.getUser().getId();
        ArrayList<Button> buttons = new ArrayList<>();
        ArrayList<String> categoriesAdded = new ArrayList<>();
        buttons.add(Button.secondary("HELP:" + uid + ":main", "🔮 Main Menu"));

        for (SlashCommand command : COMMANDS_HANDLER.commands) {
            String cmdCategory = command.getCategory();
            if (!categoriesAdded.contains(cmdCategory)) {
                String detailedName = COMMANDS_HANDLER.getCategoryDetailedName(cmdCategory);
                if (detailedName != null) {
                    categoriesAdded.add(cmdCategory);
                    buttons.add(Button.secondary("HELP:" + uid + ":" + cmdCategory, detailedName));
                }
            }
        }

        event.replyEmbeds(mainMenu)
                .addActionRow(buttons)
                .queue();
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("help", "Displays the help menu.");
    }
}
