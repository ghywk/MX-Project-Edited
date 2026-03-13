package kireiko.dev.anticheat.utils;

import kireiko.dev.anticheat.api.data.PlayerContainer;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.utils.version.VersionUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MessageUtils {

    private static final Pattern HEX_PATTERN =
                    Pattern.compile("(?i)&#([A-F0-9]{6})");

    public static void sendMessagesToPlayers(String permission, String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerProfile profile = PlayerContainer.getProfile(player);
            if (profile == null || !profile.isAlerts()) {
                continue;
            }
            if (player.hasPermission(permission)) {
                player.sendMessage(wrapColors(message));
            }
        }
    }

    public static void sendMessagesToPlayersNative(String permission, String permission2, String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(permission) || player.hasPermission(permission2)) {
                player.sendMessage(wrapColors(message));
            }
        }
    }

    public static String wrapColors(String input) {
        if (input == null) return null;
        if (VersionUtil.is1_16orAbove()) {
            Matcher matcher = HEX_PATTERN.matcher(input);
            StringBuffer sb = new StringBuffer(input.length() * 2);

            while (matcher.find()) {
                String hexCode = "#" + matcher.group(1); // "#A1B2C3"
                String replacement = net.md_5.bungee.api.ChatColor.of(hexCode).toString(); // §x§A§1§B§2§C§3
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            }
            matcher.appendTail(sb);

            return ChatColor.translateAlternateColorCodes('&', sb.toString());
        }
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static String wrapColors(String... v) {
        final StringBuilder builder = new StringBuilder();
        for (final String s : v) {
            final String wrapped = wrapColors(s);
            builder.append((builder.length() == 0) ? wrapped : "\n" + wrapped);
        }
        return builder.toString();
    }

    public static String getDate() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd:MM:yyyy");
        return sdf.format(date);
    }
}
