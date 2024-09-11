package me.nerdoron.himyb.modules.bot;

import me.nerdoron.himyb.Global;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class SlashCommand {
    private static final Logger logger = Global.logger(SlashCommand.class);

    public void executeGlobal(SlashCommandInteractionEvent event) {
        try {
            execute(event);
        } catch (Exception ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
        }
    }

    public abstract void execute(SlashCommandInteractionEvent event);

    public abstract SlashCommandData getSlash();

    public String getCategory() {
        String name = this.getClass().getPackage().getName();
        return name.split("\\.")[4];
    }

}
