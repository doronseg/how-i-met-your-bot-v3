package me.nerdoron.himyb.modules.bot;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import me.nerdoron.himyb.Global;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;

public class ChangeStatus {

    public static void changeActivity(JDA jda) {
        String[] statuses = {
                "Whenever I’m sad, I stop being sad and be awesome instead.",
                "Hey bro, I don’t know what you’re eating cause I don’t have any eyes but it’s basically awesome",
                "A lie is just a great story that someone ruined with the truth.",
                "I’m no VIP; I’m not even an IP, I’m just a lonely little P sitting out here in the gutter.| /help",
                "I finally found the one, Marshall. Her name is Bacon.",
                "Your package has always been big enough.",
                "The only reason to wait a month for sex is if she’s 17 years, 11 months old.",
                "You have to let me dance my own battles!",
                "Oprah wasn’t built in a day.",
                "Death is all around us.",
                "I thought I saw Big Foot in the park, so I tackled him...",
                "ALL HAIL BEERCULES!!!!!",
                "Dude… where’s your suit? Just once, when I say “suit up,” I wish you’d put on a suit.",
                "Happy Slapsgiving!",
                "That’s love, bitch.",
                "I’ve been dreaming of that since I was five. Well, that and my own operational Death Star!",
                "I got a two-syllable ‘damn’ in this dress...",
                "NOBODY ASKED YOU PATRICE!",
                "The Bro Code has been around for centuries. Nay… whatever’s more than centuries.",
                "just like Ryan Gosling in The Notebook, huh?",
                "Nothing good happens after 2 a.m.",
                "It’s not cheating if you’re not the one who’s married",
                "Why is Ellen DeGeneres in our bedroom?",
                "You guys bangin’?",
                "Bang, bang, bangity bang!",
                "Just... okay?",
                "Just be cool lady, damn!",
                "No can doosville, babydoll",
                "Time will heal a broken heart, but NOT THAT BITCH'S WINDOW!!!!",
                "Lawyered.",
                "First of all my parents live in Ohio, I live in the moment",
                "DAMN IT TRUDY, WHAT ABOUT THE PINEAPPLE?",
                "Pulling. Them. Off.",
                "Tell people what?",
                "We should buy a bar!",
                "Thank you, Linus",
                "Come again for Big Fudge?",
                "Bowl.",
                "Oh my God, can you just be cool? Once. Please! Just once... Can you just once be cool? Once! Please!",
                "WHAT’S IN A GIN AND TONIC?!?",
                "Legendaddy!",
                "Ain't no thing but a chicken wing, mamacita!",
                "We are international businessmen!",
                "Wait for it...",
                "Ring bear",
                "YOU DUMPED A PORNSTAR?????????",
                "Is this your water?",
                "Bold to go for the car...",
                "Why is it called Puzzles?",
                "That's a good point, bear",
                "Haaaaaave you met Ted?",
                "Article 1 - Bros before ho's",
                "Article 87 - A Bro shall at all times say yes.",
                "Article 34 - Bros cannot make eye contact during a devil's three-way"

        };

        Random random = new Random();
        String status = statuses[random.nextInt(statuses.length)];
        jda.getPresence().setActivity(Activity.playing(status));
    }

    // change status timer every 15-30 minutes
    public static void activityTimer(JDA jda) {
        int delay = 0;
        int rng = Global.generateNumber(15, 30);
        int period = rng * Global.MIN_IN_MS;
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            public void run() {
                changeActivity(jda);
            }
        }, delay, period);
    }
}
