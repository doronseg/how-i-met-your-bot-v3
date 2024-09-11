package me.nerdoron.himyb.modules.bot;

import net.dv8tion.jda.api.JDA;

public class RegisterEvents {
    public static void registerEvents(JDA jda) {
        // commands
        BotCommandsHandler commandsHandler = new BotCommandsHandler();
        commandsHandler.updateCommandsOnDiscord(jda);
        jda.addEventListener(commandsHandler);

        // tickets

        // events
        jda.addEventListener(new FriendsCringe());
        jda.addEventListener(new Sweden());
    }

}
