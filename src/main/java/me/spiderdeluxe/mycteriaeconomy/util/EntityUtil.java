package me.spiderdeluxe.mycteriaeconomy.util;

import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class EntityUtil {
	public static Set<EntityType> getEntities() {
		return new HashSet<>(Arrays.asList(EntityType.values()));
	}

	/**
	 * Check if the name is part a EntityType
	 *
	 * @param name the name
	 */
	public static boolean isCorrectEntityType(final String name) {
		for (final EntityType type : getEntities())
			if (type.name().toLowerCase(Locale.ROOT)
					.equals(name.toLowerCase(Locale.ROOT)))
				return true;
			return false;
	}


}
