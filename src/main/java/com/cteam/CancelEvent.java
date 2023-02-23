package com.cteam;

import com.cteam.yamls.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class CancelEvent implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lb, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("cancel")) return false;

        if (sender instanceof Player) {
            Player p = (Player) sender;
            YamlConfiguration yamlConfiguration = new Config().getConfigYAML();
            switch (args.length ) {

                case 0:
                    if(!p.hasPermission("cteam.treasurebook.cancelevent")) p.sendMessage(yamlConfiguration.getString("error.noPerm"));
                    if (!main.plugin.eventManager.removePlayerAsChallenger(p)) p.sendMessage(yamlConfiguration.getString("error.notChallengingSelf"));
                    p.sendMessage(yamlConfiguration.getString("success.challengeCancelSelf"));
                    return true;

                case 1:
                    Player target = Bukkit.getPlayer(args[0]);
                    if(target == null) p.sendMessage(yamlConfiguration.getString("error.playerNotFound").replace("{player_target}",args[0]));
                    if (!main.plugin.eventManager.removePlayerAsChallenger(target)) p.sendMessage(yamlConfiguration.getString("error.notChallengingTarget").replace("{player_target}",target.getName()));
                    p.sendMessage(yamlConfiguration.getString("success.challengeCancelTarget").replace("{player_target}",target.getName()));
                    return true;
                default:
                    return false;
            }

        }
        return false;
    }
}
