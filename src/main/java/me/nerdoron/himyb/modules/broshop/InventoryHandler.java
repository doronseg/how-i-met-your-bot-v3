package me.nerdoron.himyb.modules.broshop;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.Database;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static me.nerdoron.himyb.Global.BROCOINS_SQL;

public class InventoryHandler {

    private static final Logger logger = LoggerFactory.getLogger(InventoryHandler.class);
    private static final Connection con = Database.connect();

    public void buyItem(SlashCommandInteractionEvent event, Member member, ShopItem shopItem) {
        String type = shopItem.getType();
        String name = shopItem.getName();
        int price = shopItem.getPrice();

        if (BROCOINS_SQL.getBroCash(member) < price) {
            event.reply("You don't have enough cash to buy this item!").setEphemeral(true).queue();
            return;
        }

        try {
            BROCOINS_SQL.updateCash(member, -price);

            // roles
            if (type.startsWith("R")) {
                generateLog(event, member, shopItem).thenAccept(idTrim ->
                        event.reply(String.format("Bought `%s` for %d %s. Please create an admin ticket to claim it.\nMention the purchase ID: `%s`", name, price, Global.broCoin.getAsMention(), idTrim))
                                .setEphemeral(true).queue()
                );
                //other
            } else {
                updateInventory(member, type);
                int noOfItem = getItemAmount(member, type);
                if (noOfItem == 0) noOfItem = 1;
                int finalNoOfItem = noOfItem;
                generateLog(event, member, shopItem).thenAccept(idTrim ->
                        event.reply(String.format("Bought `%s` for %d %s. Now you have %d.\nPurchase ID: `%s`", name, price, Global.broCoin.getAsMention(), finalNoOfItem, idTrim))
                                .setEphemeral(true).queue()
                );
            }
            logger.info("{} (ID:{}) bought {} for {} coins.", member.getUser().getName(), member.getId(), type, price);
        } catch (SQLException e) {
            logger.error("Error during buying item for user {}: {}", member.getId(), e.getMessage());
            event.replyEmbeds(ShopHelper.createErrorEmbed("An error occurred while processing your purchase.")).setEphemeral(true).queue();
        }
    }

    private void updateInventory(Member member, String type) throws SQLException {
        String SQL;
        if (getItemAmount(member, type) == 0) {
            SQL = String.format(
                    "INSERT INTO inventory (uid, %s) VALUES (?, 1) " +
                            "ON CONFLICT(uid) DO UPDATE SET %s = 1", type, type);
        } else {
            SQL = String.format(
                    "INSERT INTO inventory (uid, %s) VALUES (?, 1) " +
                            "ON CONFLICT(uid) DO UPDATE SET %s = %s + 1", type, type, type);
        }

        assert con != null;
        try (PreparedStatement ps = con.prepareStatement(SQL)) {
            ps.setString(1, member.getId());
            ps.execute();
        }
    }

    public int getItemAmount(Member member, String itemType) throws SQLException {
        String SQL = String.format("SELECT %s FROM inventory WHERE uid=?", itemType);

        assert con != null;
        try (PreparedStatement ps = con.prepareStatement(SQL)) {
            ps.setString(1, member.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(itemType);
                }
            }
        }
        return 0;
    }

    private CompletableFuture<String> generateLog(SlashCommandInteractionEvent event, Member member, ShopItem item) {
        CompletableFuture<String> future = new CompletableFuture<>();

        TextChannel logChannel = Objects.requireNonNull(event.getGuild()).getTextChannelById(1296190541574311957L);
        assert logChannel != null;

        logChannel.sendMessage("Loading...").queue(message -> {
            message.editMessage("\u200E").queue();
            String id = message.getId();
            String idTrim = id.substring(id.length() - 4);
            message.editMessageEmbeds(ShopHelper.purchaseLog(member, item, idTrim)).queue();
            future.complete(idTrim);
        });

        return future;
    }
}
