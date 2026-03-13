package kireiko.dev.anticheat.api.player.fun;

import kireiko.dev.anticheat.utils.MaterialUtil;
import kireiko.dev.anticheat.utils.helper.ItemStackHelper;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class FunItemsService {
    public static void give(final Player player) {
        final Material rocket = MaterialUtil.getMaterial("DIAMOND_HORSE_ARMOR", "DIAMOND_BARDING");
        final Material ak47 = MaterialUtil.getMaterial("GOLDEN_HORSE_ARMOR", "GOLD_BARDING");

        { // Hook
            final ItemStack rod = new ItemStack(Material.FISHING_ROD);
            final ItemMeta meta = rod.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§9Hook");
                meta.addEnchant(Enchantment.DAMAGE_ALL, 10, true);
                rod.setItemMeta(meta);
            }
            ItemStackHelper.setUnbreakable(rod, true);
            player.getInventory().addItem(rod);
        }
        { // Rocket Launcher
            final ItemStack rod = new ItemStack(rocket);
            final ItemMeta meta = rod.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§9Rocket Launcher");
                meta.addEnchant(Enchantment.DAMAGE_ALL, 10, true);
                rod.setItemMeta(meta);
            }
            ItemStackHelper.setUnbreakable(rod, true);
            player.getInventory().addItem(rod);
        }
        { // Winter Staff
            final ItemStack rod = new ItemStack(Material.DIAMOND_HOE);
            final ItemMeta meta = rod.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§9Winter Staff");
                meta.addEnchant(Enchantment.DAMAGE_ALL, 15, true);
                rod.setItemMeta(meta);
            }
            ItemStackHelper.setUnbreakable(rod, true);
            player.getInventory().addItem(rod);
        }
        { // Blaze Staff
            final ItemStack rod = new ItemStack(Material.BLAZE_ROD);
            final ItemMeta meta = rod.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§9Blaze Staff");
                meta.addEnchant(Enchantment.DAMAGE_ALL, 15, true);
                rod.setItemMeta(meta);
            }
            ItemStackHelper.setUnbreakable(rod, true);
            player.getInventory().addItem(rod);
        }
        { // AK-47
            final ItemStack rod = new ItemStack(ak47);
            final ItemMeta meta = rod.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§9AK-47");
                meta.addEnchant(Enchantment.DAMAGE_ALL, 9, true);
                rod.setItemMeta(meta);
            }
            ItemStackHelper.setUnbreakable(rod, true);
            player.getInventory().addItem(rod);
        }
        { // Cursed Spell
            final ItemStack rod = new ItemStack(Material.BOOK);
            final ItemMeta meta = rod.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§9Cursed Spell");
                meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
                rod.setItemMeta(meta);
            }
            ItemStackHelper.setUnbreakable(rod, true);
            player.getInventory().addItem(rod);
        }
    }
}
