package me.spiderdeluxe.mycteriaeconomy.settings;

import org.bukkit.Material;
import org.mineacademy.fo.model.SimpleTime;
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
        public static Integer BUSINESS_ACCOUNT_PRICE;

        public static String PERMISSION_EDIT_MAINBANKS;
        public static String PERMISSION_EDIT_ALLBANKS;

        public static SimpleTime TRANSACTION_TIME;

        public static SimpleTime LOAN_TIME;
        public static Integer LOAN_PENALTY_PERCENTAGE;


        public static Integer BUSINESS_COMMISSION_FEE_PERCENTAGE;
        public static Integer DEFAULT_COMMISSION_FEE_PERCENTAGE;

        public static Integer LINK_MACHINE_HEIGHT;
        public static Integer LINK_MACHINE_RADIUS;

        public static Integer SHOP_DISPLAY_NAME_MIN_LENGTH;
        public static Integer SHOP_DISPLAY_NAME_MAX_LENGTH;
        public static Boolean SHOP_DISPLAY_NAME_USES_AMPERSAND;

        private static void init() {
            pathPrefix("General");
            WALLET_ITEM = getMaterial("Wallet_Item").getMaterial();
            BUSINESS_ACCOUNT_PRICE = getInteger("Business_Account_Price");
            TRANSACTION_TIME = getTime("Transaction_Time");

            pathPrefix("General.Permission");
            PERMISSION_EDIT_MAINBANKS = getString("Edit_MainBanks");
            PERMISSION_EDIT_ALLBANKS = getString("Edit_AllBanks");

            pathPrefix("General.Loan");
            LOAN_TIME = getTime("Penalty_Time");
            LOAN_PENALTY_PERCENTAGE = getInteger("Penalty_Percentage");

            pathPrefix("General.Commission_Fee");
            BUSINESS_COMMISSION_FEE_PERCENTAGE = getInteger("Business_Percentage");
            DEFAULT_COMMISSION_FEE_PERCENTAGE = getInteger("Default_Percentage");

            pathPrefix("General.Linking_Chest");
            LINK_MACHINE_HEIGHT = getInteger("Height");
            LINK_MACHINE_RADIUS = getInteger("Radius");

            pathPrefix("General.Shop.Display_Name");
            SHOP_DISPLAY_NAME_MIN_LENGTH = getInteger("Min_Length");
            SHOP_DISPLAY_NAME_MAX_LENGTH = getInteger("Max_Length");
            SHOP_DISPLAY_NAME_USES_AMPERSAND = getBoolean("Uses_Ampersand");
        }


    }

    public static class Messages {

        public static String INSUFFICIENT_BALANCE;

        private static void init() {
            pathPrefix("Messages");
            INSUFFICIENT_BALANCE = getString("Insufficient_Balance");
        }
    }

    public static class Menu {


        public static String ATM_ACTIVATION_SOUND;
        public static Double ATM_ACTIVATION_VOLUME;
        public static Double ATM_ACTIVATION_PITCH;

        public static String ATM_DEACTIVATION_SOUND;
        public static Double ATM_DEACTIVATION_VOLUME;
        public static Double ATM_DEACTIVATION_PITCH;

        public static String SHOP_ACTIVATION_SOUND;
        public static Double SHOP_ACTIVATION_VOLUME;
        public static Double SHOP_ACTIVATION_PITCH;

        public static String SHOP_DEACTIVATION_SOUND;
        public static Double SHOP_DEACTIVATION_VOLUME;
        public static Double SHOP_DEACTIVATION_PITCH;

        private static void init() {
            pathPrefix("Menu.Atm");
            pathPrefix("Menu.Atm.Sounds.Activation");
            ATM_ACTIVATION_SOUND = getString("Sound");
            ATM_ACTIVATION_VOLUME = getDouble("Volume");
            ATM_ACTIVATION_PITCH = getDouble("Pitch");
            pathPrefix("Menu.Atm.Sounds.Deactivation");
            ATM_DEACTIVATION_SOUND = getString("Sound");
            ATM_DEACTIVATION_VOLUME = getDouble("Volume");
            ATM_DEACTIVATION_PITCH = getDouble("Pitch");

            pathPrefix("Menu.Shop");
            pathPrefix("Menu.Shop.Sounds.Activation");
            SHOP_ACTIVATION_SOUND = getString("Sound");
            SHOP_ACTIVATION_VOLUME = getDouble("Volume");
            SHOP_ACTIVATION_PITCH = getDouble("Pitch");
            pathPrefix("Menu.Shop.Sounds.Deactivation");
            SHOP_DEACTIVATION_SOUND = getString("Sound");
            SHOP_DEACTIVATION_VOLUME = getDouble("Volume");
            SHOP_DEACTIVATION_PITCH = getDouble("Pitch");
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