package kireiko.dev.anticheat.commands.subcommands;

import com.google.common.collect.ImmutableList;
import kireiko.dev.anticheat.MX;
import kireiko.dev.anticheat.api.data.PlayerContainer;
import kireiko.dev.anticheat.api.player.PlayerProfile;
import kireiko.dev.anticheat.commands.MXSubCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class PunishCommand extends MXSubCommand {

    public PunishCommand() {
        super("punish");
    }

    @Override
    public String getDescription() {
        return "Punish a player";
    }

    @Override
    public String getUsage() {
        return "/" + MX.command + " " + getName() + " <player>";
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 1;
    }

    @Override
    public boolean onlyPlayerCanUse() {
        return false;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, String[] args) {
        PlayerProfile playerProfile = PlayerContainer.getProfileByName(args[0]);
        if (playerProfile == null) {
            sender.sendMessage("§cPlayer not found... Sorry!");
            return true;
        }
        playerProfile.punish("Skill issue", "Bad guy", "Punish (Staff)", 999.0f);
        sender.sendMessage("§aPunished " + playerProfile.getPlayer().getName() + "!");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) return null;
        return ImmutableList.of(); // return empty list
    }
}
