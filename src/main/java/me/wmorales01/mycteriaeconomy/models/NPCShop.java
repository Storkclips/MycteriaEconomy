package me.wmorales01.mycteriaeconomy.models;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.Location;

import java.util.List;

public class NPCShop extends VendingMachine {
    private final EntityPlayer npc;

    public NPCShop(EntityPlayer npc) {
        super(npc.getBukkitEntity().getLocation());
        this.npc = npc;
    }

    public NPCShop(EntityPlayer npc, List<Location> chestLocations, List<MachineItem> stock) {
        super(npc.getBukkitEntity().getLocation(), chestLocations, stock);
        this.npc = npc;
    }

    public static NPCShop getByEntityPlayer(EntityPlayer npc) {
        for (NPCShop npcShop : MycteriaEconomy.getInstance().getNpcShops()) {
            if (!npcShop.getNpc().equals(npc))
                continue;

            return npcShop;
        }
        return null;
    }

    public EntityPlayer getNpc() {
        return npc;
    }
}
