package kireiko.dev.anticheat.utils;

import kireiko.dev.anticheat.utils.version.VersionUtil;
import org.bukkit.entity.Player;

public final class TitleUtils {

    final static boolean MODERN_TITLE_API;

    static {
        MODERN_TITLE_API = VersionUtil.is1_16orAbove();
    }

    public static void sendTitle(Player player, String title, String subtitle,
                                 int fadeIn, int stay, int fadeOut) {
        if (!MODERN_TITLE_API) {
            player.sendTitle(title, subtitle);
        } else {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
    }
}