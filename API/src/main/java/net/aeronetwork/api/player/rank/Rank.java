package net.aeronetwork.api.player.rank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@Getter
public enum Rank {

    OWNER("Owner", RankType.HIGH_STAFF, ChatColor.DARK_RED, "§4§lOWNER §4", "", Arrays.asList("nte.owner", "aac.verbose")),
    DEVELOPER("Developer", RankType.DEPRECATED, ChatColor.DARK_GREEN, "§c§lDEV §c", "", Arrays.asList("nte.dev")),
    ADMIN("Admin", RankType.HIGH_STAFF, ChatColor.RED, "§c§lADMIN §c", "", Arrays.asList("nte.admin", "aac.verbose")),
    STAFF("Staff", RankType.STAFF, ChatColor.DARK_BLUE, "§9§lSTAFF §9", "", Arrays.asList("nte.staff")),
    JR_STAFF("Jr.Staff", RankType.STAFF, ChatColor.DARK_AQUA, "§9§lJR.STAFF §9", "", Arrays.asList("nte.jrstaff")),
    BUILDER("Builder", RankType.STAFF, ChatColor.YELLOW, "§e§lBUILDER §e", "", Arrays.asList("nte.builder")),
    YOUTUBE("YouTube", RankType.MEDIA, ChatColor.RED, "§c§lYOUTUBE §c", "", Arrays.asList("nte.youtube")),
    YT("YT", RankType.MEDIA, ChatColor.WHITE, "§f§lYT §f", "", Arrays.asList("nte.yt")),
    ZEUS("Zeus", RankType.DONATOR, ChatColor.GOLD, "§6§lZEUS §6", "", Arrays.asList("nte.zeus")),
    HOPPER("Hopper", RankType.DONATOR, ChatColor.DARK_PURPLE, "§5§lHOPPER §5", "", Arrays.asList("nte.hopper")),
    LONGSHOT("Longshot", RankType.DONATOR, ChatColor.DARK_PURPLE, "§5§lLONGSHOT §5", "", Arrays.asList("nte.longshot")),
    DONOR("Donor", RankType.DONATOR, ChatColor.AQUA, "§b§lDONOR §b", "", Arrays.asList("nte.donor")),
    STREAKER("Streaker", RankType.DONATOR, ChatColor.GREEN, "§a§lSTREAKER §a", "", Arrays.asList("nte.streaker")),
    DEFAULT("Default", RankType.NONE, ChatColor.GRAY, "§e", "", Arrays.asList("nte.default"));

    private String friendlyName;
    private RankType rankType;

    private ChatColor color;
    private String prefix;
    private String suffix;

    private List<String> permissions;

    public int getPriority() {
        return ordinal();
    }

    public static Rank getHighestRank(Rank... ranks) {
        if(ranks != null) {
            CompletableFuture<Rank> highestRank = new CompletableFuture<>();
            Arrays.stream(ranks).forEach(rank -> {
                if(highestRank.getNow(null) == null ||
                        rank.getPriority() < highestRank.getNow(null).getPriority()) {
                    highestRank.obtrudeValue(rank);
                }
            });
            return highestRank.getNow(null);
        }
        return null;
    }

    public static Rank match(String name) {
        return Arrays.stream(values())
                .filter(rank -> rank.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public enum RankType {
        DEPRECATED, NONE, DONATOR, MEDIA, STAFF, HIGH_STAFF
    }
}
