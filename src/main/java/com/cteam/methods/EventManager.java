package com.cteam.methods;

import com.cteam.TreasureRarity;
import com.cteam.events.EventStage;
import com.cteam.main;
import com.cteam.yamls.Config;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
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
        for(int x=-xSize; x <= xSize; x++) {
            for(int z=-zSize;z <=zSize;z++) {
                for(int y=-10;y < 256;y++) {
                    Block block = new Location(challenger.getWorld(),challenger.getLocation().getBlockX()+x,challenger.getLocation().getBlockY()+y,challenger.getLocation().getBlockZ()+z).getBlock();
                    protectedBlocks.add(block);
                    cellsBars.add(block);
                    if(x >= xSize || z >= zSize || x <= -xSize || z<= -zSize || y == -10) if(!block.getType().isSolid()) block.setType(Material.IRON_BARS);

                }
            }
        }
    }


    ArmorStand spawnArmorStand(Player challenger) {
        Location armorStandLocation = challenger.getLocation().getBlock().getLocation();
        ArmorStand armorStand = challenger.getWorld().spawn(armorStandLocation, ArmorStand.class);

        YamlConfiguration yamlConfiguration = new Config().getConfigYAML();
        ItemStack skull = Utils.getCustomSkull(yamlConfiguration.getString("skins.notAbleToLoot"));
        armorStand.setSmall(true);
        armorStand.setGravity(false);
        armorStand.getEquipment().setHelmet(skull);
        armorStand.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);
        armorStand.setVisible(false);
        armorStand.setCustomNameVisible(true);
        return armorStand;
    }



    public void startEvent(Player challenger, TreasureRarity rarity) {
        if(isPlayerChallenging(challenger)) return;
        ArmorStand armorStand = spawnArmorStand(challenger);
        YamlConfiguration yamlConfiguration = new Config().getConfigYAML();
        TreasureEvent treasureEvent = new TreasureEvent(challenger,armorStand, 5);
        treasureEvent.setTreasureRarity(rarity);
        Vector cageSize = new Vector(yamlConfiguration.getInt("challenges."+rarity.name().toLowerCase()+".cageSize"),10,yamlConfiguration.getInt("challenges."+rarity.name().toLowerCase()+".cageSize"));
        treasureEvent.setMaxHorde(yamlConfiguration.getInt("challenges."+rarity.name().toLowerCase()+".cageSize"));
        buildCage(treasureEvent,cageSize);


        BukkitTask event  = new BukkitRunnable() {

            int x = 0;
            double constHeight = armorStand.getLocation().getY();

            @Override
            public void run() {
                String hordeText = yamlConfiguration.getString("displayText.hordeCountText").replace("{current_horde}",String.valueOf(treasureEvent.getCurrentHorde())).replace("{max_horde}",String.valueOf(treasureEvent.getMaxHorde()));
               if(armorStand.getCustomName() != hordeText && treasureEvent.getEventPhase() == EventStage.STARTED) armorStand.setCustomName(hordeText);
               if(treasureEvent.getEventPhase() == EventStage.WAITING) {
                   if(treasureEvent.currentHorde == 0) {
                       treasureEvent.getFloatingChest().setCustomName(yamlConfiguration.getString("displayText.startingChallengeText").replace("{seconds_until_horde}",String.valueOf(treasureEvent.secondsUntilNextHorde)));
                   } else {
                       treasureEvent.getFloatingChest().setCustomName(yamlConfiguration.getString("displayText.nextHordeText").replace("{seconds_until_horde}", String.valueOf(treasureEvent.secondsUntilNextHorde)));
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
