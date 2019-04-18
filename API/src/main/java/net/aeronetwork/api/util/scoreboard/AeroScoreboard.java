package net.aeronetwork.api.util.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

public class AeroScoreboard {

    private final int MAX_LINES = 16;
    private Scoreboard scoreboard;
    private Objective objective;
    private List<String> scores = new ArrayList<>(MAX_LINES);

    public AeroScoreboard(String displayName) {
        this.scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("aero", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(displayName);
    }

    public Scoreboard getBukkitScoreboard() {
        return scoreboard;
    }

    public Objective getObjective() {
        return objective;
    }

    public List<String> getScores() {
        return scores;
    }

    public void setDisplayName(String displayName) {
        objective.setDisplayName(displayName);
    }

    public AeroScoreboard addLine(String text) {
        if(scores.size() <= MAX_LINES) {
            text = (text.length() > 40 ? text.substring(0, 40) : text);
            for(String s : scores) {
                Score score = objective.getScore(s);
                score.setScore(score.getScore() + 1);
            }
            scores.add(text);
            objective.getScore(text).setScore(0);
        }
        return this;
    }

    public void setLine(int line, String text) {
        if(scores.get(line) != null) {
            text = (text.length() > 40 ? text.substring(0, 40) : text);
            if(!scores.get(line).equals(text)) {
                scoreboard.resetScores(scores.get(line));
                objective.getScore(text).setScore(scores.size() - line - 1);
                scores.set(line, text);
            }
        }
    }

    public void removeLine(int line) {
        for (int i = 0; i < scores.size(); i++) {
            if(i > line) {
                Score score = objective.getScore(scores.get(i));
                score.setScore(score.getScore() - 1);
            }
        }
        scoreboard.resetScores(scores.get(line));
        scores.remove(line);
    }

    public void setScoreboard(Player p) {
        p.setScoreboard(scoreboard);
    }
}
