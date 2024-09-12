package me.nerdoron.himyb.modules.fun.brocoins;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.Rng;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class CurrencyHelper {
    public final static MessageEmbed scammedEmbed = new EmbedBuilder()
            .setTitle("\uD83D\uDE32  Scammed!")
            .setDescription(String.format("You've worked %s, but your employer scammed you and didn't pay you any money!", getJob()))
            .setColor(Global.embedColor)
            .setFooter(Global.footertext, Global.footerpfp)
            .build();

    public static MessageEmbed workEmbed(int earning) {
        return new EmbedBuilder()
                .setTitle("\uD83D\uDCBC  An honest day's work")
                .setDescription(String.format("You've worked %s and earned %d %s.", getJob(), earning, Global.broCoin.getAsMention()))
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    private static String getJob() {
        String[] jobs = new String[]{
                "as a Software Engineer", "as a Developer", "as a Story teller", "as a Dentist", "as a Veterinarian",
                "as a Teacher", "as a Financial Advisor", "as a Lawyer", "as a Accountant", "as a Architect",
                "as a P.L.E.A.S.E", "as an Actor", "as a Police Officer", "as a Video Editor", "as a Doctor",
                "as an Astronaut", "as a YouTuber", "as a Discord Admin", "as a Delivery Driver", "as a Cashier",
                "as a Burger Flipper", "as a Cameraman", "as a waiter", "as a Paramedic", "as a Dancer",
                "as a Electrician", "as a Chef", "as a Farmer", "as a Locksmith", "as a Mechanic", "as a Baker",
                "as a Butcher", "as a Pilot", "as a Sea-Captain", "as a Painter", "as a Musician", "as a Miner",
                "as a Bellhop", "as a Bookkeeper", "as a Car-wash Cashier", "as a Kindergarten Teacher", "as a News Reporter",
                "as a Zookeeper", "as a Garbageman", "as a Babysitter", "for GNB", "for the NRDC", "as a Stripper",
                "as a Hairstylist", "as a High Fiving Coach", "as a Funeral Clown", "as an Art Advisor", "as a Wrestler",
                "as a Scientist", "as a Pornstar", "as an Uber Driver", "as an EMT", "as a Gamer", "as a Clerk",
                "as a Jeweler", "as a Rabbi", "as a Priest", "as a Soldier", "as a Model", "as a Nanny",
                "as a Board Game Designer", "as a Parking Enforcer", "as a Photographer", "as a Dispatcher",
                "as a Business Advisor", "as a Therapist", "as a Truck Driver", "as a Zoologist", "as an HOA Board Member",
                "for McDonald's", "as a Lobbyist", "as a Prime Minister", "as the U.S President", "as a Professional Gambler"
        };

        return jobs[Rng.generateNumber(0, jobs.length - 1)];
    }

    private static String getCrime() {
        String[] crimes = new String[]{
                "rob the Diamond Casino", "steal a car", "steal a bicycle", "rob a store", "sell weed", "sell meth",
                "pickpocket someone", "help Kira's Gambling addiction", "help illegal immigrants cross the border",
                "pull a hit on a bounty", "break into someone's house", "hack in Hypixel",
                "pirate how i met your mother", "break the Geneva Convention", "rob a bank",
                "switch a fire alarm on because you couldn't help the idea of not meeting your soul-mate",
                "steal Ted's Christmas decorations", "steal a Blue French Horn", "take someone else's cab",
                "leave a suitcase at the airport", "steal a bottle of whiskey for your bro", "move to San Francisco",
                "talk to the North Koreans", "talk to the South Koreans", "TP a Laser Tag place",
                "hire a \"paralegal\"", "commit Tax Evasion", "drive under the influence", "commit arson",
                "steal a guitar pick", "play 0-3-5 in the guitar center", "buy a fake ID", "use a fake ID",
                "count cards in the casino", "play copyrighted music", "become a teenage pop star",
                "pee on a church", "seduce a police officer", "tackle Russell Brand",
                "tried to bring Oscar out of retirement", "emigrate to Canada",
                ""
        };

        return crimes[Rng.generateNumber(0, crimes.length - 1)];
    }
}


