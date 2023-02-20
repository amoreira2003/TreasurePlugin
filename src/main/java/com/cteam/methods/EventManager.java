package com.cteam.methods;

import com.cteam.events.EventStage;
import com.cteam.main;
import com.cteam.tasks.TreasureEventTask;
import com.cteam.yamls.Config;
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
import java.util.Set;

public class EventManager {

     HashMap<Player, TreasureEvent> onEvent = new HashMap<>();
     public ArrayList<Block> protectedBlocks = new ArrayList<Block>();
    public ArrayList<LivingEntity> protectedEnemies = new ArrayList<LivingEntity>();

    boolean addPlayerAsChallenger(TreasureEvent treasureEvent, Player challenger) {
      if(isPlayerChallenging(challenger)) return false;
      onEvent.put(challenger, treasureEvent);
      return true;
    }




    public Set<Player> getPlayersInChallenge() {
        return onEvent.keySet();
    }
    public TreasureEvent getTreasureEvent(Player challenger) {
        return onEvent.get(challenger);
    }


    public boolean isPlayerChallenging(Player challenger) {
        return onEvent.containsKey(challenger);
    }

    public boolean removePlayerAsChallenger(Player challenger) {
        if (!isPlayerChallenging(challenger)) return false;
        onEvent.remove(challenger);
        return true;
    }

    void buildCage(TreasureEvent event, Vector vector) {
        Player challenger = event.getChallenger();
        ArrayList<Block> cellsBars = event.getCageBlocks();
        int xSize = vector.getBlockX();
        int zSize = vector.getBlockZ();
        int ySize = vector.getBlockZ();
        for(int x=-xSize; x <= xSize; x++) {
            for(int z=-zSize;z <=zSize;z++) {
                for(int y=-10;y < ySize;y++) {
                    Block block = new Location(challenger.getWorld(),challenger.getLocation().getBlockX()+x,challenger.getLocation().getBlockY()+y,challenger.getLocation().getBlockZ()+z).getBlock();
                    protectedBlocks.add(block);
                    cellsBars.add(block);
                    if(x >= xSize || z >= zSize || x <= -xSize || z<= -zSize || y == -10) if(!block.getType().isSolid()) block.setType(Material.IRON_BARS);

                }
            }
        }
    }


    ArmorStand spawnArmorStand(Player challenger) {
        Location armorStandLocation = challenger.getLocation();
        ArmorStand armorStand = challenger.getWorld().spawn(armorStandLocation, ArmorStand.class);

        Config config = new Config();
        ItemStack skull = Utils.getCustomSkull(config.getConfigYAML().getString("notAbleToLoot"));
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
        TreasureEvent treasureEvent = new TreasureEvent(challenger,armorStand, 5);
        Vector cageSize = new Vector(10,10,10);
        buildCage(treasureEvent,cageSize);

        BukkitTask event  = new BukkitRunnable() {

            int x = 0;
            double constHeight = armorStand.getLocation().getY();
            boolean triggered = false;
            @Override
            public void run() {
               if(armorStand.getCustomName() != "Horde " + treasureEvent.getCurrentHorde() +"/" + treasureEvent.getMaxHorde() && treasureEvent.getEventPhase() == EventStage.STARTED) armorStand.setCustomName("Horde " + treasureEvent.getCurrentHorde() +"/" + treasureEvent.getMaxHorde());
               if(treasureEvent.getEventPhase() == EventStage.WAITING) {
                   if(treasureEvent.currentHorde == 0) {
                       treasureEvent.getFloatingChest().setCustomName(ChatColor.GREEN + "Starting Challenge in " + treasureEvent.secondsUntilNextHorde +"s");
                   } else {
                       treasureEvent.getFloatingChest().setCustomName(ChatColor.RED + "Next Horde in " + treasureEvent.secondsUntilNextHorde +"s");
                   }
               }
                if(!Utils.checkIfHordeAlive(treasureEvent.getEnemies())  && treasureEvent.getEventPhase() == EventStage.STARTED) {
                    treasureEvent.nextPhase();
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
