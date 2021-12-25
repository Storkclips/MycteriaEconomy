package me.spiderdeluxe.mycteriaeconomy.settings;

import org.bukkit.Material;
import org.mineacademy.fo.settings.SimpleSettings;

/**
 * @author SpiderDeluxe
 * This class manages all customizable settings
 */
public class Settings extends SimpleSettings {

	@Override
	protected int getConfigVersion() {
		return 1;
	}

	public static class General {

		public static Material WALLET_ITEM;
		public static Double ATM_TRANSACTION_FEE;

		public static Integer LINK_MACHINE_HEIGHT;
		public static Integer LINK_MACHINE_RADIUS;

		public static Integer NPC_STOCK_AMOUNT_DISCOUNT;

		public static Integer SHOP_DISPLAY_NAME_MIN_LENGTH;
		public static Integer SHOP_DISPLAY_NAME_MAX_LENGTH;
		public static Boolean SHOP_DISPLAY_NAME_USES_AMPERSAND;

		public static String SHOP_ACTIVATION_SOUND;
		public static Double SHOP_ACTIVATION_VOLUME;
		public static Double SHOP_ACTIVATION_PITCH;
		public static String SHOP_DEACTIVATION_SOUND;
		public static Double SHOP_DEACTIVATION_VOLUME;
		public static Double SHOP_DEACTIVATION_PITCH;

		private static void init() {
			pathPrefix("General");
			WALLET_ITEM = getMaterial("Wallet_Item").getMaterial();
			ATM_TRANSACTION_FEE = getDouble("Atm_Transaction_Fee");

			pathPrefix("General.Linking_Chest");
			LINK_MACHINE_HEIGHT = getInteger("Height");
			LINK_MACHINE_RADIUS = getInteger("Radius");

			pathPrefix("General.NPC");
			NPC_STOCK_AMOUNT_DISCOUNT = getInteger("Stock_Amount_Discount");

			pathPrefix("General.Shop");
			pathPrefix("General.Shop.Sounds.Activation");
			SHOP_ACTIVATION_SOUND = getString("Sound");
			SHOP_ACTIVATION_VOLUME = getDouble("Volume");
			SHOP_ACTIVATION_PITCH = getDouble("Pitch");
			pathPrefix("General.Shop.Sounds.Deactivation");
			SHOP_DEACTIVATION_SOUND = getString("Sound");
			SHOP_DEACTIVATION_VOLUME = getDouble("Volume");
			SHOP_DEACTIVATION_PITCH = getDouble("Pitch");


			pathPrefix("General.Shop.Display_Name");
			SHOP_DISPLAY_NAME_MIN_LENGTH = getInteger("Min_Length");
			SHOP_DISPLAY_NAME_MAX_LENGTH = getInteger("Max_Length");
			SHOP_DISPLAY_NAME_USES_AMPERSAND = getBoolean("Uses_Ampersand");


		}
	}
	/*
	 * Automatically called method when we load settings.yml to load values in this class
	 *
	 * See above for usage.
	 */
	private static void init() {
		pathPrefix(null);
	}
}