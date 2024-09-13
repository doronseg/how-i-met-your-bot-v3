package me.nerdoron.himyb.modules.bot;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.commands.currency.*;
import me.nerdoron.himyb.commands.currency.batches.DailyCommand;
import me.nerdoron.himyb.commands.currency.batches.MonthlyCommand;
import me.nerdoron.himyb.commands.currency.batches.WeeklyCommand;
import me.nerdoron.himyb.commands.fun.EightBallCommand;
import me.nerdoron.himyb.commands.fun.currency.CrimeCommand;
import me.nerdoron.himyb.commands.fun.currency.WorkCommand;
import me.nerdoron.himyb.commands.staff.ReviveCommand;
import me.nerdoron.himyb.commands.useful.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.slf4j.Logger;

import java.util.ArrayList;

public class BotCommandsHandler extends ListenerAdapter {
    private static final Logger logger = LoggingHandler.logger(BotCommandsHandler.class);
    public final ArrayList<SlashCommand> commands = new ArrayList<>();


    public BotCommandsHandler() {
        Global.COMMANDS_HANDLER = this;
        //useful
        commands.add(new PingCommand());
        commands.add(new UpTimeCommand());
        commands.add(new ContributeCommand());
        commands.add(new HelpCommand(this));
        commands.add(new BugReportCommand());

        //staff
        commands.add(new ReviveCommand());

        //fun
        commands.add(new EightBallCommand());
        commands.add(new WorkCommand());
        commands.add(new CrimeCommand());

        // currency
        commands.add(new BalanceCommand());
        commands.add(new DepositCommand());
        commands.add(new WithdrawCommand());
        commands.add(new TransferCommand());
        commands.add(new GiveCashCommand());
        commands.add(new LeaderboardCommand());
        commands.add(new DailyCommand());
        commands.add(new WeeklyCommand());
        commands.add(new MonthlyCommand());
        commands.add(new OddsCommand());

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
