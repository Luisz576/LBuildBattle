package com.luisz.buildbattle.game.arena;

import com.lib576.utils.Area;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Arena {

    public final String id;
    public int maxPlayers, minPlayers;
    public Location typingLocation, spawn;
    public final List<BuildLocation> buildLocations = new ArrayList<>();

    public Arena(String id){
        this.id = id;
    }

    public Arena cloneWithOtherId(String arenaId){
        arenaId = arenaId.toLowerCase();
        Arena arena = new Arena(arenaId);
        arena.maxPlayers = maxPlayers;
        arena.minPlayers = minPlayers;
        arena.typingLocation = typingLocation;
        arena.spawn = spawn;
        arena.buildLocations.addAll(buildLocations);
        return arena;
    }

    public boolean hasAllInfo(){
        return maxPlayers > 0 && minPlayers > 0 && typingLocation != null && spawn != null && buildLocations.size() >= maxPlayers;
    }

    public static class BuildLocation{
        public final Location spawn;
        public final Area area;
        public BuildLocation(Location spawn, Area area){
            this.spawn = spawn;
            this.area = area;
        }
        public boolean isInside(Location location){
            return area.isInside(location);
        }
    }

}