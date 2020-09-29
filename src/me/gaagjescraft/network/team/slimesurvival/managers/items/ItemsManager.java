package me.gaagjescraft.network.team.slimesurvival.managers.items;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemsManager {

    public static Color ARMOR_SLIME_COLOR;
    public static Color ARMOR_SURVIVOR_COLOR;

    public static ItemStack ARMOR_SLIME_HEAD;
    public static ItemStack ARMOR_SLIME_CHESTPLATE;
    public static ItemStack ARMOR_SLIME_LEGGINGS;
    public static ItemStack ARMOR_SLIME_BOOTS;

    public static ItemStack ARMOR_SURVIVOR_HELMET;
    public static ItemStack ARMOR_SURVIVOR_CHESTPLATE;
    public static ItemStack ARMOR_SURVIVOR_LEGGINGS;
    public static ItemStack ARMOR_SURVIVOR_BOOTS;

    public static ItemStack ITEM_SLIME_THROWER;
    public static ItemStack ITEM_SLIME_BOMBER;

    public static ItemStack ITEM_VOTE_MODE;
    public static ItemStack ITEM_LEAVE;

    public ItemsManager() {
        loadArmor();
        loadItems();
    }

    public void loadArmor() {
        ARMOR_SLIME_COLOR = Color.fromRGB(111, 242, 113);
        ARMOR_SURVIVOR_COLOR = Color.fromRGB(103, 110, 103);

        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwner("talaknor");
        meta.setDisplayName(ChatColor.DARK_GREEN + "Slime Head");
        meta.addItemFlags(ItemFlag.values());
        head.setItemMeta(meta);

        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        LeatherArmorMeta leta = (LeatherArmorMeta) chest.getItemMeta();
        leta.setColor(ARMOR_SLIME_COLOR);
        leta.setDisplayName(ChatColor.DARK_GREEN + "Slime Team");
        leta.addItemFlags(ItemFlag.values());

        chest.setItemMeta(leta);
        leggings.setItemMeta(leta);
        boots.setItemMeta(leta);

        ARMOR_SLIME_HEAD = head;
        ARMOR_SLIME_CHESTPLATE = chest;
        ARMOR_SLIME_LEGGINGS = leggings;
        ARMOR_SLIME_BOOTS = boots;

        //////////// survivor armor here

        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta sm = leta.clone();
        sm.setDisplayName(ChatColor.DARK_GRAY + "Survivor Team");
        sm.setColor(ARMOR_SURVIVOR_COLOR);
        ItemStack shelmet = helmet.clone();
        shelmet.setItemMeta(sm);
        ItemStack schest = chest.clone();
        schest.setItemMeta(sm);
        ItemStack sleggings = leggings.clone();
        sleggings.setItemMeta(sm);
        ItemStack sboots = boots.clone();
        sboots.setItemMeta(sm);

        ARMOR_SURVIVOR_HELMET = shelmet;
        ARMOR_SURVIVOR_CHESTPLATE = schest;
        ARMOR_SURVIVOR_LEGGINGS = sleggings;
        ARMOR_SURVIVOR_BOOTS = sboots;
    }

    public void loadItems() {
        ItemStack slimeThrower = new ItemStack(Material.SLIME_BALL);
        ItemMeta slimeMeta = slimeThrower.getItemMeta();
        slimeMeta.setDisplayName(ChatColor.GREEN + "Slime thrower");
        slimeMeta.setLore(Lists.newArrayList(ChatColor.GRAY + "Left click to throw a slime"));
        slimeMeta.addItemFlags(ItemFlag.values());
        slimeThrower.setItemMeta(slimeMeta);

        ItemStack bigSlime = new ItemStack(Material.MAGMA_CREAM);
        ItemMeta bigMeta = bigSlime.getItemMeta();
        bigMeta.setDisplayName(ChatColor.GREEN + "Magma bomber");
        bigMeta.setLore(Lists.newArrayList(ChatColor.GRAY + "Left click to throw a magma bomb"));
        bigMeta.addItemFlags(ItemFlag.values());
        bigSlime.setItemMeta(bigMeta);

        ITEM_SLIME_BOMBER = bigSlime;
        ITEM_SLIME_THROWER = slimeThrower;

        // vote items

        ItemStack vote = new ItemStack(Material.PAPER);
        ItemMeta vmeta = vote.getItemMeta();
        vmeta.setDisplayName(ChatColor.AQUA + "Vote For Mode");
        vmeta.setLore(Lists.newArrayList(ChatColor.GRAY + "Left click to open the mode voting menu"));
        vmeta.addItemFlags(ItemFlag.values());
        vote.setItemMeta(vmeta);

        ItemStack leave = new ItemStack(Material.BED);
        ItemMeta lmeta = leave.getItemMeta();
        lmeta.setDisplayName(ChatColor.RED + "Leave");
        lmeta.setLore(Lists.newArrayList(ChatColor.GRAY + "Left click to leave the game"));
        lmeta.addItemFlags(ItemFlag.values());
        leave.setItemMeta(lmeta);

        ITEM_VOTE_MODE = vote;
        ITEM_LEAVE = leave;

    }

}
