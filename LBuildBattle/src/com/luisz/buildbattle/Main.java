package com.luisz.buildbattle;

import com.lib576.Lib576;
import com.lib576.plugin.LPlugin;
import com.luisz.buildbattle.command.Commands;
import com.luisz.buildbattle.game.GameController;
import com.luisz.buildbattle.game.arena.ArenaConfigs;
import com.luisz.buildbattle.listener.GlobalListener;
import com.luisz.buildbattle.sign.SignsController;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;

public class Main extends LPlugin {

    public static ArenaConfigs arenaConfigs;
    public static Main instance;

    @Override
    public void enable() {
        instance = this;
        arenaConfigs = new ArenaConfigs();
        registerExecutor("lb", new Commands());
        Lib576.pm.registerEvents(new GlobalListener(), Lib576.getInstance());
        SignsController._start();
        Lib576.cmd.sendMessage(ChatColor.GREEN + "LBuildBattle inicializado!");
    }

    @Override
    public void disable() {
        GameController._disable();
    }

    private void registerExecutor(String commandName, CommandExecutor executor){
        PluginCommand command = getCommand(commandName);
        if(command != null)
            command.setExecutor(executor);
    }

}