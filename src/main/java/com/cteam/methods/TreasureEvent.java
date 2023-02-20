package com.cteam.methods;

import com.cteam.events.EventStage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class TreasureEvent {

    Player challenger;

    ArmorStand floatingChest;

    EventStage eventPhase;

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

    public void clearStructures() {
        for(Block block : cageBlocks) {
            block.setType(Material.AIR);
        }

        for(LivingEntity livingEntity : enemies) {
            livingEntity.remove();
        }
    }

    TreasureEvent(Player player, ArrayList<Block> cageBlockList, ArrayList<LivingEntity> enemies, ArmorStand floatingChest) {
        challenger = player;
        cageBlocks = cageBlockList;
        this.enemies = enemies;
        this.floatingChest = floatingChest;
        eventPhase = EventStage.STARTED;

    }

    public void startLootingPhase() {
        challenger.playSound(challenger.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,1f);
        challenger.sendMessage(ChatColor.GREEN + "Challenge Won");
        floatingChest.setCustomName("Right Click to Claim Your Prize");
        floatingChest.getEquipment().setHelmet(Utils.getCustomSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTcxNDUxNmU2NTY1YjgxMmZmNWIzOWVhMzljZDI4N2FmZWM4ZmNjMDZkOGYzMDUyMWNjZDhmMWI0Y2JmZGM2YiJ9fX0="));
        clearStructures();
        setEventPhase(EventStage.LOOTING);
    }

    public void startEndingPhase() {
        setEventPhase(EventStage.ENDED);
    }

    public void rotateFloatingChest(double constHeight,  int x) {
        Location newLocation =  floatingChest.getLocation();
        Vector toPlayerDirection = challenger.getLocation().toVector().subtract(newLocation.toVector());
        if(eventPhase == EventStage.LOOTING) {
            constHeight = challenger.getLocation().getY();
            newLocation.setDirection(toPlayerDirection);
            if(newLocation.toVector().distance(challenger.getLocation().toVector()) > 2.5) {
                newLocation.add(toPlayerDirection.normalize().multiply(0.5f));
            }
        } else {
            newLocation.setYaw(newLocation.getYaw()+8);
        }
        newLocation.setY(constHeight + (Math.sin((float) x * 0.2) * 0.3));
        floatingChest.teleport(newLocation);
    }

    public void endEvent() {
        clearStructures();
        floatingChest.remove();
        eventTask.cancel();
    }

    public EventStage getEventPhase() {
        return eventPhase;
    }

    public void setEventPhase(EventStage eventPhase) {
        this.eventPhase = eventPhase;
    }

    TreasureEvent(Player player, ArrayList<Block> cageBlockList, ArrayList<LivingEntity> enemies, ArmorStand floatingChest, BukkitTask eventTask) {
        challenger = player;
        cageBlocks = cageBlockList;
        this.enemies = enemies;
        this.floatingChest = floatingChest;
        eventPhase = EventStage.STARTED;
        this.eventTask = eventTask;

    }
}
