package me.nerdoron.himyb.modules.useful.tickets;

import me.nerdoron.himyb.Global;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class Panels {

    public static final MessageEmbed AdminStaff = new EmbedBuilder().setTitle("📇 Contact us")
            .addField("Staff Ticket", "Click the blue button below to contact the server's staff team.", false)
            .addField("Admin Ticket", "Click the red button below to contact the server's administration team\n" +
                    "This should be used for things that require Admin attention, like punishment appeals, staff reports, security issues, etc." +
                    "\n*Be advised, response times here will be longer, so please use a staff ticket if it's not important*", false)
            .setFooter(Global.footertext, Global.footerpfp)
            .setColor(Global.embedColor)
            .build();

    public static final MessageEmbed adminWelcome = new EmbedBuilder().setTitle("📇 Admin Ticket ").setDescription(
                    "Hello, the administration team will be with you as soon as they can. In the meantime. please let us know what you need.\nTo close this ticket, use the button below.")
            .setColor(Global.embedColor)
            .setFooter(Global.footertext, Global.footerpfp)
            .build();

    public static final MessageEmbed generalWelcome = new EmbedBuilder().setTitle("📇 Staff Ticket ").setDescription(
                    "Hello, the staff team will be with you as soon as they can. In the meantime, please let us know what you need.\nTo close this ticket, use the button below.")
            .setColor(Global.embedColor)
            .setFooter(Global.footertext, Global.footerpfp)
            .build();

}
