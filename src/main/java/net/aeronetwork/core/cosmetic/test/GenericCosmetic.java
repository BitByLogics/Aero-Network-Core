package net.aeronetwork.core.cosmetic.test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.aeronetwork.core.cosmetic.*;
import net.aeronetwork.core.player.rank.Rank;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GenericCosmetic implements Cosmetic, RankCosmetic, NonPurchasableCosmetic, RunnableCosmetic {

    private CType type;
    private String id;
    private String name;

    public GenericCosmetic(CType type, String id, String name) {
        this.type = type;
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return "Cosmetic";
    }

    @Override
    public long getCost() {
        return 5000;
    }

    @Override
    public Enum<? extends CosmeticType> getType() {
        return this.type;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.IRON_INGOT);
    }

    @Override
    public Rank getMinRank() {
        return Rank.LONGSHOT;
    }

    @Override
    public boolean isDefaultRankCosmetic() {
        return true;
    }

    @Override
    public long getTickingPeriodTicks() {
        return 0;
    }

    @Override
    public void onSelect(Player player) {

    }

    @Override
    public void onTick(Player player) {
        player.getLocation().getWorld().spigot().playEffect(
                player.getLocation(),
                Effect.FIREWORKS_SPARK,
                0,
                0,
                0,
                0,
                0,
                0,
                5,
                24
        );
    }

    @Override
    public void onUnselect(Player player) {

    }

    @AllArgsConstructor
    @Getter
    public enum CType implements CosmeticType  {

        PARTICLES("Particles", new ItemStack(Material.NETHER_STAR));

        private String friendlyName;
        private ItemStack icon;
    }
}
