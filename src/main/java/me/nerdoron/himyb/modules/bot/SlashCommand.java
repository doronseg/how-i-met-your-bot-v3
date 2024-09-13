package me.nerdoron.himyb.modules.bot;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;

public abstract class SlashCommand {
    private static final Logger logger = LoggingHandler.logger(SlashCommand.class);

    public abstract void execute(SlashCommandInteractionEvent event);

    public abstract SlashCommandData getSlash();

    public String getCategory() {
        String name = this.getClass().getPackage().getName();
        return name.split("\\.")[4];
    }

}
