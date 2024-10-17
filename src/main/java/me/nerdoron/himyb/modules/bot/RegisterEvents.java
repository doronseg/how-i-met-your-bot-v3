package me.nerdoron.himyb.modules.bot;

import me.nerdoron.himyb.modules.broshop.BuyAutocomplete;
import me.nerdoron.himyb.modules.broshop.ShopButtons;
import me.nerdoron.himyb.modules.fun.autoresponses.Canada;
import me.nerdoron.himyb.modules.fun.autoresponses.FriendsCringe;
import me.nerdoron.himyb.modules.fun.autoresponses.Major;
import me.nerdoron.himyb.modules.fun.autoresponses.Sweden;
import me.nerdoron.himyb.modules.fun.counting.CountingChannelHandler;
import me.nerdoron.himyb.modules.fun.counting.CountingEditing;
import me.nerdoron.himyb.modules.fun.server.LeaveJoin;
import me.nerdoron.himyb.modules.useful.HelpHandler;
import me.nerdoron.himyb.modules.useful.applications.EventAutoComplete;
import me.nerdoron.himyb.modules.useful.applications.EventManagerApplicationHandler;
import me.nerdoron.himyb.modules.useful.birthdays.BirthdayFunction;
import me.nerdoron.himyb.modules.useful.birthdays.BirthdayModalHandler;
import me.nerdoron.himyb.modules.useful.selfpromo.SelfPromoAutoMessage;
import me.nerdoron.himyb.modules.useful.selfpromo.SelfPromoModApproval;
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
        jda.addEventListener(new Major());
        jda.addEventListener(new Canada());
        jda.addEventListener(new LeaveJoin());


        //birthdays
        jda.addEventListener(new BirthdayModalHandler());
        jda.addEventListener(new BirthdayFunction());

        //shop
        jda.addEventListener(new ShopButtons());
        jda.addEventListener(new BuyAutocomplete());

        // useful
        jda.addEventListener(new HelpHandler());
        jda.addEventListener(new EventAutoComplete());
        jda.addEventListener(new EventManagerApplicationHandler());
        jda.addEventListener(new SelfPromoAutoMessage());
        jda.addEventListener(new SelfPromoModApproval());

        // counting
        jda.addEventListener(new CountingChannelHandler());
        jda.addEventListener(new CountingEditing());
    }

}
