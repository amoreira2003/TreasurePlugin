package com.cteam.constructors;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class TreasureEnemy implements ConfigurationSerializable {

    EntityType enemyType;
    int hordeNumber;


    public TreasureEnemy(EntityType enemyType, int hordeNumber) {
        this.enemyType = enemyType;
        this.hordeNumber = hordeNumber;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serializedEnemy = new HashMap<>();
        serializedEnemy.put("enemyType", enemyType.name());
        serializedEnemy.put("hordeNumber", hordeNumber);
        return serializedEnemy;
    }

    public EntityType getEnemyType() {
        return enemyType;
    }

    public void setEnemyType(EntityType enemyType) {
        this.enemyType = enemyType;
    }

    public int getHordeNumber() {
        return hordeNumber;
    }

    public void setHordeNumber(int hordeNumber) {
        this.hordeNumber = hordeNumber;
    }

    public static TreasureEnemy deserialize(Map<String,Object> serializedMap) {
        return new TreasureEnemy(EntityType.valueOf((String) serializedMap.get("enemyType")), (Integer) serializedMap.get("hordeNumber"));
    }
}
