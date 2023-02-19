package com.cteam;

import com.sun.tools.javac.jvm.Items;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
        e.setCancelled(true);



        PersistentDataContainer dataHolder =interactedBook.getItemMeta().getPersistentDataContainer();
        NamespacedKey treasurePluginSpaceKey = new NamespacedKey(main.plugin, "TREASUREBOOKS");
        int[] cords = dataHolder.get(treasurePluginSpaceKey, PersistentDataType.INTEGER_ARRAY);
        int X = cords[0];
        int Y = cords[1];
        int Z = cords[2];
        p.sendMessage(Arrays.toString(cords));
        if(p.getLocation().getBlockX() != X && p.getLocation().getBlockZ() != Z) return;
        if(main.plugin.eventManager.onEvent.containsKey(p)) {
            p.sendMessage("\n"+ ChatColor.RED +"You are already doing a challenge \n ");
            return;
        }
            main.plugin.eventManager.startEvent(p);
            p.getInventory().setItem(e.getHand(), new ItemStack(Material.AIR));





    }


}
