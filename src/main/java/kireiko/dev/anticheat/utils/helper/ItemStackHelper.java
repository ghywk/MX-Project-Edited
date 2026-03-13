package kireiko.dev.anticheat.utils.helper;

import kireiko.dev.anticheat.utils.ReflectionUtils;
import kireiko.dev.anticheat.utils.version.VersionUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;

public final class ItemStackHelper {
    private static final Method SET_UNBREAKABLE_METHOD;
    private static final boolean MODERN_API;

    static {
        SET_UNBREAKABLE_METHOD = ReflectionUtils.getMethod(ItemMeta.class,
                "setUnbreakable", boolean.class);
        MODERN_API = SET_UNBREAKABLE_METHOD != null;
    }

    public static void setUnbreakable(ItemStack item, boolean unbreakable) {
        if (item == null) return;
        try {
            ItemMeta meta = item.getItemMeta();
            if (MODERN_API) {
                SET_UNBREAKABLE_METHOD.invoke(meta, unbreakable);
                item.setItemMeta(meta);
            } else {
                handleLegacy(item, unbreakable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleLegacy(ItemStack item, boolean unbreakable) {
        String version = VersionUtil.getBukkitVersion();
        Class<?> craftClass = ReflectionUtils.getClass(
                "org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");

        Object nmsItem = ReflectionUtils.invokeStaticMethod(craftClass,
                "asNMSCopy", item);

        Object tag = getTag(nmsItem);
        ReflectionUtils.invokeMethod(tag, "setInt",
                "Unbreakable", unbreakable ? 1 : 0);

        ReflectionUtils.invokeMethod(nmsItem, "setTag", tag);
        ItemStack result = (ItemStack) ReflectionUtils.invokeStaticMethod(craftClass,
                "asBukkitCopy", new Object[]{nmsItem});

        item.setItemMeta(result.getItemMeta());
    }

    private static Object getTag(Object nmsItem) {
        if ((boolean) ReflectionUtils.invokeMethod(nmsItem, "hasTag")) {
            return ReflectionUtils.invokeMethod(nmsItem, "getTag");
        }
        return ReflectionUtils.newInstance(
                "net.minecraft.server." + VersionUtil.getBukkitVersion() + ".NBTTagCompound");
    }
}