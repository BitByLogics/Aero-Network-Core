package net.aeronetwork.core.cosmetic.test;

import net.aeronetwork.core.cosmetic.Cosmetic;
import net.aeronetwork.core.cosmetic.CosmeticType;
import net.aeronetwork.core.cosmetic.MultiSelectableCosmetic;
import net.aeronetwork.core.cosmetic.RunnableCosmetic;
import net.aeronetwork.core.util.FM;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GenericCosmetic2 implements Cosmetic, MultiSelectableCosmetic, RunnableCosmetic {

    private GenericCosmetic.CType type;
    private String id;
    private String name;

    public GenericCosmetic2(GenericCosmetic.CType type, String id, String name) {
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
        return "Some other cosmetic yay :D!";
    }

    @Override
    public long getCost() {
        return 1;
    }

    @Override
    public Enum<? extends CosmeticType> getType() {
        return this.type;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.CACTUS);
    }

    @Override
    public long getTickingPeriodTicks() {
        return 0;
    }

    @Override
    public void onSelect(Player player) {
        player.sendMessage(FM.mainFormat("Selected cosmetic"));
    }

    @Override
    public void onTick(Player player) {

    }

    @Override
    public void onUnselect(Player player) {

    }
}
