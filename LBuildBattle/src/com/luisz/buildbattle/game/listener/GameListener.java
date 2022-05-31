package com.luisz.buildbattle.game.listener;

import com.lib576.utils.LConvert;
import com.luisz.buildbattle.game.Game;
import com.luisz.buildbattle.game.arena.Arena;
import com.luisz.buildbattle.game.common.GameState;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class GameListener implements Listener {

    private final Game game;

    public GameListener(Game game){
        this.game = game;
    }

    @EventHandler
    public void onPlayerType(AsyncPlayerChatEvent e){
        if(game.isPlayer(e.getPlayer())){
            if(game.getGameState() == GameState.TYPING) {
                if (game.playerPrecisaMandarTema(e.getPlayer())) {
                    e.setCancelled(true);
                    String tema = e.getMessage();
                    if (tema.length() > 2 && tema.length() < 16) {
                        game.playerMandaTema(e.getPlayer(), tema);
                        e.getPlayer().sendMessage(ChatColor.GREEN + "Tema recebido!");
                    }else
                        e.getPlayer().sendMessage(ChatColor.RED + "Tema inválido!");
                }
            }else {
                if (game.needVote(e.getPlayer())) {
                    e.setCancelled(true);
                    String notaS = e.getMessage();
                    int nota = LConvert.convertToInteger(notaS);
                    if (nota >= 1 && nota <= 5) {
                        if (game.vote(e.getPlayer(), nota)) {
                            e.getPlayer().sendMessage(ChatColor.GREEN + "Voto recebido");
                        }
                    } else
                        e.getPlayer().sendMessage(ChatColor.RED + "Informe um valor válido!");
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        Player p = e.getPlayer();
        if(game.isEspectador(p)){
            e.setCancelled(true);
            return;
        }
        if(game.isPlayer(p)){
            e.setCancelled(true);
            if(game.getGameState() == GameState.BUILDING){
                Arena.BuildLocation buildLocation = game.getPlayerBuildLocation(p);
                if(buildLocation != null)
                    if(buildLocation.isInside(e.getBlock().getLocation()))
                        e.setCancelled(false);
            }
        }
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent e){
        Player p = e.getPlayer();
        if(game.isEspectador(p)){
            e.setCancelled(true);
            return;
        }
        if(game.isPlayer(p)){
            e.setCancelled(true);
            if(game.getGameState() == GameState.BUILDING){
                Arena.BuildLocation buildLocation = game.getPlayerBuildLocation(p);
                if(buildLocation != null)
                    if(buildLocation.isInside(e.getBlock().getLocation()))
                        e.setCancelled(false);
            }
        }
    }

}