package com.cteam;

import com.cteam.methods.TreasureEvent;
import com.cteam.yamls.Config;
import com.sun.tools.javac.jvm.Items;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class Listeners implements Listener {

    @EventHandler
    public void interactWithTreasureBook(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player p = e.getPlayer();
        ItemStack interactedBook = e.getItem();
        if(interactedBook == null || interactedBook.getType().isAir()) return;
        if(interactedBook.getType() != Material.WRITTEN_BOOK) return;
        if(!interactedBook.getItemMeta().getDisplayName().equalsIgnoreCase("Treasure Book")) return;
        PersistentDataContainer dataHolder =interactedBook.getItemMeta().getPersistentDataContainer();
        NamespacedKey treasurePluginSpaceKey = new NamespacedKey(main.plugin, "TreasureBookCoordinates");
        int[] cords = dataHolder.get(treasurePluginSpaceKey, PersistentDataType.INTEGER_ARRAY);
        NamespacedKey treasurePluginRaritySpaceKey = new NamespacedKey(main.plugin, "TreasureBookRarity");
        TreasureRarity rarity = TreasureRarity.valueOf(dataHolder.get(treasurePluginRaritySpaceKey, PersistentDataType.STRING));
        int X = cords[0];
        int Z = cords[1];
        p.sendMessage(Arrays.toString(cords));
        p.sendMessage(rarity.name());
        if(p.getLocation().getBlockX() != X && p.getLocation().getBlockZ() != Z) return;
        if(main.plugin.eventManager.isPlayerChallenging(p)) {
            p.sendMessage();
            return;
        }
            main.plugin.eventManager.startEvent(p, rarity);
            p.getInventory().setItem(e.getHand(), new ItemStack(Material.AIR));





    }


    @EventHandler
    public void breakBlock(BlockBreakEvent e) {
        Block b = e.getBlock();
        if(main.plugin.eventManager.protectedBlocks.contains(b)) {
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void interactEntity(PlayerInteractAtEntityEvent e) {
        Player p = e.getPlayer();
        Entity entity = e.getRightClicked();
        YamlConfiguration yamlConfiguration = new Config().getConfigYAML();
        if(!entity.getCustomName().equalsIgnoreCase(yamlConfiguration.getString("displayText.challengeWinChestName")))  return;
        if(main.plugin.eventManager.isPlayerChallenging(p)) {
        TreasureEvent treasureEvent = main.plugin.eventManager.getTreasureEvent(p);

        p.openInventory(treasureEvent.getLootInventory());
        treasureEvent.startEndingPhase();
        }


        entity.remove();

    }



}
