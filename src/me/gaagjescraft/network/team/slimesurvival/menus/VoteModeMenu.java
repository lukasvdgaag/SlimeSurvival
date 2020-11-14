package me.gaagjescraft.network.team.slimesurvival.menus;

import com.google.common.collect.Lists;
import me.gaagjescraft.network.team.slimesurvival.enums.ArenaMode;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import me.gaagjescraft.network.team.slimesurvival.game.SlimePlayer;
import me.gaagjescraft.network.team.slimesurvival.managers.items.ItemsManager;
import me.gaagjescraft.network.team.slimesurvival.utils.SlimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class VoteModeMenu  {

    private SlimeArena arena;
    private List<SlimePlayer> viewers = Lists.newArrayList();

    public VoteModeMenu(SlimeArena arena) {
        this.arena = arena;
    }

    public void click(SlimePlayer player, int slot) {
        if (slot == 11 && player.getVotedMode() != ArenaMode.NORMAL) {
            player.setVotedMode(ArenaMode.NORMAL);
            player.getPlayer().sendMessage("You voted for normal mode");
            updateAll();
        }
        else if (slot == 13 && player.getVotedMode() != ArenaMode.CLASSIC) {
            player.setVotedMode(ArenaMode.CLASSIC);
            player.getPlayer().sendMessage("You voted for classic mode");
            updateAll();
        }
        else if (slot == 15 && player.getVotedMode() != ArenaMode.FREEZE){
            player.getPlayer().sendMessage(ChatColor.RED+ "You can't vote for this option right now, as it's still being developed!");
            /*player.setVotedMode(ArenaMode.FREEZE);
            player.getPlayer().sendMessage("You voted for freeze mode");
            updateAll();*/
        }
    }

    private void open(SlimePlayer player, Inventory copy) {
        player.getPlayer().openInventory(copy);
        addViewer(player);
        if (player.getVotedMode() != null) {
            int slot = 11;
            if (player.getVotedMode() == ArenaMode.CLASSIC) {
                slot = 13;
            } else if (player.getVotedMode() == ArenaMode.FREEZE) {
                slot = 15;
            }
            ItemStack i = copy.getItem(slot);
            ItemMeta im = i.getItemMeta();
            im.addEnchant(Enchantment.DURABILITY, 1, true);
            i.setItemMeta(im);
            copy.setItem(slot, i);
            //player.getPlayer().openInventory(copy);
        }
    }

    private void updateAll() {
        for (SlimePlayer sp : getViewers()) {
            update(sp);
        }
    }

    public void update(SlimePlayer sp) {
        Inventory menu = Bukkit.createInventory(null, 27, "§aVote for game mode");

        ItemStack vm = ItemsManager.MENU_VOTE_NORMAL.clone();
        List<String> vLore = vm.getItemMeta().getLore();
        ItemMeta vmm = vm.getItemMeta();
        vLore.add(SlimeUtils.c("&cVotes: " + arena.getModeVotes(ArenaMode.NORMAL)));
        vmm.setLore(vLore);
        vm.setItemMeta(vmm);

        ItemStack cm = ItemsManager.MENU_VOTE_CLASSIC.clone();
        List<String> cLore = cm.getItemMeta().getLore();
        ItemMeta cmm = cm.getItemMeta();
        cLore.add(SlimeUtils.c("&cVotes: " + arena.getModeVotes(ArenaMode.CLASSIC)));
        cmm.setLore(cLore);
        cm.setItemMeta(cmm);

        ItemStack fm = ItemsManager.MENU_VOTE_FREEZE.clone();
        List<String> fLore = fm.getItemMeta().getLore();
        ItemMeta fmm = fm.getItemMeta();
        fLore.add(SlimeUtils.c("&cVotes: " + arena.getModeVotes(ArenaMode.FREEZE)));
        fmm.setLore(fLore);
        fm.setItemMeta(fmm);

        menu.setItem(11, vm);
        menu.setItem(13, cm);
        menu.setItem(15, fm);

        open(sp, menu);

    }

    public List<SlimePlayer> getViewers() {
        return viewers;
    }

    public void addViewer(SlimePlayer sp) {
        if (!viewers.contains(sp)) {
            viewers.add(sp);
        }
    }

    public void removeViewer(SlimePlayer sp) {
        viewers.remove(sp);
    }
}
