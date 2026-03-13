package kireiko.dev.anticheat.commands.subcommands;

import com.google.common.collect.ImmutableList;
import kireiko.dev.anticheat.api.data.PlayerContainer;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.commands.MXSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static kireiko.dev.anticheat.utils.MessageUtils.wrapColors;

public final class DebugCommand extends MXSubCommand {
    public DebugCommand() {
        super("debug");
    }

    @Override
    public String getDescription() {
        return "Toggle debug mode(check verbose)";
    }

    @Override
    public int getMinArgs() {
        return 0;
    }

    @Override
    public int getMaxArgs() {
        return 0;
    }

    @Override
    public boolean onlyPlayerCanUse() {
        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, String[] args) {
        Player player = (Player) sender;
        PlayerProfile profile = PlayerContainer.getProfile(player);
        if (profile == null) {
            sender.sendMessage(wrapColors("&cProfile not initialized!"));
            return true;
        }
        sender.sendMessage(wrapColors("&cDebug: &e" + profile.toggleDebug()));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return ImmutableList.of();
    }
}
