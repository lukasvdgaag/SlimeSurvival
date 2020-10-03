package me.gaagjescraft.network.team.slimesurvival.commands.arenas;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ArenaCmdManager implements CommandExecutor  {

    private static ArenaCmdManager mcm;
    private List<BaseCmd> mapcmds = new ArrayList<>();

    public ArenaCmdManager() {
        mcm = this;
        mapcmds.add(new CreateArenaCmd());
        mapcmds.add(new SaveArenaCmd());
        mapcmds.add(new ListArenaCmd());
        mapcmds.add(new SetDisplayNameArenaCmd());
        mapcmds.add(new AddSpawnArenaCmd());
        mapcmds.add(new EditArenaCmd());
        mapcmds.add(new SetMinArenaCmd());
        mapcmds.add(new EnableArenacmd());
        mapcmds.add(new DisableArenaCmd());
        mapcmds.add(new InfoArenaCmd());
    }

    public static List<BaseCmd> getCommands() { return mcm.mapcmds; }

    public boolean onCommand(CommandSender s, Command command, String label, String[] args) {
        if (args.length == 0 || getCommands(args[0]) == null) {
            sendHelp(mapcmds, s);
        } else getCommands(args[0]).processCmd(s, args);
        return true;
    }

    private void sendHelp(List<BaseCmd> cmds, CommandSender s) {
        SlimeSurvival.getMessages().getCommandDescription("slimearena", "header").send(s);
        for (BaseCmd cmd : cmds) {
            if (s.hasPermission("slimesurvival.commands." + cmd.type + "." + cmd.cmdName)) {
                SlimeSurvival.getMessages().getCommandDescription(cmd.type, cmd.cmdName).send(s);
            }
        }
    }

    private BaseCmd getCommands(String s) {
        return getCmd(mapcmds, s);
    }

    private BaseCmd getCmd(List<BaseCmd> cmds, String s) {
        for (BaseCmd cmd : cmds) {
            if (cmd.cmdName.equalsIgnoreCase(s)) {
                return cmd;
            }
            for (String alias : cmd.alias) {
                if (alias.equalsIgnoreCase(s))
                    return cmd;
            }
        }
        return null;
    }

}
