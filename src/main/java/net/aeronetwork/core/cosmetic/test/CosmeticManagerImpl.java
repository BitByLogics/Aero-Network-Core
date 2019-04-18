package net.aeronetwork.core.cosmetic.test;

import com.google.common.collect.Maps;
import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.cosmetic.Cosmetic;
import net.aeronetwork.core.cosmetic.CosmeticManager;

import java.util.Map;
import java.util.UUID;

public class CosmeticManagerImpl extends CosmeticManager {

    private Map<UUID, Map<Cosmetic, Boolean>> cosmetics;

    public CosmeticManagerImpl() {
        this.cosmetics = Maps.newConcurrentMap();

        for(GenericCosmetic.CType type : GenericCosmetic.CType.values()) {
            addCosmetic(new GenericCosmetic(type, type.name() + "_IMPL", "Cosmetic"));
            addCosmetic(new GenericCosmetic2(type, type.name() + "_IMPL2", "Twinkle Toes"));
        }
    }

    @Override
    public Map<Cosmetic, Boolean> getCosmetics(UUID uuid) {
        Map<Cosmetic, Boolean> cosmetics = this.cosmetics.getOrDefault(uuid, null);

        if(cosmetics == null) {
            cosmetics = Maps.newConcurrentMap();
            this.cosmetics.put(uuid, cosmetics);
        }

        return cosmetics;
    }

    @Override
    public void onUpdate(UUID uuid, Map<String, Boolean> cosmetics) {
        // nothing
    }

    @Override
    public boolean onPurchase(UUID uuid, Cosmetic cosmetic) {
        return isPurchaseAllowed(AeroCore.PLAYER_MANAGER.getPlayer(uuid), cosmetic);
    }

    @Override
    public boolean onSelect(UUID uuid, Cosmetic cosmetic) {
        return isSelectAllowed(AeroCore.PLAYER_MANAGER.getPlayer(uuid), cosmetic);
    }

    @Override
    public void onUnselect(UUID uuid, Cosmetic cosmetic) {

    }
}
