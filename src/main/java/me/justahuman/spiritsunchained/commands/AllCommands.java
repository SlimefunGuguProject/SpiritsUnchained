package me.justahuman.spiritsunchained.commands;

import me.justahuman.spiritsunchained.SpiritsUnchained;
import me.justahuman.spiritsunchained.implementation.mobs.AbstractCustomMob;

import me.justahuman.spiritsunchained.managers.SpiritsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AllCommands implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (! (sender instanceof Player player ) || args.length == 0) {
            return false;
        }
        if (args[0].equalsIgnoreCase("spawnsoul") && hasPerm(player) && args.length >= 2) {
            return spawnSoul(args[1], player);
        } else {
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("spirits")) {
            List<String> l = new ArrayList<String>();

            if (args.length == 1) {
                l.add("SpawnSoul");
            }

            else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("spawnsoul")) {
                    l.addAll(SpiritsUnchained.getSpiritEntityManager().EntityMap.keySet());
                }

            }

            return l;
        }
        return null;
    }

    private boolean hasPerm(Player player) {
        return player.isOp() || player.hasPermission("spiritsunchained.admin");
    }

    private boolean spawnSoul(String soulId, Player player) {
        AbstractCustomMob<?> spirit = SpiritsUnchained.getSpiritEntityManager().getCustomClass(null, soulId);
        if (spirit == null) {
            return false;
        }
        spirit.spawn(player.getLocation(), player.getWorld(), "Natural");
        return true;
    }
}
