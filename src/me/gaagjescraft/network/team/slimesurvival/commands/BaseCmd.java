package me.gaagjescraft.network.team.slimesurvival.commands;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
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
                SlimeSurvival.getMessages().getMustBePlayer().send(sender);
                return;
            }
            player = ((Player) s);
        }


        if (!sender.hasPermission("slimesurvival.commands." + type + "." + cmdName)) {
            SlimeSurvival.getMessages().getNoCommandPermission().send(sender);
        } else if ((maxArgs == -1 && argLength > arg.length) || (maxArgs!=-1 && arg.length > maxArgs)) {
            s.sendMessage(ChatColor.DARK_RED + "Wrong usage: " + SlimeSurvival.getMessages().getCommandDescription(type, cmdName).getMessage().get(0));
        } else {
            boolean returnVal = run();
            if (!returnVal) {
                s.sendMessage(ChatColor.DARK_RED + "Wrong usage: " + SlimeSurvival.getMessages().getCommandDescription(type, cmdName).getMessage().get(0));
            }
        }
    }

    public String getType() {
        return type;
    }

    public abstract boolean run();

}
