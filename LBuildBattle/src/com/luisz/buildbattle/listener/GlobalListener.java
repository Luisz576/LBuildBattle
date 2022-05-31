package com.luisz.buildbattle.listener;

import com.luisz.buildbattle.Main;
import com.luisz.buildbattle.building.BuildingVars;
import com.luisz.buildbattle.game.GameController;
import com.luisz.buildbattle.game.arena.Arena;
import com.luisz.buildbattle.sign.SignGame;
import com.luisz.buildbattle.sign.SignsController;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class GlobalListener implements Listener {

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent e){
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK)
            if(e.getClickedBlock() != null)
                if(e.getClickedBlock().getType() == Material.OAK_SIGN ||
                    e.getClickedBlock().getType() == Material.OAK_WALL_SIGN) {
                    SignGame signGame = SignGame.fromLocation(e.getClickedBlock().getLocation());
                    if (signGame != null)
                        if(!GameController.joinInGame(signGame.arena.id, e.getPlayer()))
                            e.getPlayer().sendMessage(ChatColor.RED + "Arena fechada!");
                }else if(e.getPlayer().getInventory().getItemInMainHand().getType() == Material.WOODEN_HOE){
                    BuildingVars.setRight(e.getPlayer(), e.getClickedBlock().getLocation());
                    e.getPlayer().sendMessage(ChatColor.YELLOW + "Right setted!");
                }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        Player p = e.getPlayer();
        if(p.getInventory().getItemInMainHand().getType() == Material.WOODEN_HOE){
            BuildingVars.setLeft(e.getPlayer(), e.getBlock().getLocation());
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Left setted!");
            e.setCancelled(true);
        }
        if(e.getBlock().getType() == Material.OAK_SIGN ||
            e.getBlock().getType() == Material.OAK_WALL_SIGN)
            if(SignsController.getSignGame(e.getBlock().getLocation()) != null)
                e.setCancelled(true);
    }

    @EventHandler
    public void onSignChangeEvent(SignChangeEvent e){
        if(e.getBlock().getType() == Material.OAK_SIGN ||
            e.getBlock().getType() == Material.OAK_WALL_SIGN){
            String verify = e.getLine(0);
            if(verify != null && verify.equalsIgnoreCase("SIGNGAME")){
                String arenaId = e.getLine(1);
                String signId = e.getLine(2);
                if(arenaId != null && signId != null){
                    arenaId = arenaId.toLowerCase();
                    signId = signId.toLowerCase();
                    Arena arena = Main.arenaConfigs.getArena(arenaId);
                    if(arena != null){
                        if(!SignsController.hasSignGame(signId)){
                            if(signId.length() >= 4){
                                if(SignsController._register(signId, arena, e.getBlock().getLocation())){
                                    e.setLine(0, "Registered!");
                                    e.setLine(1, "");
                                    e.setLine(2, "");
                                    e.setLine(3, "");
                                }else{
                                    e.setLine(0, "Error while");
                                    e.setLine(1, "creating");
                                    e.setLine(2, "sign :(");
                                    e.setLine(3, "");
                                }
                            }else{
                                e.setLine(0, "Invalid");
                                e.setLine(1, "sign name");
                                e.setLine(2, "");
                                e.setLine(3, "");
                            }
                        }else{
                            e.setLine(0, "A Sign with");
                            e.setLine(1, "this id");
                            e.setLine(2, "already");
                            e.setLine(3, "exist");
                        }
                    }else{
                        e.setLine(0, "Arena not");
                        e.setLine(1, "founded");
                        e.setLine(2, "");
                        e.setLine(3, "");
                    }
                }
            }
        }
    }

}