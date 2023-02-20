package com.cteam.methods;

import com.cteam.main;
import com.cteam.tasks.TreasureEventTask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTables;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;

public class EventManager {

    public HashMap<Player, TreasureEvent> onEvent = new HashMap<>();
    public int runningEvents = 0;



    boolean addPlayerAsChallenger(TreasureEvent treasureEvent, Player challenger) {
      if(isPlayerChallenging(challenger)) return false;
      onEvent.put(challenger, treasureEvent);
      return true;
    }


    boolean isPlayerChallenging(Player challenger) {
        return onEvent.containsKey(challenger);
    }

    public boolean removePlayerAsChallenger(Player challenger) {
        if (!isPlayerChallenging(challenger)) return false;
        onEvent.remove(challenger);
        return true;
    }

    void buildCage(Material cageMaterial, TreasureEvent event, Vector vector) {
        Player challenger = event.getChallenger();
        ArrayList<Block> cellsBars = event.getCageBlocks();
        int xSize = vector.getBlockX();
        int zSize = vector.getBlockZ();
        int ySize = vector.getBlockZ();
        for(int x=-xSize; x <= xSize; x++) {
            for(int z=-zSize;z <=zSize;z++) {
                for(int y=0;y < ySize;y++) {
                    Block block = new Location(challenger.getWorld(),challenger.getLocation().getBlockX()+x,challenger.getLocation().getBlockY()+y,challenger.getLocation().getBlockZ()+z).getBlock();
                    cellsBars.add(block);
                    if(x >= xSize || z >= zSize || x <= -xSize || z<= -zSize) block.setType(cageMaterial);

                }
            }
        }
    }


    ArmorStand spawnArmorStand(Player challenger) {
        Location armorStandLocation = challenger.getLocation();
        ArmorStand armorStand = challenger.getWorld().spawn(armorStandLocation, ArmorStand.class);
        ItemStack skull = Utils.getCustomSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWRjMzZjOWNiNTBhNTI3YWE1NTYwN2EwZGY3MTg1YWQyMGFhYmFhOTAzZThkOWFiZmM3ODI2MDcwNTU0MGRlZiJ9fX0=");
        armorStand.setSmall(true);
        armorStand.setGravity(false);
        armorStand.getEquipment().setHelmet(skull);
        armorStand.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);
        armorStand.setVisible(false);
        armorStand.setCustomNameVisible(true);
        return armorStand;
    }




    public void startEvent(Player challenger) {
        if(isPlayerChallenging(challenger)) return;
        ArmorStand armorStand = spawnArmorStand(challenger);
        ArrayList<Block> cellsBars = new ArrayList<>();
        ArrayList<LivingEntity> spawnEntities = new ArrayList<>();
        TreasureEvent treasureEvent = new TreasureEvent(challenger,cellsBars,spawnEntities,armorStand);
        Vector cageSize = new Vector(10,10,10);
        buildCage(Material.IRON_BARS,treasureEvent,cageSize);

        for(int i = 0; i < 2; i++) {
            spawnEntities.add(challenger.getWorld().spawn(challenger.getLocation(), Zombie.class));
        }

        for(LivingEntity i : spawnEntities) {
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
            i.setCustomName(challenger.getName() +"'s Treasure");
            i.setGlowing(true);
            i.setAI(true);
        }

        BukkitTask event  = new BukkitRunnable() {

            int x = 0;
            double constHeight = armorStand.getLocation().getY();
            boolean triggered = false;
            @Override
            public void run() {

                if(!triggered) armorStand.setCustomName(((double) x/20)+"s");
                if(!Utils.checkIfHordeAlive(spawnEntities) && !triggered || !isPlayerChallenging(challenger)) {
                    triggered= true;
                    treasureEvent.startLootingPhase();
                }
                treasureEvent.rotateFloatingChest(constHeight,x);
                if(!isPlayerChallenging(challenger)) treasureEvent.endEvent();
                x++;
            }
        }.runTaskTimer(main.plugin, 0, 1);
        treasureEvent.setEventTask(event);
        addPlayerAsChallenger(treasureEvent,challenger);
    }
}
