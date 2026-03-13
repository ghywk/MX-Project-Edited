package kireiko.dev.anticheat.commands.subcommands;

import com.google.common.collect.ImmutableList;
import kireiko.dev.anticheat.MX;
import kireiko.dev.anticheat.commands.MXSubCommand;
import kireiko.dev.anticheat.utils.ConfigCache;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static kireiko.dev.anticheat.utils.MessageUtils.wrapColors;

public final class BroadcastCommand extends MXSubCommand {

    public BroadcastCommand() {
        super("bc");
    }

    @Override
    public String getDescription() {
        return "Broadcast a message to all players";
    }

    @Override
    public String getUsage() {
        return "/" + MX.command + " " + getName() + " <message>";
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 999;
    }

    @Override
    public boolean onlyPlayerCanUse() {
        return false;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, String[] args) {
        StringBuilder total = new StringBuilder();
        for (String arg : args) {
            total.append(" ").append(arg);
        }
        String totalMessage = wrapColors(ConfigCache.BC_MSG.replace("%message%", total.toString().trim()));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(totalMessage);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return ImmutableList.of();
    }

}
