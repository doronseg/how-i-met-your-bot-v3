package me.nerdoron.himyb.modules.bot;

import java.util.Random;

public class Rng {
    public static int generateNumber(int min, int max) {
        Random r = new Random();
        int high = max + 1;
        return r.nextInt(high - min) + min;
    }
}
