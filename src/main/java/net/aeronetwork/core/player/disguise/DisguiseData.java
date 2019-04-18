package net.aeronetwork.core.player.disguise;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.aeronetwork.core.player.rank.Rank;

@Getter
@NoArgsConstructor @AllArgsConstructor
public class DisguiseData {

    private String name;
    private String skin;
    private Rank rank;
}
