package com.luisz.buildbattle.sign;

import com.lib576.Lib576;
import com.luisz.buildbattle.game.Game;
import com.luisz.buildbattle.game.GameController;
import com.luisz.buildbattle.game.arena.Arena;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;

public class SignGame {

    public final String id;
    public final Arena arena;
    public final Location signLocation;

    public SignGame(String id, Arena arena, Location signLocation){
        this.id = id;
        this.arena = arena;
        this.signLocation = signLocation;
    }

    public void _update(){
        Sign sign = (Sign) signLocation.getBlock().getState();
        Game game = GameController.getGame(arena.id);
        if(game != null){
            switch (game.getGameState()){
                case STARTING:
                    sign.setLine(0, ChatColor.GREEN + "[STARTING]");
                    break;
                case TYPING:
                    sign.setLine(0, ChatColor.YELLOW + "[TYPING]");
                    break;
                case VOTING:
                    sign.setLine(0, ChatColor.YELLOW + "[VOTING]");
                    break;
                case BUILDING:
                    sign.setLine(0, ChatColor.YELLOW + "[BUILDING]");
                    break;
                case STOPPING:
                    sign.setLine(0, ChatColor.RED + "[STOPPING]");
                    break;
            }
            sign.setLine(3, ChatColor.BLACK + "" + game.getPlayers() + "/" + game.getMaxPlayers());
        }else{
            sign.setLine(0, ChatColor.DARK_GRAY + "[CLOSED]");
            sign.setLine(3, ChatColor.BLACK + "0/0");
        }
        sign.setLine(1, ChatColor.DARK_GRAY + "" + arena.id);
        sign.setLine(2, ChatColor.BLACK + "" + id);
        sign.update();
    }

    public static SignGame fromLocation(Location location){
        return SignsController.getSignGame(location);
    }

}