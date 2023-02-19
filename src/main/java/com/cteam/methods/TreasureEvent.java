package com.cteam.methods;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class TreasureEvent {

    Player challenger;

    ArmorStand floatingChest;


    ArrayList<Block> cageBlocks;

    ArrayList<LivingEntity> enemies;

    BukkitTask eventTask;

    public Player getChallenger() {
        return challenger;
    }

    public ArrayList<Block> getCageBlocks() {
        return cageBlocks;
    }

    public ArmorStand getFloatingChest() {
        return floatingChest;
    }

    public void setFloatingChest(ArmorStand floatingChest) {
        this.floatingChest = floatingChest;
    }


    public void setChallenger(Player challenger) {
        this.challenger = challenger;
    }

    public void setCageBlocks(ArrayList<Block> cageBlocks) {
        this.cageBlocks = cageBlocks;
    }

    public ArrayList<LivingEntity> getEnemies() {
        return enemies;
    }

    public void setEnemies(ArrayList<LivingEntity> enemies) {
        this.enemies = enemies;
    }

    public BukkitTask getEventTask() {
        return eventTask;
    }

    public void setEventTask(BukkitTask eventTask) {
        this.eventTask = eventTask;
    }

    public void clearStructures(boolean bruteForce) {
        for(Block block : cageBlocks) {
            block.setType(Material.AIR);
        }

        for(LivingEntity livingEntity : enemies) {
            livingEntity.remove();
        }

        if(!bruteForce) return;

        floatingChest.remove();
        eventTask.cancel();
    }

    TreasureEvent(Player player, ArrayList<Block> cageBlockList, ArrayList<LivingEntity> enemies, ArmorStand floatingChest) {
        challenger = player;
        cageBlocks = cageBlockList;
        this.enemies = enemies;
        this.floatingChest = floatingChest;

    }
    TreasureEvent(Player player, ArrayList<Block> cageBlockList, ArrayList<LivingEntity> enemies,ArmorStand floatingChest, BukkitTask eventTask) {
        challenger = player;
        cageBlocks = cageBlockList;
        this.enemies = enemies;
        this.floatingChest = floatingChest;
        this.eventTask = eventTask;

    }
}
