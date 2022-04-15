package me.spiderdeluxe.mycteriaeconomy.models.npc;

import me.spiderdeluxe.mycteriaeconomy.cache.EconomyPlayer;
import me.spiderdeluxe.mycteriaeconomy.menu.tellers.TellersMenu;
import me.spiderdeluxe.mycteriaeconomy.models.bank.BaseBank;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.UUID;

public class NPCTeller extends NPCBase {

    public NPCTeller(final String name, final UUID uuid, final EntityType type, final NPC citizen) {
        super(uuid, name, type, citizen);
        setFunction(NPCFunction.TELLER);
    }


    @Override
    protected void onNPCRightClick(final Player player, final LivingEntity entity, final PlayerInteractEntityEvent event) {
        final EconomyPlayer economyPlayer = EconomyPlayer.from(player);

        if (BaseBank.isWithin(player, false))
            new TellersMenu(economyPlayer).displayTo(player);
    }
}
