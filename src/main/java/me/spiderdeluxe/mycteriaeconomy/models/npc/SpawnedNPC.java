package me.spiderdeluxe.mycteriaeconomy.models.npc;

import lombok.Data;
import org.bukkit.entity.LivingEntity;

@Data
public final class SpawnedNPC {

	private final NPCBase npcBase;
	private final LivingEntity entity;
}
