package com.luisz.buildbattle.command;

import com.lib576.utils.LConvert;
import com.luisz.buildbattle.Main;
import com.luisz.buildbattle.building.BuildingVars;
import com.luisz.buildbattle.game.Game;
import com.luisz.buildbattle.game.GameController;
import com.luisz.buildbattle.game.arena.Arena;
import com.luisz.buildbattle.sign.SignGame;
import com.luisz.buildbattle.sign.SignsController;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "Você não pode usar este comando!");
            return false;
        }
        Player p = (Player) sender;
        if(p.isOp()){
            if(cmd.getName().equalsIgnoreCase("lb")){
                if(args.length > 0){
                    switch(args[0].toLowerCase()){
                        case "forcetime":
                            if(args.length == 3){
                                String arenaid = args[1];
                                int time = LConvert.convertToInteger(args[2]);
                                if(time >= 0){
                                    Arena arena = Main.arenaConfigs.getArena(arenaid);
                                    if(arena != null) {
                                        Game game = GameController.getGame(arenaid);
                                        if(game != null){
                                            game._forceTime(time);
                                            p.sendMessage(ChatColor.YELLOW + "Time alterado!");
                                        }else
                                            p.sendMessage(ChatColor.RED + "Essa arena não está aberta!");
                                    }else
                                        p.sendMessage(ChatColor.RED + "Essa arena não existe!");
                                }else
                                    p.sendMessage(ChatColor.RED + "Informe um time válido!");
                            }else
                                p.sendMessage(ChatColor.RED + "Use /lb forcetime <arenaid> <time>");
                            break;
                        case "removearena":
                            if(args.length == 2){
                                if(Main.arenaConfigs.removeArena(args[1].toLowerCase()))
                                    p.sendMessage(ChatColor.GREEN + "Arena removida!");
                                else
                                    p.sendMessage(ChatColor.RED + "Arena não encontrada!");
                            }else
                                p.sendMessage(ChatColor.RED + "Use /lb removearena <arenaid>");
                            break;
                        case "startgame":
                            if(args.length == 2){
                                int res = GameController.startGame(args[1].toLowerCase());
                                if(res == 3)
                                    p.sendMessage(ChatColor.GREEN + "Jogo iniciado!");
                                else if(res == 2)
                                    p.sendMessage(ChatColor.RED + "Arena não encontrada!");
                                else
                                    p.sendMessage(ChatColor.RED + "Arena já carregada!");
                            }else
                                p.sendMessage(ChatColor.RED + "Use /lb startgame <arenaid>");
                            break;
                        case "stopgame":
                            if(args.length == 2)
                                if(GameController.stopGame(args[1].toLowerCase()))
                                    p.sendMessage(ChatColor.GREEN + "Jogo finalizado!");
                                else
                                    p.sendMessage(ChatColor.RED + "Nenhum jogo carregado nessa arena!");
                            else
                                p.sendMessage(ChatColor.RED + "Use /lb stopgame <arenaid>");
                            break;
                        case "removesign":
                            if(args.length == 2) {
                                SignGame signGame = SignsController.getSignGame(args[1]);
                                if (signGame != null) {
                                    if(SignsController._unregister(signGame))
                                        p.sendMessage(ChatColor.GREEN + "Sign removida!");
                                    else
                                        p.sendMessage(ChatColor.RED + "Erro ao remover sign!");
                                }else
                                    p.sendMessage(ChatColor.RED + "Sign não encontrada!");
                            }else
                                p.sendMessage(ChatColor.RED + "Use /lb removesign <signid>");
                            break;
                        case "build":
                            return buildCommands(p, args);
                        default:
                            p.sendMessage(ChatColor.RED + "Esse comando não existe ou ainda não foi adicionado!");
                            break;
                    }
                }else
                    p.sendMessage(ChatColor.RED + "Use /lb <comando>");
            }
        }else
            p.sendMessage(ChatColor.RED + "Você não pode usar este comando!");
        return false;
    }

    private boolean buildCommands(Player p, String[] args){
        if(args.length > 1){
            switch (args[1].toLowerCase()) {
                case "startedit":
                    if (args.length == 3){
                        BuildingVars.setEditingArena(p, args[2].toLowerCase());
                        p.sendMessage(ChatColor.GREEN + "Editando!");
                    }else
                        p.sendMessage(ChatColor.RED + "/lb build startedit <arenaid>");
                    break;
                case "addbuildarea":
                    if(BuildingVars.isNotEditingArena(p)){
                        p.sendMessage(ChatColor.RED + "Você não está editando uma arena!");
                        return false;
                    }
                    int res = BuildingVars.addBuildLocation(p, p.getLocation());
                    if(res == 3)
                        p.sendMessage(ChatColor.GREEN + "Nova buildLocation adicionada!");
                    else if(res == 2)
                        p.sendMessage(ChatColor.RED + "Spawn não está contido na area!");
                    else
                        p.sendMessage(ChatColor.RED + "Left e/ou Right não definido(s)!");
                    break;
                case "settypingarea":
                    if(BuildingVars.isNotEditingArena(p)){
                        p.sendMessage(ChatColor.RED + "Você não está editando uma arena!");
                        return false;
                    }
                    if(BuildingVars.setTypingArea(p, p.getLocation()))
                        p.sendMessage(ChatColor.GREEN + "Typing area setada!");
                    else
                        p.sendMessage(ChatColor.RED + "Erro ao definir typing area!");
                    break;
                case "changearenaid":
                    if(BuildingVars.isNotEditingArena(p)){
                        p.sendMessage(ChatColor.RED + "Você não está editando uma arena!");
                        return false;
                    }
                    if(args.length == 3){
                        if(BuildingVars.changeArenaId(p, args[2]))
                            p.sendMessage(ChatColor.GREEN + "ArenaId trocado!");
                        else
                            p.sendMessage(ChatColor.RED + "Erro ao trocar id!");
                    }else
                        p.sendMessage(ChatColor.RED + "/lb build changearenaid <arenaid>");
                    break;
                case "save":
                    if(BuildingVars.isNotEditingArena(p)){
                        p.sendMessage(ChatColor.RED + "Você não está editando uma arena!");
                        return false;
                    }
                    if(args.length == 4){
                        int minPlayers = LConvert.convertToInteger(args[2]);
                        int maxPlayers = LConvert.convertToInteger(args[3]);
                        if(minPlayers > 0 && maxPlayers > 0){
                            Arena arena = BuildingVars.getEditingArena(p);
                            if(arena.typingLocation == null){
                                p.sendMessage(ChatColor.RED + "Typing location não setada!");
                                return false;
                            }
                            if(arena.buildLocations.size() < maxPlayers){
                                p.sendMessage(ChatColor.RED + "Quantidade de buildLocations insuficiente!");
                                return false;
                            }
                            arena.spawn = p.getLocation();
                            arena.maxPlayers = maxPlayers;
                            arena.minPlayers = minPlayers;
                            if(arena.hasAllInfo()){
                                if(Main.arenaConfigs.addArena(arena))
                                    p.sendMessage(ChatColor.GREEN + "Arena setada!");
                                else
                                    p.sendMessage(ChatColor.RED + "Arena já existente!");
                            }else
                                p.sendMessage(ChatColor.RED + "Algum erro ocorreu!");
                        }else
                            p.sendMessage(ChatColor.RED + "Passe valores válidos!");
                    }else
                        p.sendMessage(ChatColor.RED + "/lb build save <minPlayers> <maxPlayers>");
                    break;
                default:
                    p.sendMessage(ChatColor.RED + "Esse comando não existe ou ainda não foi adicionado!");
                    break;
            }
        }else
            p.sendMessage(ChatColor.RED + "Use /lb build <comando>");
        return false;
    }
}