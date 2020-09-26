package me.gaagjescraft.network.team.slimesurvival.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class BaseCmd {

    public CommandSender sender;
    public String[] args;
    public String[] alias = new String[0];
    public String cmdName;
    public int argLength = 0;
    public boolean forcePlayer = true;
    public Player player;
    public String type;
    public int maxArgs = -1;

    public BaseCmd() {}

    public void processCmd(CommandSender s, String[] arg) {
        sender = s;
        args = arg;

        if (forcePlayer) {
            if (!(s instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player to perform this command");
                return;
            }
            player = ((Player) s);
        }


        if (!sender.hasPermission("slimesurvival.commands." + type + "." + cmdName)) {
            sender.sendMessage(ChatColor.RED+"You don't have permission to perform this command");
        } else if ((maxArgs == -1 && argLength > arg.length) || (maxArgs!=-1 && arg.length > maxArgs)) {
            s.sendMessage(ChatColor.DARK_RED + "Wrong usage: " + cmdName);
        } else {
            boolean returnVal = run();
            if (!returnVal) {
                s.sendMessage(ChatColor.DARK_RED + "Wrong usage: " + cmdName);
            }
        }
    }

    public String getType() {
        return type;
    }

    public abstract boolean run();

}
