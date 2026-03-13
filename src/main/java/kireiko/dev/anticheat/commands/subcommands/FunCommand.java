package kireiko.dev.anticheat.commands.subcommands;

import com.google.common.collect.ImmutableList;
import kireiko.dev.anticheat.api.player.fun.FunItemsService;
import kireiko.dev.anticheat.commands.MXSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class FunCommand extends MXSubCommand {
    public FunCommand() {
        super("fun");
    }

    @Override
    public String getDescription() {
        return "Give you fun items";
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
        FunItemsService.give(player);
        sender.sendMessage("Given fun items");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return ImmutableList.of();
    }
}
