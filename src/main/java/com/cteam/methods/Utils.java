package com.cteam.methods;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;

public class Utils {

    public static ItemStack getCustomSkull(String texture) {
        ItemStack customSkull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) customSkull.getItemMeta();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(),null);
        gameProfile.getProperties().put("textures", new Property("texture",texture));

        try {
           Field profileField = skullMeta.getClass().getDeclaredField("profile");
           profileField.setAccessible(true);
           profileField.set(skullMeta,gameProfile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        customSkull.setItemMeta(skullMeta);
        return customSkull;
    }




    public static boolean checkIfHordeAlive(ArrayList<LivingEntity> zombieArrayList) {
        int aliveZombies = 0;
        for(LivingEntity LivingEntity : zombieArrayList) {
            if(LivingEntity.getHealth() > 0) {
                aliveZombies++;
            }
        }
        return aliveZombies > 0;
    }
}
