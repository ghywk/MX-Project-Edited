package kireiko.dev.anticheat.commands;

import kireiko.dev.anticheat.MX;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public abstract class MXSubCommand {

    protected final String name;

    public MXSubCommand(String name) {
        this.name = name;
    }

    /**
     * @return description of the subcommand
     */
    public abstract String getDescription();

    /**
     * @return usage of the subcommand
     */
    public String getUsage() {
        return "/" + MX.command + " " + getName();
    }

    /**
     * @return permission of the subcommand
     */
    public String getPermission() {
        return null;
    }

    public boolean hasPermission(CommandSender sender) {
        if (getPermission() == null || getPermission().isEmpty()) {
            return true;
        } else {
            return sender.hasPermission(getPermission());
        }
    }

    /**
     * @return minimum arguments of the subcommand
     */
    public abstract int getMinArgs();

    /**
     * @return max args of the subcommand
     */
    public abstract int getMaxArgs();

    /**
     * @return if the subcommand can only used by player
     */
    public abstract boolean onlyPlayerCanUse();

    public abstract boolean onCommand(@NotNull CommandSender sender, String[] args);

    public abstract List<String> onTabComplete(CommandSender sender, String[] args);
}
