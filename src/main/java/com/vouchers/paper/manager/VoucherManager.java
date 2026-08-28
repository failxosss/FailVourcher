package com.vouchers.paper.manager;

import com.vouchers.paper.VoucherPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VoucherManager {

    private final VoucherPlugin plugin;
    private final Map<String, VoucherType> vouchers = new LinkedHashMap<>();
    private final NamespacedKey voucherKey;

    public VoucherManager(VoucherPlugin plugin) {
        this.plugin = plugin;
        this.voucherKey = new NamespacedKey(plugin, "voucher_id");
    }

    /** Znovu nacte vsechny vouchery z config.yml (vola se i pri /voucher reload). */
    public void loadVouchers() {
        vouchers.clear();
        plugin.reloadConfig();

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("vouchers");
        if (section == null) {
            plugin.getLogger().warning("V config.yml nebyla nalezena sekce 'vouchers'!");
            return;
        }

        for (String id : section.getKeys(false)) {
            ConfigurationSection voucherSection = section.getConfigurationSection(id);
            if (voucherSection == null) continue;

            String materialName = voucherSection.getString("material", "PAPER");
            Material material = Material.matchMaterial(materialName);
            if (material == null) {
                plugin.getLogger().warning("Neplatny material '" + materialName
                        + "' pro voucher '" + id + "', pouzivam PAPER.");
                material = Material.PAPER;
            }

            String displayName = voucherSection.getString("name", "&fVoucher");
            List<String> lore = voucherSection.getStringList("lore");
            boolean glow = voucherSection.getBoolean("glow", false);
            List<String> commands = voucherSection.getStringList("commands");
            String broadcast = voucherSection.getString("broadcast", "");
            String receiveMessage = voucherSection.getString("receive-message", "");
            String redeemMessage = voucherSection.getString("redeem-message", "");
            boolean consumeOnUse = voucherSection.getBoolean("consume-on-use", true);
            int customModelData = voucherSection.getInt("custom-model-data", 0);

            VoucherType type = new VoucherType(id, material, displayName, lore, glow, commands,
                    broadcast, receiveMessage, redeemMessage, consumeOnUse, customModelData);
            vouchers.put(id.toLowerCase(), type);
        }
    }

    public VoucherType getVoucher(String id) {
        if (id == null) return null;
        return vouchers.get(id.toLowerCase());
    }

    public Map<String, VoucherType> getVouchers() {
        return vouchers;
    }

    public NamespacedKey getVoucherKey() {
        return voucherKey;
    }

    /** Vrati ID voucheru ulozene v NBT/PDC daneho itemu, nebo null pokud to voucher neni. */
    public String getVoucherIdFromItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        if (!meta.getPersistentDataContainer().has(voucherKey, PersistentDataType.STRING)) return null;
        return meta.getPersistentDataContainer().get(voucherKey, PersistentDataType.STRING);
    }

    /** Vytvori fyzicky ItemStack (papirek) pro dany typ voucheru. */
    public ItemStack createVoucherItem(VoucherType type, int amount) {
        ItemStack item = new ItemStack(type.getMaterial(), Math.max(1, amount));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(color(type.getDisplayName()));

            List<String> coloredLore = new ArrayList<>();
            for (String line : type.getLore()) {
                coloredLore.add(color(line));
            }
            meta.setLore(coloredLore);

            if (type.getCustomModelData() > 0) {
                meta.setCustomModelData(type.getCustomModelData());
            }

            if (type.isGlow()) {
                // Paper 1.20.5+ API - vizualni "glint" bez skryte enchanty
                meta.setEnchantmentGlintOverride(true);
            }

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

            meta.getPersistentDataContainer().set(voucherKey, PersistentDataType.STRING, type.getId());
            item.setItemMeta(meta);
        }
        return item;
    }

    /** Da hraci do inventare X kusu daneho voucheru. Vraci false pokud voucher s tim ID neexistuje. */
    public boolean giveVoucher(Player target, String voucherId, int amount) {
        VoucherType type = getVoucher(voucherId);
        if (type == null) return false;

        ItemStack item = createVoucherItem(type, amount);
        Map<Integer, ItemStack> leftover = target.getInventory().addItem(item);
        if (!leftover.isEmpty()) {
            leftover.values().forEach(i -> target.getWorld().dropItem(target.getLocation(), i));
        }

        if (!type.getReceiveMessage().isEmpty()) {
            target.sendMessage(color(type.getReceiveMessage().replace("%player%", target.getName())));
        }
        return true;
    }

    /** Spusti vsechny prikazy naskladane pro dany typ voucheru pro konkretniho hrace. */
    public void redeemVoucher(Player player, VoucherType type) {
        for (String cmd : type.getCommands()) {
            String parsed = cmd.replace("%player%", player.getName())
                    .replace("%uuid%", player.getUniqueId().toString());
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), parsed);
        }

        if (!type.getRedeemMessage().isEmpty()) {
            player.sendMessage(color(type.getRedeemMessage().replace("%player%", player.getName())));
        }

        if (type.getBroadcastMessage() != null && !type.getBroadcastMessage().isEmpty()) {
            plugin.getServer().broadcastMessage(color(type.getBroadcastMessage().replace("%player%", player.getName())));
        }
    }

    public static String color(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
