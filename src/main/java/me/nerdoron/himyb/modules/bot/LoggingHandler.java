package me.nerdoron.himyb.modules.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggingHandler {
    public static String getTimeStamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);

    }

    public static String className(Class<?> clazz) {

        if (clazz.getName().contains("commands")) {
            String[] dots = clazz.getName().split("\\.");
            return getTimeStamp() + " COMMAND > " + dots[dots.length - 1];
        }
        if (clazz.getName().contains("modules.bot")) {
            String[] dots = clazz.getName().split("\\.");
            return getTimeStamp() + " CORE > " + dots[dots.length - 1];
        }
        if (clazz.getName().contains("modules.fun.brocoins")) {
            String[] dots = clazz.getName().split("\\.");
            return getTimeStamp() + " BROCOINS > " + dots[dots.length - 1];
        }
        if (clazz.getName().contains("modules.broshop")) {
            String[] dots = clazz.getName().split("\\.");
            return getTimeStamp() + " BROSHOP > " + dots[dots.length - 1];
        }
        if (clazz.getName().contains("modules.fun.blackjack")) {
            String[] dots = clazz.getName().split("\\.");
            return getTimeStamp() + " BLACKJACK > " + dots[dots.length - 1];
        }
        if (clazz.getName().contains("modules.fun.hilo")) {
            String[] dots = clazz.getName().split("\\.");
            return getTimeStamp() + " HILO > " + dots[dots.length - 1];
        }
        if (clazz.getName().contains("modules.fun.texasholdem")) {
            String[] dots = clazz.getName().split("\\.");
            return getTimeStamp() + " TEXAS HOLD'EM > " + dots[dots.length - 1];
        }
        if (clazz.getName().contains("Main")) {
            String[] dots = clazz.getName().split("\\.");
            return getTimeStamp() + " HIMYB > " + dots[dots.length - 1];
        }

        return clazz.getName();
    }

    // logger
    public static Logger logger(Class<?> clazz) {
        return LoggerFactory.getLogger(className(clazz));
    }
}
