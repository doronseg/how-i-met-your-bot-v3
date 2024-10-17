package me.nerdoron.himyb.commands.currency.broshop;

import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.broshop.ShopHelper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class InventoryCommand extends SlashCommand {
    private static final Logger logger = LoggerFactory.getLogger(InventoryCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        assert member != null;
        try {
            event.replyEmbeds(ShopHelper.inventoryEmbed(member)).queue();
        } catch (SQLException e) {
            logger.error("Error during buying item for user {}: {}", member.getId(), e.getMessage());
            event.replyEmbeds(ShopHelper.createErrorEmbed("An error occurred while processing your purchase.")).setEphemeral(true).queue();
        }
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("inventory", "Opens your inventory.");
    }
}