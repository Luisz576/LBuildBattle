package com.luisz.buildbattle.building;

import com.lib576.utils.Area;
import com.luisz.buildbattle.game.arena.Arena;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class BuildingVars {

    private static final HashMap<Player, Location> left = new HashMap<>(), right = new HashMap<>();
    public static void setLeft(Player p, Location location){
        left.put(p, location);
    }
    public static void setRight(Player p, Location location){
        right.put(p, location);
    }
    public static boolean hasLeft(Player p){
        return left.containsKey(p);
    }
    public static boolean hasRight(Player p){
        return right.containsKey(p);
    }
    public static Location getLeft(Player p){
        return left.get(p);
    }
    public static Location getRight(Player p){
        return right.get(p);
    }
    public static Area getAreaFromLeftAndRight(Player p){
        if(hasLeft(p) && hasRight(p))
            return new Area(left.get(p), right.get(p));
        return null;
    }

    private static final HashMap<Player, Arena> arenaBuilding = new HashMap<>();
    public static boolean isNotEditingArena(Player p){
        return !arenaBuilding.containsKey(p);
    }
    public static void setEditingArena(Player p, String arenaId){
        arenaBuilding.put(p, new Arena(arenaId));
    }
    public static Arena getEditingArena(Player p){
        return arenaBuilding.get(p);
    }
    public static int addBuildLocation(Player p, Location spawn){
        Arena arena = arenaBuilding.get(p);
        Area area = getAreaFromLeftAndRight(p);
        if(area == null) return 1;
        if(!area.isInside(spawn)) return 2;
        arena.buildLocations.add(new Arena.BuildLocation(spawn, area));
        arenaBuilding.put(p, arena);
        return 3;
    }
    public static boolean setTypingArea(Player p, Location location){
        Arena arena = arenaBuilding.get(p);
        if(arena == null) return false;
        arena.typingLocation = location;
        arenaBuilding.put(p, arena);
        return true;
    }
    public static boolean changeArenaId(Player p, String arenaId){
        arenaId = arenaId.toLowerCase();
        Arena oldArena = getEditingArena(p);
        if(oldArena != null) {
            arenaBuilding.put(p, oldArena.cloneWithOtherId(arenaId));
            return true;
        }
        return false;
    }

}