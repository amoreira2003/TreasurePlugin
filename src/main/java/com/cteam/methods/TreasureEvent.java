package com.cteam.methods;

import com.cteam.TreasureRarity;
import com.cteam.constructors.TreasureEnemy;
import com.cteam.events.EventStage;
import com.cteam.main;
import com.cteam.yamls.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTables;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TreasureEvent {

    private Player challenger;

    private Inventory lootInventory;

    private ArmorStand floatingChest;

    private EventStage eventPhase;

    private ArrayList<Block> cageBlocks;

    private ArrayList<LivingEntity> enemies;

    private BukkitTask eventTask;

    private TreasureRarity treasureRarity;


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

    public TreasureRarity getTreasureRarity() {
        return treasureRarity;
    }

    public void setTreasureRarity(TreasureRarity treasureRarity) {
        this.treasureRarity = treasureRarity;
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
        for (Block block : cageBlocks) {
            main.plugin.eventManager.protectedBlocks.remove(block);
            if (block.getType() == Material.IRON_BARS) block.setType(Material.AIR);
        }

        for (LivingEntity livingEntity : enemies) {
            livingEntity.remove();
        }
    }

    TreasureEvent(Player player, ArmorStand floatingChest, int maxHorde) {
        challenger = player;
        cageBlocks = new ArrayList<>();;
        this.enemies = new ArrayList<>();;
        this.maxHorde = maxHorde;
        this.floatingChest = floatingChest;
        eventPhase = EventStage.STARTED;

    }

    public Inventory getLootInventory() {
        return lootInventory;
    }

    public void setLootInventory(Inventory lootInventory) {
        this.lootInventory = lootInventory;
    }

    TreasureEvent(Player player, ArrayList<Block> cageBlockList, ArrayList<LivingEntity> enemies, ArmorStand floatingChest, BukkitTask eventTask, int maxHorde) {
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
        challenger.playSound(challenger.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        challenger.sendMessage(yamlConfiguration.getString("chatMessages.challengeWin"));
        floatingChest.setCustomName(yamlConfiguration.getString("displayText.challengeWinChestName"));
        floatingChest.getEquipment().setHelmet(Utils.getCustomSkull(yamlConfiguration.getString("skins.ableToLoot")));
        List<Map<?, ?>> listLootDropSerialized = yamlConfiguration.getMapList("challenges."+ treasureRarity.name().toLowerCase()+".lootDrop");
        lootInventory = Bukkit.createInventory(challenger,54, "Take your reward");
        for(Map<?,?> lootDropSerialized : listLootDropSerialized) {
            Map<String,Object> lootDropUnserialized = (Map<String, Object>) lootDropSerialized;
            ItemStack item = ItemStack.deserialize(lootDropUnserialized);
            lootInventory.addItem(item);
        }

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

        for(Map<?,?> enemyBeforeDeserialization : yamlConfiguration.getMapList("challenges." + treasureRarity.name().toLowerCase() + ".enemies")) {
            challenger.sendMessage(enemyBeforeDeserialization.toString());
            Map<String, Object> enemyMapOPP = (Map<String, Object>) enemyBeforeDeserialization;
            TreasureEnemy enemy = TreasureEnemy.deserialize(enemyMapOPP);
            Location spawnLocation = floatingChest.getLocation().add(0,3,0);
            for(int i = 0; i < enemy.getHordeNumber() * currentHorde; i++) {
                enemies.add((LivingEntity) challenger.getWorld().spawnEntity(spawnLocation, enemy.getEnemyType()));
            }
        }

        for (LivingEntity i : enemies) {
            if (i instanceof Zombie) {
                Zombie z = (Zombie) i;
                z.getEquipment().clear();
                z.setLootTable(LootTables.EMPTY.getLootTable());
                z.setAdult();
                z.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 10000, 10000));
            }

            i.setCanPickupItems(false);
            i.setRemoveWhenFarAway(false);
            i.setVisualFire(false);
            i.setGlowing(true);
            i.setAI(true);
            if (!yamlConfiguration.getBoolean("extra.useCustomNameForEnemies")) continue;
            ArrayList<String> names = (ArrayList<String>) yamlConfiguration.get("extra.enemiesListName");
            String randomName = names.get(new Random().nextInt(names.size()));
            i.setCustomName(randomName);

        }
    }

    public void nextPhase() {
        YamlConfiguration yamlConfiguration = new Config().getConfigYAML();
        if (currentHorde >= maxHorde) {
            startLootingPhase();
            return;
        }

        startHorde(yamlConfiguration.getInt("challenges." + treasureRarity.name().toLowerCase()+ ".hordeWait"));

    }


    public void startHorde(int delay) {
        YamlConfiguration yamlConfiguration = new Config().getConfigYAML();
        challenger.sendMessage(yamlConfiguration.getString("chatMessages.hordeComplete").replace("{current_horde}", String.valueOf(currentHorde)).replace("{max_horde}", String.valueOf(maxHorde)));
        setEventPhase(EventStage.WAITING);
        new BukkitRunnable() {
            int x = 0;

            @Override
            public void run() {
                x++;
                secondsUntilNextHorde = delay - x;

                if (secondsUntilNextHorde <= 0) {
                    if (!main.plugin.eventManager.isPlayerChallenging(challenger)) {
                        cancel();
                        return;
                    }
                    setCurrentHorde(currentHorde + 1);
                    spawnEnemies(currentHorde);
                    setEventPhase(EventStage.STARTED);
                    cancel();
                }
            }
        }.runTaskTimer(main.plugin, 0, 20);
    }


    public void rotateFloatingChest(double constHeight, int x) {
        YamlConfiguration yamlConfiguration = new Config().getConfigYAML();
        Location newLocation = floatingChest.getLocation();
        Vector toPlayerDirection = challenger.getLocation().toVector().subtract(newLocation.toVector());
        if (eventPhase == EventStage.LOOTING) {
            constHeight = challenger.getLocation().getY();
            newLocation.setDirection(toPlayerDirection);
            if (newLocation.toVector().distance(challenger.getLocation().toVector()) > yamlConfiguration.getDouble("animation.followDistance")) {
                newLocation.add(toPlayerDirection.normalize().multiply(yamlConfiguration.getDouble("animation.followSpeed")));
            }
        } else {
            newLocation.setYaw( newLocation.getYaw() + (float) yamlConfiguration.getDouble("animation.floatingRotationSpeed"));
        }
        newLocation.setY(constHeight + (Math.sin((float) x * yamlConfiguration.getDouble("animation.floatingSpeed")) * yamlConfiguration.getDouble("animation.floatingAmplitude")));
        floatingChest.teleport(newLocation);
    }

    public void endEvent() {
        if (main.plugin.eventManager.isPlayerChallenging(challenger))
            main.plugin.eventManager.removePlayerAsChallenger(challenger);
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
