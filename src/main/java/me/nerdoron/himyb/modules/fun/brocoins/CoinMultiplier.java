package me.nerdoron.himyb.modules.fun.brocoins;

import me.nerdoron.himyb.modules.bot.CooldownManager;
import net.dv8tion.jda.api.entities.Member;

import static me.nerdoron.himyb.Global.COOLDOWN_MANAGER;

public class CoinMultiplier {
    private final double multiplier;

    public CoinMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public static CoinMultiplier getMultiplier(Member member) {
        BroCoinsSQL coinBoost = new BroCoinsSQL();

        // Check if the user has an active boost
        if (coinBoost.hasCoinBoost(member)) {
            if (COOLDOWN_MANAGER.hasCooldown(CooldownManager.coinsCooldown(member, "noob"))) {
                return new CoinMultiplier(1.25);
            } else if (COOLDOWN_MANAGER.hasCooldown(CooldownManager.coinsCooldown(member, "pro"))) {
                return new CoinMultiplier(1.5);
            } else if (COOLDOWN_MANAGER.hasCooldown(CooldownManager.coinsCooldown(member, "gambler"))) {
                return new CoinMultiplier(2.5);
            }
        }

        // Return default multiplier if no active boost
        return new CoinMultiplier(1.0);
    }

    public int applyMultiplier(int amount) {
        return (int) Math.round(amount * multiplier);
    }
}
