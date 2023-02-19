package com.cteam;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class KillEnt implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lb, String[] strings) {
        if(!cmd.getName().equalsIgnoreCase("killent")) return false;

            if (sender instanceof Player) {
                Player p = (Player) sender;
                for(Entity entitys : p.getWorld().getEntities()) {
                    entitys.remove();
                }
            }
        return false;
    }
}
