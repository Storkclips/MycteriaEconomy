package me.wmorales01.mycteriaeconomy.models;

import me.wmorales01.mycteriaeconomy.util.InventoryUtil;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractShop {
    private final List<Chest> linkedChests;
    private final List<ShopItem> shopItems;
    private final ShopType shopType;

    public AbstractShop(ShopType shopType) {
        this.linkedChests = new ArrayList<>();
        this.shopItems = new ArrayList<>();
        this.shopType = shopType;
    }

    public AbstractShop(List<Chest> linkedChests, List<ShopItem> shopItems, ShopType shopType) {
        this.linkedChests = linkedChests;
        this.shopItems = shopItems;
        this.shopType = shopType;
    }

    /**
     * Saves the data of the Shop to its corresponding .yml file.
     */
    public abstract void saveShopData();

    /**
     * Creates the GUI where players will be able to execute commercial transactions on the shop.
     *
     * @return Inventory with all the existing Shop Items.
     */
    public abstract Inventory getShopGUI();

    /**
     * Creates the GUI where the Shop owner will be able to configure the items existing on the Shop GUI.
     *
     * @return Inventory with all the available configurations for the Shop.
     */
    public abstract Inventory getOwnerGUI();

    /**
     * Adds all the Machine Item's exhibition or configuration item to the passed inventory.
     *
     * @param inventory Inventory where all the Machine Items will be added.
     * @param isShopGUI True if the GUI that the items are being added to is the Shop GUI. False if it is the Owner GUI.
     */
    public void addShopItemsToInventory(Inventory inventory, boolean isShopGUI) {
        boolean isVendingMachine = shopType == ShopType.VENDING;
        for (ShopItem shopItem : shopItems) {
            if (inventory.firstEmpty() == -1) return;
            if (isShopGUI) {
                inventory.addItem(shopItem.getExhibitionItem(isVendingMachine));
            } else {
                inventory.addItem(shopItem.getConfigurationItem());
            }
        }
    }

    public List<Chest> getLinkedChests() {
        return linkedChests;
    }

    public boolean isChestLinked(Chest chest) {
        return linkedChests.contains(chest);
    }

    public void linkChest(Chest chest) {
        linkedChests.add(chest);
        saveShopData();
    }

    public void unlinkChest(Chest chest) {
        linkedChests.remove(chest);
        saveShopData();
    }

    public List<ShopItem> getShopItems() {
        return shopItems;
    }

    /**
     * Searches for a Shop Item that corresponds to the passed ItemStack within all the Shop Items of the Shop.
     *
     * @param item ItemStack that will be compared to the existed Machine Items.
     * @return ShopItem that corresponds the passed ItemStack, null if it couldn't be found.
     */
    public ShopItem getShopItem(ItemStack item) {
        UUID shopItemUuid = ShopItem.getUuidFromItemStack(item);
        if (shopItemUuid == null) return null;
        for (ShopItem shopItem : shopItems) {
            if (!shopItemUuid.equals(shopItem.getUuid())) continue;

            return shopItem;
        }
        return null;
    }

    public void addShopItem(ShopItem shopItem) {
        this.shopItems.add(shopItem);
        saveShopData();
    }

    public void removeShopItem(ShopItem shopItem) {
        this.shopItems.remove(shopItem);
        saveShopData();
    }

    /**
     * Iterates through all the Shop Items looking into every linked chest and updates the available stock of each
     * one of them.
     */
    public void updateShopItemsStock() {
        for (ShopItem shopItem : shopItems) {
            int shopItemStock = 0;
            for (Chest chest : linkedChests) {
                // Making sure there is still a chest in the location
                if (!(chest.getLocation().getBlock().getState() instanceof Chest)) continue;

                Inventory chestInventory = chest.getInventory();
                shopItemStock += InventoryUtil.countItem(shopItem.getItemStack().clone(), chestInventory);
            }
            shopItem.setStock(shopItemStock);
        }
    }

    /**
     * Goes through all the chest slots checking for the available inventory slots and compares it to the passed amount.
     *
     * @param shopItem      Shop Item that will be checked.
     * @param requiredSlots Amount of Shop Items to check.
     * @return True if there is enough inventory slots to store the passed amount.
     */
    public boolean canReceiveShopItemStock(ShopItem shopItem, int requiredSlots) {
        ItemStack shopItemStack = shopItem.getItemStack();
        int availableSlots = 0;
        for (Chest chest : linkedChests) {
            Inventory chestInventory = chest.getInventory();
            availableSlots += InventoryUtil.countAvailableSlots(shopItemStack, chestInventory);

            if (availableSlots >= requiredSlots) return true;
        }
        return false;
    }

    /**
     * Adds the passed amount of Shop Items to the linked chests.
     *
     * @param shopItem ShopItem that will be added to the linked chests.
     * @param amount   Amount of the MachineItem that will be added.
     */
    public void increaseShopItemStock(ShopItem shopItem, int amount) {
        ItemStack shopItemStack = shopItem.getItemStack();
        shopItemStack.setAmount(amount);
        for (Chest chest : linkedChests) {
            Inventory chestInventory = chest.getInventory();
            if (InventoryUtil.countAvailableSlots(shopItemStack, chestInventory) == 0) continue;

            Map<Integer, ItemStack> remainingItems = chestInventory.addItem(shopItemStack);
            if (remainingItems.isEmpty()) break;

            int remainingAmount = remainingItems.values().iterator().next().getAmount();
            shopItemStack.setAmount(remainingAmount);
        }
    }

    /**
     * Removes the passed amount of shop items from the linked chests.
     *
     * @param shopItem MachineItem that will be removed from the linked chests.
     * @param amount   Amount of the MachineItem that will be removed.
     */
    public void decreaseShopItemStock(ShopItem shopItem, int amount) {
        ItemStack shopItemStack = shopItem.getItemStack();
        shopItemStack.setAmount(amount);
        for (Chest linkedChest : linkedChests) {
            Inventory chestInventory = linkedChest.getInventory();
            if (!chestInventory.containsAtLeast(shopItemStack, 1)) continue;

            int availableChestStock = InventoryUtil.countItem(shopItemStack, chestInventory);
            chestInventory.removeItem(shopItemStack);
            if (availableChestStock < amount) {
                shopItemStack.setAmount(amount - availableChestStock);
                continue;
            }
            break;
        }
        updateShopItemsStock();
    }

    public ShopType getShopType() {
        return shopType;
    }
}
