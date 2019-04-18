package net.aeronetwork.api.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Map;

public class ItemStackBuilder {

    private Material material;
    private ItemType itemType;
    private int amount = 1;
    private short data = 0;
    private String name;
    private List<String> lore = Lists.newArrayList();
    private Map<Enchantment, Integer> enchantments = Maps.newHashMap();
    private List<ItemFlag> itemFlags = Lists.newArrayList();
    private boolean unbreakable;

    // Leather Armor
    private Color color = Color.GRAY;

    // Potions
    private List<PotionEffect> potionEffects = Lists.newArrayList();

    // Banners
    private DyeColor baseColor = DyeColor.BLACK;
    private List<Pattern> patterns = Lists.newArrayList();

    // Skulls
    private String owner = "UNKNOWN";

    private ItemStack builtItem;

    public ItemStackBuilder(Material material, ItemType type) {
        this.material = material;
        this.itemType = type;
    }

    public ItemStackBuilder(Material material, int amount, ItemType type) {
        this.material = material;
        this.itemType = type;
        this.amount = amount;
    }

    public ItemStackBuilder(Material material, int amount, short data, ItemType type) {
        this.material = material;
        this.amount = amount;
        this.data = data;
        this.itemType = type;
    }

    public ItemStackBuilder(ItemStack item) {
        this.material = item.getType();
        this.amount = item.getAmount();
        this.data = item.getDurability();

        ItemMeta m = item.getItemMeta();
        this.name = m.getDisplayName();
        this.lore = m.getLore();
        this.enchantments = m.getEnchants();
        this.unbreakable = m.spigot().isUnbreakable();

        if(m instanceof LeatherArmorMeta) {
            LeatherArmorMeta meta = (LeatherArmorMeta) m;
            this.itemType = ItemType.LEATHER_ARMOR;
            this.color = meta.getColor();
        } else if(m instanceof PotionMeta) {
            PotionMeta meta = (PotionMeta) m;
            this.itemType = ItemType.POTION;
            this.potionEffects = meta.getCustomEffects();
        } else if(m instanceof BannerMeta) {
            BannerMeta meta = (BannerMeta) m;
            this.itemType = ItemType.BANNER;
            this.baseColor = meta.getBaseColor();
            this.patterns = meta.getPatterns();
        } else if(m instanceof SkullMeta) {
            SkullMeta meta = (SkullMeta) m;
            this.itemType = ItemType.SKULL;
            this.owner = meta.getOwner();
        }
    }

    public ItemStackBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public ItemStackBuilder setAmount(int amount) {
        if(amount < 1 || amount > 64) {
            this.amount = 1;
        } else {
            this.amount = amount;
        }
        return this;
    }

    public ItemStackBuilder setData(short data) {
        this.data = data;
        return this;
    }

    public ItemStackBuilder setName(String name) {
		this.name = ChatColor.translateAlternateColorCodes('&', name);
		return this;
	}

	public ItemStackBuilder setLore(String[] lore) {
		this.lore = Lists.newArrayList(lore);
		return this;
	}

    public ItemStackBuilder setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

	public ItemStackBuilder addLore(String lore) {
        this.lore.add(lore);
        return this;
    }

    public ItemStackBuilder addLore(String... lore) {
        if(lore != null)
            this.lore.addAll(Lists.newArrayList(lore));
        return this;
    }

    public ItemStackBuilder addEnchant(Enchantment enchant, int level) {
        enchantments.put(enchant, level);
        return this;
    }

    public ItemStackBuilder removeEnchant(Enchantment enchant) {
        enchantments.remove(enchant);
        return this;
    }

	public ItemStackBuilder setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
		return this;
	}

    public ItemStackBuilder setColor(Color color) {
        this.color = color;
        return this;
    }

    public ItemStackBuilder addPotionEffect(PotionEffect effect) {
        this.potionEffects.add(effect);
        return this;
    }

    public ItemStackBuilder setBaseColor(DyeColor baseColor) {
        this.baseColor = baseColor;
        return this;
    }

    public ItemStackBuilder addPattern(Pattern pattern) {
        this.patterns.add(pattern);
        return this;
    }

    public ItemStackBuilder setOwner(String owner) {
        this.owner = owner;
        this.data = (short) 3;
        return this;
    }

    public ItemStackBuilder setAttribute(double baseDamage) {
        if(builtItem == null)
            build();
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(builtItem);
        NBTTagCompound compound = nmsStack.getTag();
        if (compound == null) {
            compound = new NBTTagCompound();
            nmsStack.setTag(compound);
            compound = nmsStack.getTag();
        }
        NBTTagList modifiers = new NBTTagList();
        NBTTagCompound damage = new NBTTagCompound();
        damage.set("AttributeName", new NBTTagString("generic.attackDamage"));
        damage.set("Name", new NBTTagString("generic.attackDamage"));
        damage.set("Amount", new NBTTagDouble(baseDamage));
        damage.set("Operation", new NBTTagInt(0));
        damage.set("UUIDLeast", new NBTTagInt(894654));
        damage.set("UUIDMost", new NBTTagInt(2872));
        modifiers.add(damage);
        compound.set("AttributeModifiers", modifiers);
        nmsStack.setTag(compound);
        return this;
    }

    public ItemStackBuilder setGlowing() {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(builtItem);
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        }
        if (tag == null) tag = nmsStack.getTag();
        NBTTagList ench = new NBTTagList();
        tag.set("ench", ench);
        nmsStack.setTag(tag);
        builtItem = CraftItemStack.asCraftMirror(nmsStack);
        return this;
    }

    public ItemStackBuilder build() {
        ItemStack item = new ItemStack(material, amount, data);
        ItemMeta m = item.getItemMeta();
        m.setDisplayName(name);
        m.setLore(lore);
        enchantments.forEach((enchant, level) -> m.addEnchant(enchant, level, true));
        m.addItemFlags(itemFlags.toArray(new ItemFlag[0]));
        m.spigot().setUnbreakable(unbreakable);

        if(itemType == ItemType.LEATHER_ARMOR) {
            LeatherArmorMeta meta = (LeatherArmorMeta) m;
            meta.setColor(color);
        } else if(itemType == ItemType.POTION) {
            PotionMeta meta = (PotionMeta) m;
            potionEffects.forEach(effect -> meta.addCustomEffect(effect, true));
        } else if(itemType == ItemType.BANNER) {
            BannerMeta meta = (BannerMeta) m;
            meta.setBaseColor(baseColor);
            patterns.forEach(meta::addPattern);
        } else if(itemType == ItemType.SKULL) {
            SkullMeta meta = (SkullMeta) m;
            meta.setOwner(owner);
        }
        item.setItemMeta(m);

        this.builtItem = item;
		return this;
	}

	public ItemStack get() {
        if(builtItem == null)
            build();
        return builtItem;
    }

	public enum ItemType {
        NORMAL,
        LEATHER_ARMOR,
        POTION,
        BANNER,
        SKULL
    }
}
