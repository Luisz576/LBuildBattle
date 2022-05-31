package com.luisz.buildbattle.sign;

import com.lib576.Lib576;
import com.luisz.buildbattle.game.arena.Arena;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class SignsController {

    private static final List<SignGame> signsGames = new ArrayList<>();
    private static SignsConfig signsConfig = null;

    public static void _start(){
        if(signsConfig == null)
            signsConfig = new SignsConfig();
        signsGames.clear();
        signsGames.addAll(signsConfig.loadSigns());
        Lib576.sc.scheduleSyncRepeatingTask(Lib576.getInstance(), () -> {
            //update signs
            for(SignGame signGame : signsGames)
                signGame._update();
        }, 0, 40);
    }

    public static boolean _register(String name, Arena arena, Location location){
        if(signsConfig == null)
            signsConfig = new SignsConfig();
        boolean a = signsConfig.addSign(name, arena.id, location);
        if(a)
            signsGames.add(new SignGame(name, arena, location));
        return a;
    }

    public static SignGame getSignGame(Location location){
        for(SignGame signGame : signsGames)
            if(signGame.signLocation.getBlockX() == location.getBlockX() &&
                signGame.signLocation.getBlockY() == location.getBlockY() &&
                signGame.signLocation.getBlockZ() == location.getBlockZ() &&
                signGame.signLocation.getWorld() == location.getWorld())
                return signGame;
        return null;
    }

    public static SignGame getSignGame(String signGameId){
        for(SignGame sign : signsGames)
            if(sign.id.equalsIgnoreCase(signGameId))
                return sign;
        return null;
    }

    public static boolean hasSignGame(String signGameId){
        for(SignGame sign : signsGames)
            if(sign.id.equalsIgnoreCase(signGameId))
                return true;
        return false;
    }

    public static boolean _unregister(SignGame signGame){
        if(signsConfig == null)
            signsConfig = new SignsConfig();
        boolean a = signsConfig.removeSign(signGame.id);
        if(a)
            signsGames.remove(signGame);
        return a;
    }

}