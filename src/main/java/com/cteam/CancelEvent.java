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
           if (!main.plugin.eventManager.removePlayerAsChallenger(p)) p.sendMessage("Event was not cancelled");
            p.sendMessage("Event was cancelled");
            return true;
        }
        return false;
    }
}
