package net.aeronetwork.core.config;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.aeronetwork.core.config.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Getter
public class AeroConfig extends YamlConfiguration {

    private JavaPlugin plugin;
    private File file;

    private boolean colorCodeSupported = true;

    public AeroConfig(JavaPlugin plugin, String folder, String name) {
        this.plugin = plugin;
        file = new File(folder + File.separator + name);

        ConfigUtil.saveDefaultConfig(plugin, name + ".yml", false); // Save the default instance.
    }

    public void reload() {
        ConfigUtil.reloadConfig(plugin, this, file);
    }

    public void save() {
        try {
            save(file);
        } catch (IOException e) {
            System.out.println("[Aero Core] [ERROR HANDLER]: Couldn't save config '" + file.getName() + "'.");
            e.printStackTrace();
        }
    }

    public boolean hasKeys(String section, String... keys) {
        Set<String> validKeys = getConfigurationSection(section).getKeys(true);
        return Arrays.asList(keys).stream().allMatch(key -> validKeys.contains(key));
    }

    @Override
    public boolean isItemStack(String path) {
        return hasKeys(path, "material", "amount", "data");
    }

    public boolean isLocation(String path) {
        return hasKeys(path, "world", "x", "y", "z");
    }

    public void setLocation(String path, Location location) {
        set(path + ".world", location.getWorld().getName());
        set(path + ".x", location.getX() + "");
        set(path + ".y", location.getY() + "");
        set(path + ".z", location.getZ() + "");
        set(path + ".yaw", location.getYaw() + "");
        set(path + ".pitch", location.getPitch() + "");
    }

    public Location getLocation(String path) {
        if(isLocation(path)) {
            Location location = new Location(Bukkit.getWorld(getString(path + ".world")), Double.valueOf(getString(path + ".x")), Double.valueOf(getString(path + ".y")), Double.valueOf(getString(path + ".z")));

            if(hasKeys(path, "yaw")) {
                location.setYaw(Float.valueOf(getString(path + ".yaw")));
            }

            if(hasKeys(path, "pitch")) {
                location.setPitch(Float.valueOf(getString(path + ".pitch")));
            }

            return location;
        }
        return null;
    }

    public void setItemStack(String path, ItemStack stack) {
        set(path + ".material", stack.getType().name());
        set(path + ".amount", stack.getAmount());
        set(path + ".data", stack.getData().getData());

        if(stack.hasItemMeta()) {
            ItemMeta meta = stack.getItemMeta();

            if(meta.hasDisplayName()) {
                set(path + ".name", meta.getDisplayName().replace("§", "&"));
            }

            if(meta.hasLore()) {
                List<String> formattedLore = Lists.newArrayList();
                meta.getLore().forEach(lore -> formattedLore.add(lore.replace("§", "&")));
                set(path + ".lore", formattedLore);
            }

            if(meta.hasEnchants()) {
                List<String> enchants = Lists.newArrayList();
                meta.getEnchants().keySet().forEach(enchantment -> enchants.add(enchantment.getName() + ":" + meta.getEnchantLevel(enchantment) + ":" + true));
                set(path + ".enchants", enchants);
            }

            if(meta.getItemFlags().size() > 0) {
                List<String> flags = Lists.newArrayList();
                meta.getItemFlags().forEach(flag -> flags.add(flag.name()));
                set(path + ".flags", flags);
            }

            if(meta.spigot().isUnbreakable()) {
                set(path + ".unbreakable", true);
            }
        }
    }

    @Override
    public ItemStack getItemStack(String path) {
        if(isItemStack(path)) {
            Material material = Material.valueOf(getString(path + ".material"));
            int amount = getInt(path + ".amount");
            short data = (short) getInt(path + ".data");
            ItemStack stack = new ItemStack(material, amount, data);
            ItemMeta meta = stack.getItemMeta();

            if(hasKeys(path, "name")) {
                meta.setDisplayName(getString(path + ".name"));
            }

            if(hasKeys(path, "lore")) {
                meta.setLore(getStringList(path + ".lore"));
            }

            if(hasKeys(path, "enchants")) {
                for (String enchant : getStringList(path + ".enchants")) {
                    String[] splitEnchant = enchant.split(":");
                    boolean ignoreLevel = splitEnchant.length >= 3 ? Boolean.valueOf(splitEnchant[2]) : false;
                    meta.addEnchant(Arrays.stream(Enchantment.values()).filter(enchantment -> enchantment.getName().equalsIgnoreCase(splitEnchant[0])).findFirst().orElse(null),
                            Integer.valueOf(splitEnchant[1]), ignoreLevel);
                }
            }

            if(hasKeys(path, "flags")) {
                for (String flag : getStringList(path + ".flags")) {
                    meta.addItemFlags(ItemFlag.valueOf(flag.toUpperCase()));
                }
            }

            if(hasKeys(path, "unbreakable")) {
                meta.spigot().setUnbreakable(getBoolean(path + ".unbreakable"));
            }

            stack.setItemMeta(meta);

            return stack;
        }

        return null;
    }

    @Override
    public List<String> getStringList(String path) {
        List<?> list = getList(path);

        if (list == null) {
            return new ArrayList<>(0);
        }

        List<String> result = new ArrayList<String>();

        for (Object object : list) {
            if ((object instanceof String) || (isPrimitiveWrapper(object))) {
                result.add(isColorCodeSupported() ? ChatColor.translateAlternateColorCodes('&', String.valueOf(object)) : String.valueOf(object));
            }
        }

        return result;
    }

    @Override
    public String getString(String path) {
        Object def = getDefault(path);
        return isColorCodeSupported() ? ChatColor.translateAlternateColorCodes('&', getString(path, def != null ? def.toString() : null)) : getString(path, def != null ? def.toString() : null);
    }
}
