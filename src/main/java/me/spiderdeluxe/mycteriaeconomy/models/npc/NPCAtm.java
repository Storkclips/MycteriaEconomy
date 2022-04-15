package me.spiderdeluxe.mycteriaeconomy.models.npc;

import lombok.Getter;
import me.spiderdeluxe.mycteriaeconomy.cache.EconomyPlayer;
import me.spiderdeluxe.mycteriaeconomy.models.bank.ATM;
import me.spiderdeluxe.mycteriaeconomy.menu.atm.ATMMenu;
import me.spiderdeluxe.mycteriaeconomy.models.bank.BaseBank;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.UUID;

public class NPCAtm extends NPCBase {
    @Getter
    private ATM atm;


    public NPCAtm(final String name, final UUID uuid, final EntityType type, final NPC citizen) {
        super(uuid, name, type, citizen);
        atm = ATM.alreadyExist(uuid) ?
                ATM.findATM(uuid) : ATM.createATM(uuid);
        setAtmUUID(uuid);


        setFunction(NPCFunction.ATM);
    }


    @Override
    protected void onNPCCreation(final NPCBase npcBase) {

    }

    @Override
    protected void onNPCDelete(final String name) {
        atm.deleteATM();
    }

    @Override
    protected void onNPCRightClick(final Player player, final LivingEntity entity, final PlayerInteractEntityEvent event) {

        final EconomyPlayer economyPlayer = EconomyPlayer.from(player);

        if (BaseBank.isWithin(player, false))
            new ATMMenu(economyPlayer).displayTo(player);
    }
}
