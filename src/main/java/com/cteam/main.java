package com.cteam;

import com.cteam.methods.EventManager;
import com.cteam.methods.TreasureEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

import static org.bukkit.Bukkit.getConsoleSender;
import static org.bukkit.Bukkit.getWorld;

public final class main extends JavaPlugin implements Listener {


   public static main plugin;

   public EventManager eventManager;

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Treasure Plugin Enabled");
        Bukkit.getPluginManager().registerEvents(this,this);
        Bukkit.getPluginManager().registerEvents(new Listeners(),this);
        getCommand("killent").setExecutor(new KillEnt());
        getCommand("cancel").setExecutor(new CancelEvent());
        eventManager = new EventManager();
    }

    @Override
    public void onDisable() {
        for(Player player : eventManager.onEvent.keySet()) {
            TreasureEvent treasureEvent = eventManager.onEvent.get(player);
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
        if(eventManager.onEvent.containsKey(p)) eventManager.onEvent.remove(p);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lb, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("test")) return false;
        if (sender instanceof Player) {
            Player p = (Player) sender;
                ItemStack treasureBook = new ItemStack(Material.WRITTEN_BOOK);
                ItemMeta bookMeta = treasureBook.getItemMeta();
                bookMeta.setDisplayName("Treasure Book");
                PersistentDataContainer dataContainer = bookMeta.getPersistentDataContainer();
                int[] treasureCordinates = {162,63,66};
                NamespacedKey treasurePluginSpaceKey = new NamespacedKey(this, "TREASUREBOOKS");
                dataContainer.set(treasurePluginSpaceKey, PersistentDataType.INTEGER_ARRAY, treasureCordinates);
                treasureBook.setItemMeta(bookMeta);
                p.getInventory().addItem(treasureBook);

        }
        return false;
    }
}
