package com.cteam;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EventStage implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lb, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("stage")) return false;

        if (sender instanceof Player) {
            Player p = (Player) sender;
           if (!main.plugin.eventManager.isPlayerChallenging(p)) {
               p.sendMessage("You ain't in a challenge bitch ");
               return false;
           }
            p.sendMessage(main.plugin.eventManager.getTreasureEvent(p).getEventPhase().toString());
            return true;
        }
        return false;
    }
}
