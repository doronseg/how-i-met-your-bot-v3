package me.nerdoron.himyb.modules.moderation.modlogs;

public enum ModLogType {
    STRIKE(1),
    PARDON(2),
    KICK(3),
    BAN(4),
    UNBAN(5),
    MUTE(6),
    UNMUTE(7),
    RAID_MODE(8);

    private final int id;

    ModLogType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
