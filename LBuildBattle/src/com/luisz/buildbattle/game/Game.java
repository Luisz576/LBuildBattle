package com.luisz.buildbattle.game;

import com.lib576.Lib576;
import com.lib576.libs.NMS;
import com.luisz.buildbattle.Main;
import com.luisz.buildbattle.game.arena.Arena;
import com.luisz.buildbattle.game.common.GameState;
import com.luisz.buildbattle.game.listener.GameListener;
import net.minecraft.server.v1_16_R3.Tuple;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Game {

    public static final int TIME_TO_START = 30,
            TIME_TO_TYPE = 30,
            TIME_TO_BUILD = 10*60,
            TIME_TO_VOTE = 12,
            TIME_TO_FINISH = 10;

    private GameListener gameListener;

    private final Arena arena;
    public int getMaxPlayers(){ return arena.maxPlayers; }
    public int getPlayers(){ return players.size(); }
    public String getArenaId(){
        return arena.id;
    }
    private final int gameLoopId;

    private GameState gameState = GameState.STARTING;
    public GameState getGameState(){ return this.gameState; }
    private final List<Player> players = new ArrayList<>(), espectadores = new ArrayList<>();
    private boolean removePlayer(Player p){
        boolean isP = isPlayer(p);
        players.remove(p);
        espectadores.remove(p);
        return isP;
    }
    private void addPlayer(Player p){
        removePlayer(p);
        p.setGameMode(GameMode.CREATIVE);
        players.add(p);
    }
    private void addEspectador(Player p){
        removePlayer(p);
        p.setGameMode(GameMode.SPECTATOR);
        espectadores.add(p);
    }

    private int time;
    public void _forceTime(int newTime){
        this.time = newTime;
    }

    public Game(Arena arena){
        this.arena = arena;
        gameLoopId = Lib576.sc.scheduleSyncRepeatingTask(Lib576.getInstance(), this::run, 0, 20);
        gameListener = new GameListener(this);
        Lib576.pm.registerEvents(gameListener, Main.instance);
        time = TIME_TO_START;
    }

    public void _stop(){
        if(gameLoopId != -1){
            gameState = GameState.STARTING;
            Lib576.sc.cancelTask(gameLoopId);
            HandlerList.unregisterAll(gameListener);
            gameListener = null;
            _unloadArena();
        }
        GameController._removeGame(arena.id);
    }

    private void _unloadArena(){
        for(Arena.BuildLocation buildLocation : arena.buildLocations)
            for(Block block : buildLocation.area.getBlocksInside())
                block.setType(Material.AIR);
    }

    //GameLoop
    private void run(){
        switch (gameState){
            case STARTING:
                if(getPlayers() < arena.minPlayers)
                    time = TIME_TO_START;
                else if(time < 10)
                    if(time <= 0)
                        __start();
                    else
                        sendMessageToAll(ChatColor.YELLOW + "O jogo começa em " + ChatColor.GREEN + "" + time + "" + ChatColor.YELLOW + " segundos!");
                break;
            case TYPING:
                if(getPlayers() <= 0){
                    sendTitleToAll(ChatColor.RED + "Sem jogadores");
                    _stop();
                }
                if(time <= 0)
                    if(_decideTema()){
                        _selectBuildLocations();
                        Location lastL = null;
                        for(Player p : players) {
                            lastL = playersBuildLocations.get(p).spawn;
                            p.teleport(lastL);
                        }
                        for(Player p : espectadores) {
                            if(lastL != null)
                                p.teleport(lastL);
                        }
                        sendTitleToAll(ChatColor.YELLOW + tema);
                        sendMessageToAll(ChatColor.GREEN + "Tema: " + ChatColor.YELLOW + tema);
                        gameState = GameState.BUILDING;
                        time = TIME_TO_BUILD;
                    }else{
                        sendTitleToAll(ChatColor.RED + "Esperando temas");
                        sendMessageToAll(ChatColor.RED + "Esperando por temas...");
                        time = TIME_TO_TYPE;
                    }
                break;
            case BUILDING:
                if(getPlayers() <= 0){
                    sendTitleToAll(ChatColor.RED + "Sem jogadores");
                    _stop();
                }
                if(time <= 0)
                    __voting();
                else if(time <= 10)
                    sendMessageToAll(ChatColor.YELLOW + "" + time + " segundo(s) restante(s)!");
                else if(time % 60 == 0)
                    sendMessageToAll(ChatColor.YELLOW + "" + time + " minuto(s) restante(s)!");
                break;
            case VOTING:
                if(getPlayers() <= 0){
                    sendTitleToAll(ChatColor.RED + "Sem jogadores");
                    _stop();
                }
                if(time <= 0)
                    if(__novoVoting())
                        time = TIME_TO_VOTE;
                    else
                        __finishVoting();
                else if(time <= 5)
                    sendMessageToAll(ChatColor.YELLOW + "" + time + " segundo(s) restante(s)!");
                break;
            case STOPPING:
                if(time <= 0)
                    _stop();
                else if(time <= 5)
                    sendMessageToAll(ChatColor.YELLOW + "" + time + " segundo(s) para reiniciar!");
                break;
        }
        for(Player p : players)
            p.setLevel(time);
        for(Player p : espectadores)
            p.setLevel(time);
        time--;
    }

    //buildLocations
    private final HashMap<Player, Arena.BuildLocation> playersBuildLocations = new HashMap<>();
    private void _selectBuildLocations(){
        int i = 0;
        for(Player p : players){
            playersBuildLocations.put(p, arena.buildLocations.get(i));
            playersVotingRemaning.put(p, arena.buildLocations.get(i));
            i++;
        }
    }
    public Arena.BuildLocation getPlayerBuildLocation(Player p){
        return playersBuildLocations.get(p);
    }

    //tema
    private String tema = null;
    private final HashMap<Player, String> possiveisTemas = new HashMap<>();
    public void playerMandaTema(Player p, String tema){
        if(!possiveisTemas.containsKey(p))
            possiveisTemas.put(p, tema);
    }
    public boolean playerPrecisaMandarTema(Player p){
        return !possiveisTemas.containsKey(p);
    }
    private boolean _decideTema(){
        if(possiveisTemas.size() > 0){
            int r = Lib576.random.nextInt(possiveisTemas.size());
            int counter = 0;
            for(String t : possiveisTemas.values()) {
                if (counter == r)
                    tema = t;
                counter++;
            }
            return tema != null;
        }
        return false;
    }

    //states
    private void __start(){
        gameState = GameState.TYPING;
        time = TIME_TO_TYPE;
        teleportAll(arena.typingLocation);
        sendMessageToAll(ChatColor.GREEN + "O jogo começou!");
        sendMessageToPlayers(ChatColor.YELLOW + "Digite um possível " + ChatColor.RED + "tema" + ChatColor.YELLOW + " no chat!");
    }

    private final HashMap<Player, Integer> playersVoting = new HashMap<>();
    private final HashMap<Player, Arena.BuildLocation> playersVotingRemaning = new HashMap<>();
    private void __voting(){
        gameState = GameState.VOTING;
        time = TIME_TO_VOTE;
        for(Player p : players)
            p.getInventory().clear();
        for(Player p : espectadores)
            p.getInventory().clear();
        sendMessageToAll(ChatColor.GREEN + "Acabou o tempo!");
        sendTitleToAll(ChatColor.GREEN + "Votação");
        __novoVoting();
    }
    private final List<Player> needVote = new ArrayList<>();
    private Player currentVoting;
    private boolean __novoVoting(){
        needVote.clear();
        needVote.addAll(players);
        if(playersVotingRemaning.keySet().size() > 0){
            for(Player pVoting : playersVotingRemaning.keySet()){
                currentVoting = pVoting;
                needVote.remove(currentVoting);
                teleportAll(playersVotingRemaning.get(pVoting).spawn);
                sendMessageToPlayers(ChatColor.YELLOW + "Votando a costrução de " + ChatColor.RED + pVoting.getName());
                playersVotingRemaning.remove(pVoting);
                playersVoting.put(pVoting, 0);
                sendMessageToPlayers(ChatColor.GREEN + "Digine um número de 1 a 5 para votar:");
                break;
            }
            return true;
        }
        return false;
    }
    public boolean vote(Player p, int n){
        if(p != currentVoting && needVote(p)) {
            needVote.remove(p);
            playersVoting.put(currentVoting, playersVoting.get(currentVoting) + n);
            return true;
        }
        return false;
    }
    public boolean needVote(Player p){
        return needVote.contains(p);
    }

    private void __finishVoting(){
        gameState = GameState.STOPPING;
        time = TIME_TO_FINISH;
        Tuple<Player, Integer> w = new Tuple<>(null, -1);
        for(Player p : playersVoting.keySet()){
            if(playersVoting.get(p) > w.b())
                w = new Tuple<>(p, playersVoting.get(p));
        }
        sendMessageToAll(ChatColor.YELLOW + "Vencedor: " + ChatColor.GREEN + w.a().getName());
        sendTitleToAll(ChatColor.YELLOW + w.a().getName());
    }

    //public methods
    public boolean isPlayer(Player p){
        return players.contains(p);
    }
    public boolean isEspectador(Player p){
        return espectadores.contains(p);
    }
    public void joinPlayer(Player p){
        if(players.contains(p) || espectadores.contains(p))
            quitPlayer(p);
        if(gameState == GameState.STARTING){
            if(arena.maxPlayers > players.size())
                addPlayer(p);
            else
                addEspectador(p);
        }else
            addEspectador(p);
        p.getInventory().clear();
        p.setHealth(20);
        p.setLevel(0);
        p.setExp(0);
        p.setFoodLevel(20);
        p.teleport(arena.spawn);
        sendMessageToPlayers(ChatColor.GREEN + p.getName() + ChatColor.YELLOW + " entrou! [" + getPlayers() + "/" + arena.maxPlayers + "]");
    }
    public void quitPlayer(Player p){
        boolean isPlayer = removePlayer(p);
        if(isPlayer)
            sendMessageToAll(ChatColor.RED + "O jogador " + ChatColor.YELLOW + p.getName() + ChatColor.RED + " saiu!");
        else
            sendMessageToAll(ChatColor.GRAY + "O jogador " + ChatColor.DARK_GRAY + p.getName() + ChatColor.GRAY + " saiu!");
    }
    public void sendMessageToAll(String message){
        for(Player p : players)
            p.sendMessage(message);
        for(Player p : espectadores)
            p.sendMessage(message);
    }
    public void sendMessageToPlayers(String message){
        for(Player p : players)
            p.sendMessage(message);
    }
    public void teleportAll(Location location){
        for(Player p : players)
            p.teleport(location);
        for(Player p : espectadores)
            p.teleport(location);
    }
    public void sendTitleToAll(String title){
        for(Player p : players)
            NMS.sendTitle(title, p);
        for(Player p : espectadores)
            NMS.sendTitle(title, p);
    }

}