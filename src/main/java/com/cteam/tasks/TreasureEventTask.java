package com.cteam.tasks;

import com.cteam.main;
import com.cteam.methods.TreasureEvent;
import com.cteam.methods.Utils;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class TreasureEventTask extends BukkitRunnable {

    private ArmorStand armorStand;

    double constHeight;

    private Player challenger;
    boolean triggered = false;
    int x = 0;

    public ArrayList<LivingEntity> spawnEntities;

    TreasureEvent treasureEvent;

    public TreasureEventTask(TreasureEvent treasureEvent) {
        armorStand = treasureEvent.getFloatingChest();
        spawnEntities = treasureEvent.getEnemies();
        constHeight = armorStand.getLocation().getY();
        this.treasureEvent = treasureEvent;
    }
    @Override
    public void run() {


    }
}
