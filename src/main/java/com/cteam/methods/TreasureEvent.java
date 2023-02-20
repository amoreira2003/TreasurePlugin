package com.cteam.methods;

import com.cteam.events.EventStage;
import com.cteam.main;
import com.cteam.yamls.Config;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.loot.LootTables;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TreasureEvent {

    private Player challenger;

     private ArmorStand floatingChest;

     private EventStage eventPhase;

     private ArrayList<Block> cageBlocks;

     private ArrayList<LivingEntity> enemies;

     private BukkitTask eventTask;

    int currentHorde = 0;
    int maxHorde = 0;

    int secondsUntilNextHorde = 0;

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
            main.plugin.eventManager.protectedBlocks.remove(block);
            if(block.getType() == Material.IRON_BARS) block.setType(Material.AIR);
        }

        for(LivingEntity livingEntity : enemies) {
            livingEntity.remove();
        }
    }

    TreasureEvent(Player player, ArmorStand floatingChest,int maxHorde) {
        challenger = player;
        cageBlocks = new ArrayList<Block>();;
        this.enemies = new ArrayList<LivingEntity>();;
        this.maxHorde = maxHorde;
        this.floatingChest = floatingChest;
        eventPhase = EventStage.STARTED;

    }

    TreasureEvent(Player player, ArrayList<Block> cageBlockList, ArrayList<LivingEntity> enemies, ArmorStand floatingChest, BukkitTask eventTask,int maxHorde) {
        challenger = player;
        cageBlocks = cageBlockList;
        this.enemies = enemies;
        this.floatingChest = floatingChest;
        eventPhase = EventStage.STARTED;
        this.maxHorde = maxHorde;
        this.eventTask = eventTask;

    }

    public void startLootingPhase() {
        Config config = new Config();
        YamlConfiguration yamlConfiguration = config.getConfigYAML();
        challenger.playSound(challenger.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,1f);
        challenger.sendMessage(yamlConfiguration.getString("challengeWin"));
        floatingChest.setCustomName(yamlConfiguration.getString("challengeWinChestName"));
        floatingChest.getEquipment().setHelmet(Utils.getCustomSkull(yamlConfiguration.getString("ableToLoot")));
        clearStructures();
        setEventPhase(EventStage.LOOTING);
    }

    public int getCurrentHorde() {
        return currentHorde;
    }

    public void setCurrentHorde(int currentHorde) {
        this.currentHorde = currentHorde;
    }

    public int getMaxHorde() {
        return maxHorde;
    }

    public void setMaxHorde(int maxHorde) {
        this.maxHorde = maxHorde;
    }

    public void startEndingPhase() {
        setEventPhase(EventStage.ENDED);
        endEvent();
    }

    void spawnEnemies(int currentHorde) {
        YamlConfiguration yamlConfiguration = new Config().getConfigYAML();
        enemies.clear();
        for(int i = 0; i < 5 * currentHorde; i++) {
            enemies.add(challenger.getWorld().spawn(challenger.getLocation(), Zombie.class));
        }

        for(LivingEntity i : enemies) {
            if(i instanceof Zombie) {
                Zombie z = (Zombie) i;
                z.getEquipment().clear();
                z.setLootTable(LootTables.EMPTY.getLootTable());
                z.setAdult();
                z.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 10000, 10000));
            }

            i.setCanPickupItems(false);
            i.setRemoveWhenFarAway(false);
            i.setVisualFire(false);
            ArrayList<String> names = (ArrayList<String>) yamlConfiguration.get("enemiesListName");
            String randomName = names.get(new Random().nextInt(names.size()));
            i.setCustomName(randomName);
            i.setGlowing(true);
            i.setAI(true);
        }
    }
    public void nextPhase() {

        if(currentHorde >= maxHorde)  {
            startLootingPhase();
            return;
        }

        startHorde(10);

    }


    public void startHorde(int delay) {
        challenger.sendMessage(ChatColor.GREEN +""+ currentHorde + " of " + maxHorde + " completed");
        setEventPhase(EventStage.WAITING);
        new BukkitRunnable() {
            int x = 0;
            @Override
            public void run() {
                x++;
                secondsUntilNextHorde = delay - x;

                if(secondsUntilNextHorde <= 0) {
                    if(!main.plugin.eventManager.isPlayerChallenging(challenger)) {
                        cancel();
                        return;
                    }
                    setCurrentHorde(currentHorde+1);
                    spawnEnemies(currentHorde);
                    setEventPhase(EventStage.STARTED);
                    cancel();
                }
            }
        }.runTaskTimer(main.plugin, 0, 20);
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
        if(main.plugin.eventManager.isPlayerChallenging(challenger)) main.plugin.eventManager.removePlayerAsChallenger(challenger);
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


}
