package me.nerdoron.himyb.modules.bot;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.commands.fun.ReviveCommand;
import me.nerdoron.himyb.commands.useful.ContributeCommand;
import me.nerdoron.himyb.commands.useful.HelpCommand;
import me.nerdoron.himyb.commands.useful.PingCommand;
import me.nerdoron.himyb.commands.useful.UpTimeCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.slf4j.Logger;

import java.util.ArrayList;

public class BotCommandsHandler extends ListenerAdapter {
    private static final Logger logger = Global.logger(BotCommandsHandler.class);
    public ArrayList<SlashCommand> commands = new ArrayList<>();


    public BotCommandsHandler() {
        Global.COMMANDS_HANDLER = this;
        commands.add(new PingCommand());
        commands.add(new UpTimeCommand());
        commands.add(new ReviveCommand());
        commands.add(new ContributeCommand());
        commands.add(new HelpCommand(this));
    }

    public void updateCommandsOnDiscord(JDA jda) {
        ArrayList<CommandData> slashes = new ArrayList<>();
        for (SlashCommand command : commands) {
            slashes.add(command.getSlash());
            logger.info("Registered /{} - {}", command.getSlash().getName(), command.getSlash().getDescription());
        }
        jda.updateCommands().addCommands(slashes).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String commandName = event.getName();
        for (SlashCommand command : commands) {
            if (command.getSlash().getName().equals(commandName)) {
                command.execute(event);
                break;
            }
        }
    }

    public String getCategoryDetailedName(String category) {
        switch (category) {
            case "useful":
                return "🛠️ Useful Commands";
            case "fun":
                return "🦩 Fun Commands";
            case "currency":
                return "🪙 Currency Commands";
            default:
                return null;
        }
    }

}
