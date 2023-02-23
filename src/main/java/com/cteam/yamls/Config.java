package com.cteam.yamls;

import com.cteam.constructors.TreasureEnemy;
import com.cteam.main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Config {

    File configFile;
    YamlConfiguration configYAML;

    public Config() {
        configFile = new File(main.plugin.getDataFolder(), "config.yml");
        if(!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String[] header = {"Hello my fellow user, in this YAML are some generals configurations for the cTeam Treasure Plugin \n",
"If you ain't sure how to edit this file, or what variable do, please check out the documentation at https://cTeam.com/docs/TreasurePlugin",
"That's All, Hope you like it - cTeam AKA Best or Most Friendly's, ok Charming, nah anh ok Coolest Developers Team :3\n",
"Oh yeah if you are a developer trying using our sample plugin, you might wanna check out the documentation for development, it explains what each part of the code do.",
"Check it at https://cTeam.com/docs/TreasurePlugin/devs, and have a cup of couffé mate, ya",
"I do speak french, oui oui","","§0: Black §1: Dark Blue §2: Dark Green §3: Dark Aqua §4: Dark Red §5: Dark Purple §6: Gold §7: Gray §8: Dark Gray §9: Blue §a: Green §b: Aqua §c: Red §d: Light Purple §e: Yellow §f: White §k: Obfuscated §l: Bold §m: Strikethrough §n: Underline §o: Italic §r: Reset"
};
        configYAML = YamlConfiguration.loadConfiguration(configFile);
        configYAML.options().setHeader(Arrays.asList(header));
        String[] randomNames = {"StampyLongHead", "CaptainSparklez", "PewDiePie", "Skeppy", "Dream", "Technoblade", "Grian", "TommyInnit", "Tubbo", "WilburSoot",
                "LuzuGames", "vegetta777", "Willyrex", "Herobrine", "elRichMC", "TBNRFrags", "xNestorio", "PhoenixSC", "Cubfan135", "OMGcraft",
                "Aphmau", "iBallisticSquid", "LDShadowLady", "BajanCanadian", "Thinknoodles", "DanTDM", "Solidarity", "Falsesymmetry", "Stressmonster101", "Joey Graceffa",
                "Slogoman", "SmallishBeans", "Grian", "Mumbo Jumbo", "Welsknight", "Hypno", "EthosLab", "xisumavoid", "Cubehamster", "False",
                "AuthenticGames", "Jazzghost", "Coisa de Nerd"};


        String[] bookLines = {
                "Ahoy, me mateys! Listen well, for I have a tale to share. The treasure be found at X:{treasureCoordinates_X}, Z:{treasureCoordinates_Z}. Beware, for the path to riches is perilous, with traps and enemies aplenty. Only the bravest pirates can claim the prize. Rarity: {treasureRarity}, prepare for a challenge!",
                "Avast ye! A treasure be at X:{treasureCoordinates_X}, Z:{treasureCoordinates_Z}, but the road to riches is fraught with danger. Ye must face many challenges, from treacherous traps to fierce foes. Only the smartest pirates stand a chance. Rarity: {treasureRarity}, hoist the Jolly Roger and set sail for adventure!",
                "Shiver me timbers! A treasure has been found at X:{treasureCoordinates_X}, Z:{treasureCoordinates_Z}. Beware, for the location be guarded by fearsome beasts and cunning traps. Only the most skilled pirates can claim the treasure. Rarity: {treasureRarity}, weigh anchor and prepare for the dangers of the high seas!",
                "Yo ho ho! A treasure awaits at X:{treasureCoordinates_X}, Z:{treasureCoordinates_Z}. But take heed, the path to riches is perilous. Deadly traps and savage foes lie in wait. Only the most daring pirates can brave the dangers and claim the loot. Rarity: {treasureRarity}, batten down the hatches and get ready for adventure!"
        };

        String[] bookDesc =   {"", ChatColor.AQUA + "" + ChatColor.BOLD + "Right click to read the message","", ChatColor.GRAY +  "Treasure Rarity: {treasureRarity}", ""};

        TreasureEnemy treasureEnemy = new TreasureEnemy(EntityType.ZOMBIE,5);
        List<Map<String,Object>> enemyList = new ArrayList<>();
        List<Map<String,Object>> lootDrop = new ArrayList<>();
        lootDrop.add(new ItemStack(Material.DIAMOND).serialize());

        enemyList.add(treasureEnemy.serialize());

        checkField("animation.floatingRotationSpeed", 8.0);
        checkField("animation.floatingAmplitude", 0.3);
        checkField("animation.floatingSpeed", 0.2);
        checkField("animation.followSpeed", 0.5);
        checkField("animation.followDistance", 2.5);

        checkField("skins.notAbleToLoot", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWRjMzZjOWNiNTBhNTI3YWE1NTYwN2EwZGY3MTg1YWQyMGFhYmFhOTAzZThkOWFiZmM3ODI2MDcwNTU0MGRlZiJ9fX0=");
        checkField("skins.ableToLoot", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTcxNDUxNmU2NTY1YjgxMmZmNWIzOWVhMzljZDI4N2FmZWM4ZmNjMDZkOGYzMDUyMWNjZDhmMWI0Y2JmZGM2YiJ9fX0=");

        checkField("chatMessages.challengeWin", ChatColor.GREEN + "Challenge Won");
        checkField("chatMessages.hordeComplete", ChatColor.GREEN +"{current_horde} of {max_horde} completed");

        checkField("extra.useCustomNameForEnemies", false);
        checkField("extra.enemiesListName", randomNames);
        checkField("book.introText", bookLines);
        checkField("book.author","Treasure Book");
        checkField("book.title","Find the Treasure");
        checkField("book.displayName","Treasure Book");


        checkField("displayText.challengeWinChestName", "Right Click to Claim Your Prize");
        checkField("displayText.hordeCountText","Horde {current_horde}/{max_horde}");
        checkField("displayText.startingChallengeText",ChatColor.GREEN + "Starting Challenge in {seconds_until_horde}s");
        checkField("displayText.nextHordeText",ChatColor.RED + "Next Horde in {seconds_until_horde}s");


        checkField("book.desc", bookDesc);
        checkField("book.generator.xMaxCords", 10000);
        checkField("book.generator.zMaxCords", 10000);
        checkField("book.generator.xMinCords", 1000);
        checkField("book.generator.zMinCords", 1000);

        checkField("error.noPerm", ChatColor.GRAY + "You don't have enough" + ChatColor.RED + ChatColor.BOLD + "PERMISSION");
        checkField("error.notChallengingSelf",ChatColor.GRAY + "You are not in a" + ChatColor.RED + ChatColor.BOLD +" CHALLENGE");
        checkField("error.notChallengingTarget",ChatColor.GRAY + "{player_target} isn't in a" + ChatColor.RED + ChatColor.BOLD +" CHALLENGE");
        checkField("error.playerNotFound", ChatColor.GRAY + "{player_target} was not " + ChatColor.RED + ChatColor.BOLD +" FOUND");
        checkField("error.alreadyInChallenge","\n"+ ChatColor.RED +"You are already doing a challenge \n ");
        checkField("success.challengeCancelSelf", ChatColor.GRAY + "The challenge was" + ChatColor.GREEN + ChatColor.BOLD + " CANCELED");
        checkField("success.challengeCancelTarget", ChatColor.GRAY + "{player_target} challenge was" + ChatColor.GREEN + ChatColor.BOLD + " CANCELED");


        checkField("challenges.normal.cageSize", 15);
        checkField("challenges.normal.maxHorde", 3);
        checkField("challenges.normal.enemies", enemyList);
        checkField("challenges.normal.hordeWait", 10);
        checkField("challenges.normal.lootDrop", lootDrop);

        checkField("challenges.rare.cageSize", 15);
        checkField("challenges.rare.maxHorde", 10);
        checkField("challenges.rare.enemies", enemyList);
        checkField("challenges.rare.hordeWait", 10);
        checkField("challenges.rare.lootDrop", lootDrop);

        checkField("challenges.epic.cageSize", 8);
        checkField("challenges.epic.maxHorde", 8);
        checkField("challenges.epic.enemies", enemyList);
        checkField("challenges.epic.hordeWait", 10);
        checkField("challenges.epic.lootDrop", lootDrop);

        checkField("challenges.legendary.cageSize", 5);
        checkField("challenges.legendary.maxHorde", 12);
        checkField("challenges.legendary.enemies", enemyList);
        checkField("challenges.legendary.hordeWait", 10);
        checkField("challenges.legendary.lootDrop", lootDrop);

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
