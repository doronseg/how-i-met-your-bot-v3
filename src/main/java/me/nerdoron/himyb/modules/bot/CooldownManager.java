package me.nerdoron.himyb.modules.bot;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class CooldownManager {
    private static final Connection con = Database.connect();

    public CooldownManager() {
    }

    public static String commandID(SlashCommandInteractionEvent event) {
        return "@" + event.getUser().getId() + event.getName() + "@";
    }

    public static String jailID(Member member) {
        return "@" + member.getId() + "jail@";
    }

    public static String expCooldown(Member member, String type) {
        return "@" + member.getId() + type + "@";
    }

    public static String coinsCooldown(Member member) {
        return "@" + member.getId() + "coins@";
    }

    private static OffsetDateTime parseTimestringToOffset(String timestamp) {
        java.time.format.DateTimeFormatter parser = java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        java.time.LocalDateTime dt = java.time.LocalDateTime.parse(timestamp, parser);
        ZonedDateTime zdt = ZonedDateTime.of(dt, java.time.ZoneId.of(ZoneId.systemDefault().getId()));
        return OffsetDateTime.from(zdt);
    }

    private static String parseOffsetToText(OffsetDateTime offset) {
        return offset.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public void addCooldown(String identifier, int timeInSeconds) {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime plus = now.plusSeconds(timeInSeconds);
        DB_addNewEntry(identifier, plus);
    }

    public void addCooldown(String identifier, String tag, int timeInSeconds) {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime plus = now.plusSeconds(timeInSeconds);
        DB_addNewEntry(identifier + " #" + tag, plus);
    }

    public void deleteCooldown(String identifier) {
        DB_removeEntry(identifier);
    }

    public boolean hasCooldown(String identifier) {
        OffsetDateTime now = OffsetDateTime.now();
        Map<String, OffsetDateTime> cooldown = DB_findIdentifier(identifier);

        if (cooldown == null) {
            return false;
        } else {
            String ID = get1stKey(cooldown);
            OffsetDateTime Coffset = cooldown.get(ID);
            if (Coffset.isAfter(now)) {
                return true;
            } else {
                DB_removeEntry(identifier);
                return false;
            }
        }
    }

    public OffsetDateTime getCooldownEnd(String identifier) {
        Map<String, OffsetDateTime> cooldowns = DB_findIdentifier(identifier);
        if (cooldowns != null && !cooldowns.isEmpty()) {
            return cooldowns.values().iterator().next();
        }
        return OffsetDateTime.now();
    }

    public boolean hasTag(String identifier, String tag) {
        Map<String, OffsetDateTime> cooldown = DB_findIdentifier(identifier);
        if (cooldown == null) {
            return false;
        } else {
            return Objects.requireNonNull(get1stKey(cooldown)).contains("#" + tag);
        }
    }

    public String parseCooldown(String identifier) {
        Map<String, OffsetDateTime> cooldown = DB_findIdentifier(identifier);
        if (cooldown == null) {
            return "";
        } else {
            return parseOffsetDateTimeHumanText(cooldown.get(get1stKey(cooldown)));
        }
    }

    private String parseOffsetDateTimeHumanText(OffsetDateTime timeCreated) {
        OffsetDateTime now = OffsetDateTime.now();

        long sec = ChronoUnit.SECONDS.between(timeCreated, now) % 60;
        long min = ChronoUnit.MINUTES.between(timeCreated, now) % 60;
        long hur = ChronoUnit.HOURS.between(timeCreated, now) % 24;
        long day = ChronoUnit.DAYS.between(timeCreated, now) % 31;
        long mth = ChronoUnit.MONTHS.between(timeCreated, now) % 12;
        long yhr = ChronoUnit.YEARS.between(timeCreated, now);

        String send = "";

        if (yhr != 0) {
            send += " " + Math.abs(yhr) + " year" + (yhr > 1 ? "" : "s");
        }
        if (mth != 0) {
            send += " " + Math.abs(mth) + " month" + (mth > 1 ? "" : "s");
        }
        if (day != 0) {
            send += " " + Math.abs(day) + " day" + (day > 1 ? "" : "s");
        }
        if (hur != 0) {
            send += " " + Math.abs(hur) + " hour" + (hur > 1 ? "" : "s");
        }
        if (min != 0) {
            send += " " + Math.abs(min) + " minute" + (min > 1 ? "" : "s");
        }
        if (sec != 0) {
            send += " " + Math.abs(sec) + " second" + (sec > 1 ? "" : "s");
        }
        return send.trim();
    }

    private String get1stKey(Map<String, OffsetDateTime> map) {
        for (String key : map.keySet()) {
            return key;
        }
        return null;
    }

    private Map<String, OffsetDateTime> DB_findIdentifier(String identifier) {
        try {
            String SQL = "SELECT * FROM cooldowns WHERE uid LIKE \"" + identifier + "%\"";
            assert con != null;
            PreparedStatement ps = con.prepareStatement(SQL);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, OffsetDateTime> r = new HashMap<>();
                String dbID = rs.getString(1);
                String dbOffsetText = rs.getString(2);
                r.put(dbID, parseTimestringToOffset(dbOffsetText));
                ps.close();
                return r;
                // Found
            } else {
                ps.close();
                // Not found;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void DB_addNewEntry(String identifier, OffsetDateTime cooldownTime) {
        if (DB_findIdentifier(identifier) == null) {
            try {
                String statement = "INSERT INTO cooldowns (uid, cooldown) values(?,?)";
                assert con != null;
                PreparedStatement ps = con.prepareStatement(statement);
                ps.setString(1, identifier);
                ps.setString(2, parseOffsetToText(cooldownTime));
                ps.execute();
                ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void DB_removeEntry(String identifier) {
        Map<String, OffsetDateTime> cooldown = DB_findIdentifier(identifier);
        if (cooldown != null) {
            try {
                String statement = "DELETE FROM cooldowns WHERE uid = ?";
                assert con != null;
                PreparedStatement ps = con.prepareStatement(statement);
                ps.setString(1, get1stKey(cooldown));
                ps.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
