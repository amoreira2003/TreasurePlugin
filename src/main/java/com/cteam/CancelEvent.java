package com.cteam;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CancelEvent implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lb, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("cancel")) return false;

        if (sender instanceof Player) {
            Player p = (Player) sender;
           if (!main.plugin.eventManager.onEvent.containsKey(p)) return false;
            main.plugin.eventManager.onEvent.remove(p);
            p.sendMessage("Event Cancelled");
            return true;
        }
        return false;
    }
}
