package kireiko.dev.anticheat.commands.subcommands;

import com.google.common.collect.ImmutableList;
import kireiko.dev.anticheat.MX;
import kireiko.dev.anticheat.commands.MXSubCommand;
import kireiko.dev.millennium.math.Statistics;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static kireiko.dev.anticheat.utils.MessageUtils.wrapColors;

public final class StatsCommand extends MXSubCommand {
    public StatsCommand() {
        super("stats");
    }

    @Override
    public String getDescription() {
        return "Show statistics";
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
        return false;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, String[] args) {
        int banCount = 0, blockCount = 0;
        for (int i : MX.bannedPerMinuteList) {
            banCount += i;
        }
        for (int i : MX.blockedPerMinuteList) {
            blockCount += i;
        }
        sender.sendMessage(wrapColors("&c&l" + MX.name + " &9Statistics"));
        sender.sendMessage(wrapColors("&4Banned in the last hour&f: &e" + banCount));
        sender.sendMessage(wrapColors("&4Average bans per minute&f: &e"
                + (int) Statistics.getAverage(MX.bannedPerMinuteList)));
        sender.sendMessage(wrapColors("&4Blocked hits in the last hour&f: &e" + blockCount));
        sender.sendMessage(wrapColors("&4Average blocked hits per minute&f: &e"
                + (int) Statistics.getAverage(MX.blockedPerMinuteList)));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return ImmutableList.of();
    }
}
