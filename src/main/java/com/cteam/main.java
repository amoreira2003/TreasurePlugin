package com.cteam;

import com.cteam.constructors.TreasureEnemy;
import com.cteam.methods.EventManager;
import com.cteam.methods.TreasureEvent;
import com.cteam.yamls.Config;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.bukkit.Bukkit.getWorld;

public final class main extends JavaPlugin implements Listener {


   public static main plugin;

   public EventManager eventManager;

    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(TreasureEnemy.class);
        new Config();
        Bukkit.getLogger().info("Treasure Plugin Enabled");
        Bukkit.getPluginManager().registerEvents(this,this);
        Bukkit.getPluginManager().registerEvents(new Listeners(),this);
        getCommand("cancel").setExecutor(new CancelEvent());
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


    String formatRarityString(TreasureRarity rarity) {
        switch (rarity) {
            case NORMAL:
                return ChatColor.YELLOW + ChatColor.BOLD.toString()  + "NORMAL";
            case RARE:
            return ChatColor.AQUA + ChatColor.BOLD.toString()  + "RARE";
            case EPIC:
                return ChatColor.DARK_PURPLE + ChatColor.BOLD.toString()  + "EPIC";
            case LEGENDARY:
                return ChatColor.GOLD + ChatColor.BOLD.toString()  + "LEGENDARY";
            default:
                return rarity.name();
        }
    }

    ItemStack GenerateBook(TreasureRarity rarity) {
        ItemStack treasureBook = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) treasureBook.getItemMeta();
        YamlConfiguration yamlConfiguration = new Config().getConfigYAML();

        List<String> itemDesc = new ArrayList<>();

        for(String descLine : yamlConfiguration.getStringList("book.desc")) {
           itemDesc.add(descLine.replace("{treasureRarity}",formatRarityString(rarity)));
        }
        bookMeta.setLore(itemDesc);
        bookMeta.setTitle(yamlConfiguration.getString("book.title"));
        bookMeta.setAuthor(yamlConfiguration.getString("book.author"));

        int xMax = yamlConfiguration.getInt("book.generator.xMaxCords");
        int xMin = yamlConfiguration.getInt("book.generator.xMinCords");
        int zMax = yamlConfiguration.getInt("book.generator.zMaxCords");
        int zMin = yamlConfiguration.getInt("book.generator.zMinCords");

        boolean xNegative = new Random().nextBoolean();
        boolean zNegative = new Random().nextBoolean();

        int x = new Random().nextInt(xMax + 1) + xMin;
        int z = new Random().nextInt(zMax + 1) + zMin;

        if(xNegative) x *= -1;
        if(zNegative) z *= -1;

        int[] treasureCoordinates = {x,z};
        List<String> bookLinesFromYAML = yamlConfiguration.getStringList("book.introText");
        String bookLine = bookLinesFromYAML.get(new Random().nextInt(bookLinesFromYAML.size())).replace("{treasureCoordinates_X}", String.valueOf(x)).replace("{treasureCoordinates_Z}", String.valueOf(z)).replace("{treasureRarity}",rarity.name().toLowerCase());
        bookMeta.setPages(bookLine);
        bookMeta.setDisplayName(yamlConfiguration.getString("book.displayName"));
        PersistentDataContainer dataContainer = bookMeta.getPersistentDataContainer();
        NamespacedKey treasurePluginCoordinatesSpaceKey = new NamespacedKey(this, "TreasureBookCoordinates");
        dataContainer.set(treasurePluginCoordinatesSpaceKey, PersistentDataType.INTEGER_ARRAY, treasureCoordinates);
        NamespacedKey treasurePluginRaritySpaceKey = new NamespacedKey(this, "TreasureBookRarity");
        dataContainer.set(treasurePluginRaritySpaceKey, PersistentDataType.STRING, rarity.name());
        treasureBook.setItemMeta(bookMeta);
        return treasureBook;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lb, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("test")) return false;
        if (sender instanceof Player) {
            Player p = (Player) sender;
            ItemStack treasureBook = GenerateBook(TreasureRarity.NORMAL);
            p.getInventory().addItem(treasureBook);
            treasureBook = GenerateBook(TreasureRarity.RARE);
            p.getInventory().addItem(treasureBook);
            treasureBook = GenerateBook(TreasureRarity.EPIC);
            p.getInventory().addItem(treasureBook);
            treasureBook = GenerateBook(TreasureRarity.LEGENDARY);
            p.getInventory().addItem(treasureBook);


        }
        return false;
    }
}
