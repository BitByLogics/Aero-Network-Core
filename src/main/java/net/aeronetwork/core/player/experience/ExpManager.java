package net.aeronetwork.core.player.experience;

import net.aeronetwork.core.player.AeroPlayer;

public class ExpManager {

    public ExpManager() {
    }

    public long getExperienceForLevel(long level) {
        return (long) Math.abs(50 * Math.pow(level, 2));
    }

    public void addExperience(AeroPlayer player, long deltaExp) {
        player.updateExperience(player.getExperience() + deltaExp);
    }

    public void removeExperience(AeroPlayer player, long deltaExp) {
        player.updateExperience(player.getExperience() - deltaExp);
    }

    public void addLevel(AeroPlayer player, long deltaLevel) {
        long level = calculateLevel(player.getExperience());
        long exp = 0;

        for(long i = level + 1; i < level + deltaLevel; i++) {
            exp += getExperienceForLevel(i);
        }

        addExperience(player, exp);
    }

    public void removeLevel(AeroPlayer player, long deltaLevel) {
        long level = calculateLevel(player.getExperience());
        long exp = 0;

        for(long i = level; i > level - deltaLevel; i--) {
            exp += getExperienceForLevel(i);
        }

        removeExperience(player, exp);
    }

    public long calculateLevel(long exp) {
        long level = 1;

        while(exp - getExperienceForLevel(level) >= 0) {
            level++;
        }

        return level;
    }
}
