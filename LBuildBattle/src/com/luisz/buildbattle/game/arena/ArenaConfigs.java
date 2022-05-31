package com.luisz.buildbattle.game.arena;

import com.lib576.libs.LConfig;
import com.lib576.utils.Area;
import com.luisz.buildbattle.Main;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class ArenaConfigs {

    private final LConfig config;
    private final List<Arena> arenas = new ArrayList<>();

    public ArenaConfigs(){
        config = new LConfig("arenas", Main.instance);
        _load();
        config.save();
    }

    private void _load(){
        if(config.hasKey("arenas_ids")) {
            List<String> arenas_ids = (List<String>) config.getList("arenas_ids");
            arenas.clear();
            for (String arenaId : arenas_ids)
                arenas.add(loadArena(arenaId));
        }
    }

    public boolean addArena(Arena arena){
        if(getArena(arena.id) == null){
            arenas.add(arena);
            save();
            return true;
        }
        return false;
    }
    public boolean removeArena(String arenaId){
        arenaId = arenaId.toLowerCase();
        Arena arena = getArena(arenaId);
        if(arena != null){
            arenas.remove(arena);
            config.remove("arenas." + arena.id);
            save();
            return true;
        }
        return false;
    }

    private void save(){
        List<String> arenas_ids = new ArrayList<>();
        for(Arena arena : arenas)
            arenas_ids.add(arena.id);
        config.setValue("arenas_ids", arenas_ids);
        for(Arena arena : arenas){
            String path = "arenas." + arena.id;
            config.setValue(path + ".maxp", arena.maxPlayers);
            config.setValue(path + ".minp", arena.minPlayers);
            config.setValue(path + ".spawn", arena.spawn);
            config.setValue(path + ".typinglocation", arena.typingLocation);
            int i = 0;
            for(Arena.BuildLocation buildLocation : arena.buildLocations){
                String thisPath = path + ".buildlocations." + i;
                config.setValue(thisPath + ".spawn", buildLocation.spawn);
                config.setValue(thisPath + ".arena", buildLocation.area);
                i++;
            }
            config.setValue(path + ".buildlocations.size", i);
        }
        config.save();
    }

    private Arena loadArena(String arenaId){
        arenaId = arenaId.toLowerCase();
        String path = "arenas." + arenaId;
        Arena arena = new Arena(arenaId);
        arena.maxPlayers = config.getInt(path + ".maxp");
        arena.minPlayers = config.getInt(path + ".minp");
        arena.spawn = config.getLocation(path + ".spawn");
        arena.typingLocation = config.getLocation(path + ".typinglocation");
        int buildlocationssize = config.getInt(path + ".buildlocations.size");
        for(int i = 0; i < buildlocationssize; i++){
            String thisPath = path + ".buildlocations." + i;
            Location spawn = config.getLocation(thisPath + ".spawn");
            Area area = config.getArea(thisPath + ".arena");
            arena.buildLocations.add(new Arena.BuildLocation(spawn, area));
        }
        return arena;
    }

    public Arena getArena(String arenaId){
        arenaId = arenaId.toLowerCase();
        for(Arena arena : arenas)
            if(arena.id.equalsIgnoreCase(arenaId))
                return arena;
        return null;
    }

}