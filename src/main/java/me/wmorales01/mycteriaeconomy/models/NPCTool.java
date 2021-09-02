package me.wmorales01.mycteriaeconomy.models;

import me.wmorales01.mycteriaeconomy.util.StringUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class NPCTool {

    public static ItemStack getItemStack() {
        ItemStack tool = new ItemStack(Material.GOLDEN_AXE);
        ItemMeta meta = tool.getItemMeta();
        List<String> lore = new ArrayList<>();

        meta.setDisplayName(StringUtil.formatColor("&6NPC Tool"));
        lore.add(StringUtil.formatColor("&cRight click to delete an NPC."));
        lore.add(StringUtil.formatColor("&aShift + Right click to configure an NPC."));
        meta.setLore(lore);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.values());
        tool.setItemMeta(meta);

        return tool;
    }
}
