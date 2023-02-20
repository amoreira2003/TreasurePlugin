package com.cteam;

import com.cteam.methods.EventManager;
import com.cteam.methods.TreasureEvent;
import com.cteam.yamls.Config;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static org.bukkit.Bukkit.getConsoleSender;
import static org.bukkit.Bukkit.getWorld;

public final class main extends JavaPlugin implements Listener {


   public static main plugin;

   public EventManager eventManager;

    @Override
    public void onEnable() {
        new Config();
        Bukkit.getLogger().info("Treasure Plugin Enabled");
        Bukkit.getPluginManager().registerEvents(this,this);
        Bukkit.getPluginManager().registerEvents(new Listeners(),this);
        getCommand("killent").setExecutor(new KillEnt());
        getCommand("cancel").setExecutor(new CancelEvent());
        getCommand("stage").setExecutor(new EventStage());
        eventManager = new EventManager();
    }

    @Override
    public void onDisable() {
        for(Player player : eventManager.getPlayersInChallenge()) {
            TreasureEvent treasureEvent = eventManager.getTreasureEvent(player);
            treasureEvent.endEvent();

        }
        Bukkit.getLogger().info("Treasure Plugin Disabled");
    }


    @Override
    public void onLoad() {
        plugin = this;
        Bukkit.getLogger().info("Treasure Plugin Loaded");

    }



    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if(!eventManager.removePlayerAsChallenger(p)) getLogger().info(p.getName() + " Treasure was not cleaned correctly");
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lb, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("test")) return false;
        if (sender instanceof Player) {
            Player p = (Player) sender;
                ItemStack treasureBook = new ItemStack(Material.WRITTEN_BOOK);
                BookMeta bookMeta = (BookMeta) treasureBook.getItemMeta();
                String[] itemDesc = {"", ChatColor.AQUA + "" + ChatColor.BOLD + "Right click to read the message","", ChatColor.GRAY +  "Treasure Rarity: " + ChatColor.WHITE + ChatColor.BOLD + "NORMAL", ""};
                bookMeta.setLore(Arrays.asList(itemDesc));
                bookMeta.setTitle("Find the treasure");
                bookMeta.setAuthor("Treasure Book");
                int[] treasureCordinates = {162,63,66};
                String[] bookLines = {"Hey, just found an old map, if you can find it, keep whatever you find \n\nX:" + treasureCordinates[0] +" Z: " + treasureCordinates[2]};
                bookMeta.setPages(bookLines);
                bookMeta.setDisplayName("Treasure Book");
                PersistentDataContainer dataContainer = bookMeta.getPersistentDataContainer();

                NamespacedKey treasurePluginSpaceKey = new NamespacedKey(this, "TREASUREBOOKS");
                dataContainer.set(treasurePluginSpaceKey, PersistentDataType.INTEGER_ARRAY, treasureCordinates);
                treasureBook.setItemMeta(bookMeta);
                p.getInventory().addItem(treasureBook);

        }
        return false;
    }
}
