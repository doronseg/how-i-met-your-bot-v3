package me.nerdoron.himyb.modules.bot;

import me.nerdoron.himyb.modules.fun.autoresponses.FriendsCringe;
import me.nerdoron.himyb.modules.fun.autoresponses.Sweden;
import me.nerdoron.himyb.modules.fun.counting.CountingChannelHandler;
import me.nerdoron.himyb.modules.fun.counting.CountingEditing;
import me.nerdoron.himyb.modules.fun.server.LeaveJoin;
import me.nerdoron.himyb.modules.useful.HelpHandler;
import me.nerdoron.himyb.modules.useful.tickets.TicketButtonHandler;
import me.nerdoron.himyb.modules.useful.tickets.TicketFileMonitor;
import net.dv8tion.jda.api.JDA;

public class RegisterEvents {
    public static void registerEvents(JDA jda) {
        // commands
        BotCommandsHandler commandsHandler = new BotCommandsHandler();
        commandsHandler.updateCommandsOnDiscord(jda);
        jda.addEventListener(commandsHandler);

        // tickets
        jda.addEventListener(new TicketButtonHandler());
        jda.addEventListener(new TicketFileMonitor());

        //fun
        jda.addEventListener(new FriendsCringe());
        jda.addEventListener(new Sweden());
        jda.addEventListener(new LeaveJoin());

        // useful
        jda.addEventListener(new HelpHandler());

        // counting
        jda.addEventListener(new CountingChannelHandler());
        jda.addEventListener(new CountingEditing());
    }

}
