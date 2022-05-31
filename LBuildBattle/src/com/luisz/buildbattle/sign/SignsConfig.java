package com.luisz.buildbattle.sign;

import com.lib576.Lib576;
import com.lib576.libs.LConfig;
import com.luisz.buildbattle.Main;
import com.luisz.buildbattle.game.arena.Arena;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class SignsConfig {

    private final LConfig config;
    private final HashMap<String, SignGame> signs = new HashMap<>();

    public SignsConfig(){
        config = new LConfig("signs", Main.instance);
        _load();
        config.save();
    }

    private void _load(){
        if(config.hasKey("signs_names")) {
            signs.clear();
            List<String> signs_names = (List<String>) config.getList("signs_names");
            for (String signName : signs_names)
                signs.put(signName, loadSign(signName));
        }
    }

    public boolean addSign(String name, String arenaId, Location location){
        name = name.toLowerCase();
        if(signs.containsKey(name)) return false;
        Arena arena = Main.arenaConfigs.getArena(arenaId);
        if(arena != null){
            SignGame signGame = new SignGame(name, arena, location);
            signs.put(name.toLowerCase(), signGame);
            save();
            return true;
        }
        return false;
    }
    public boolean removeSign(String name){
        name = name.toLowerCase();
        SignGame signGame = getSign(name);
        if(signGame != null){
            signs.remove(signGame.id);
            config.remove("signs." + signGame.id);
            save();
            return true;
        }
        return false;
    }
    public SignGame getSign(String name){
        name = name.toLowerCase();
        return signs.get(name);
    }

    private void save(){
        config.setValue("signs_names", new ArrayList<>(signs.keySet()));
        for(SignGame signGame : signs.values()){
            String path = "signs." + signGame.id;
            config.setValue(path + ".arena", signGame.arena.id);
            config.setValue(path + ".location", signGame.signLocation);
        }
        config.save();
    }

    private SignGame loadSign(String name){
        name = name.toLowerCase();
        String path = "signs." + name;
        Arena arena = Main.arenaConfigs.getArena(config.getString(path + ".arena"));
        Location location = config.getLocation(path + ".location");
        return new SignGame(name, arena, location);
    }

    public Collection<SignGame> loadSigns(){
        return signs.values();
    }

}