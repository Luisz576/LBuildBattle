package com.luisz.buildbattle.game;

import com.luisz.buildbattle.Main;
import com.luisz.buildbattle.game.arena.Arena;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameController {

    private static final List<Game> games = new ArrayList<>();

    public static void _disable(){
        for(Game game : games)
            game._stop();
    }

    public static int startGame(String arenaId){
        Arena arena = Main.arenaConfigs.getArena(arenaId);
        if(isArenaLoaded(arenaId)) return 1; //Arena already loaded
        if(arena == null) return 2; //Arena not founded
        Game game = new Game(arena);
        games.add(game);
        return 3;//Game started
    }

    public static boolean stopGame(String arenaId){
        for(Game game : games)
            if(game.getArenaId().equalsIgnoreCase(arenaId)) {
                game._stop();
                return true;
            }
        return false;
    }

    public static void _removeGame(String arenaId){
        Game gameToDelete = null;
        for(Game game : games)
            if(game.getArenaId().equalsIgnoreCase(arenaId))
                gameToDelete = game;
        games.remove(gameToDelete);
    }

    public static boolean isArenaLoaded(String arenaId){
        for(Game game : games)
            if(game.getArenaId().equalsIgnoreCase(arenaId))
                return true;
        return false;
    }

    public static boolean joinInGame(String id, Player p){
        for(Game game : games)
            if(game.getArenaId().equalsIgnoreCase(id)) {
                game.joinPlayer(p);
                return true;
            }
        return false;
    }

    public static Game getGame(String arenaId){
        for(Game game : games)
            if(game.getArenaId().equalsIgnoreCase(arenaId))
                return game;
        return null;
    }

}