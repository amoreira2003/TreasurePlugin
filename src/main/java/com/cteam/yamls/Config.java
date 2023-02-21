package com.cteam.yamls;

import com.cteam.main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class Config {

    File configFile;
    YamlConfiguration configYAML;

    public Config() {
        configFile = new File(main.plugin.getDataFolder(), "config.yml");
        if(!configFile.exists()) {
            try {
                configFile.mkdir();
                configFile.createNewFile();
                configFile.setWritable(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String[] header = {"Hello my fellow user, in this YAML are some generals configurations for the cTeam Treasure Plugin \n",
"If you ain't sure how to edit this file, or what variable do, please check out the documentation at https://cTeam.com/docs/TreasurePlugin",
"That's All, Hope you like it - cTeam AKA Best or Most Friendly's, ok Charming, nah anh ok Coolest Developers Team :3\n",
"Oh yeah if you are a developer trying using our sample plugin, you might wanna check out the documentation for development, it explains what each part of the code do.",
"Check it at https://cTeam.com/docs/TreasurePlugin/devs, and have a cup of couffé mate, ya",
"I do speak french, oui oui",
};
        configYAML = YamlConfiguration.loadConfiguration(configFile);
        configYAML.options().setHeader(Arrays.asList(header));
        String[] randomNames = {"StampyLongHead", "CaptainSparklez", "PewDiePie", "Skeppy", "Dream", "Technoblade", "Grian", "TommyInnit", "Tubbo", "WilburSoot"};
        checkField("notAbleToLoot", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWRjMzZjOWNiNTBhNTI3YWE1NTYwN2EwZGY3MTg1YWQyMGFhYmFhOTAzZThkOWFiZmM3ODI2MDcwNTU0MGRlZiJ9fX0=");
        checkField("challengeWin", ChatColor.GREEN + "Challenge Won");
        checkField("challengeWinChestName", "Right Click to Claim Your Prize");
        checkField("ableToLoot", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTcxNDUxNmU2NTY1YjgxMmZmNWIzOWVhMzljZDI4N2FmZWM4ZmNjMDZkOGYzMDUyMWNjZDhmMWI0Y2JmZGM2YiJ9fX0=");
        checkField("enemiesListName", randomNames);
        checkField("hordeCountText","Horde {current_horde}/{max_horde}");
        checkField("startingChallengeText",ChatColor.GREEN + "Starting Challenge in {seconds_until_horde}s");
        checkField("nextHordeText",ChatColor.RED + "Next Horde in {seconds_until_horde}s");
        saveConfig();


    }



    void checkField(String fieldName, Object defaultValue) {
        if(!configYAML.contains(fieldName)) configYAML.set(fieldName, defaultValue);
    }

    public YamlConfiguration getConfigYAML() {
        return configYAML;
    }

    public boolean saveConfig() {
        try {
            configYAML.save(configFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
