package me.wmorales01.mycteriaeconomy.listeners;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.inventories.MachineHolder;
import me.wmorales01.mycteriaeconomy.inventories.ShopHolder;
import me.wmorales01.mycteriaeconomy.inventories.ShopItemEditorGUI;
import me.wmorales01.mycteriaeconomy.inventories.ShopItemEditorHolder;
import me.wmorales01.mycteriaeconomy.models.*;
import me.wmorales01.mycteriaeconomy.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ShopHandler implements Listener {
    private final MycteriaEconomy plugin;
    // Stores the players that are adding new Machine Items
    //           PlayerUUID
    private final Map<UUID, ShopItemAdder> shopItemAdders = new HashMap<>();
    // Stores the players that are editing a Machine Item
    //           PlayerUUID
    private final Map<UUID, ShopItemEditor> machineItemEditors = new HashMap<>();
    // Stores the players that are starting a transaction with a custom buy amount
    //           PlayerUUID
    private final Map<UUID, CustomAmountUser> customAmountUsers = new HashMap<>();
    // Stores the players that have been asked for confirmation when deleting a Machine or MachineItem
    private final Set<UUID> deletionConfirmations = new HashSet<>();

    public ShopHandler(MycteriaEconomy plugin) {
        this.plugin = plugin;
    }

    /**
     * Listens when a player clicks an inventory, if the inventory is related to a Shop then handle it according
     * to its InventoryHolder.
     *
     * @param event InventoryClickEvent
     */
    @EventHandler
    public void onMachineGUIClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof ShopHolder) {
            handleAbstractShopGUI(event);
        } else if (holder instanceof ShopItemEditorHolder) {
            handleShopItemEditorGUI(event);
        }
    }

    /**
     * Handles all the clicks made on the Machine GUI differently depending on the type of the clicked GUI
     *
     * @param event InventoryClickEvent
     */
    private void handleAbstractShopGUI(InventoryClickEvent event) {
        ShopHolder shopHolder = (ShopHolder) event.getInventory().getHolder();
        if (shopHolder.isShopGUI()) {
            handleShopGUI(event);
            return;
        }
        handleShopOwnerGUI(event);
    }

    /**
     * Handles all the required operations when clicking on a Machine's Shop GUI.
     *
     * @param event InventoryClickEvent
     */
    private void handleShopGUI(InventoryClickEvent event) {
        if (isWalletClick(event)) return;

        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        AbstractShop shop = ((ShopHolder) event.getInventory().getHolder()).getShop();
        ShopItem shopItem = shop.getShopItem(clickedItem);
        if (shopItem == null) return;

        Inventory inventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        ClickType clickType = event.getClick();
        int transactionAmount;
        if (clickType.isLeftClick() && clickType.isShiftClick()) {
            CustomAmountUser customAmountUser = new CustomAmountUser(shopItem, shop, inventory);
            customAmountUsers.put(player.getUniqueId(), customAmountUser);
            player.closeInventory();
            player.updateInventory();
            Messager.sendMessage(player, "&eEnter the transaction amount as a message in the chat.\n" +
                    "You can cancel this operation in any moment by typing &ocancel&e.");
            SFXManager.playPlayerSound(player, Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 0.6F, 1.4F);
            return;
        } else if (clickType.isRightClick()) {
            transactionAmount = 64;
        } else if (clickType.isLeftClick()) {
            transactionAmount = 1;
        } else {
            return;
        }
        if (shop.getShopType() == ShopType.VENDING) {
            buyShopItem(player, shopItem, shop, transactionAmount, inventory);
        } else {
            sellShopItem(player, shopItem, shop, transactionAmount, inventory);
        }
    }

    /**
     * Determines if the player clicked a Wallet on the inventory or on the Wallet slot of the Machine GUI.
     *
     * @param event InventoryClickEvent.
     * @return True if the player clicked a Wallet on its Inventory or with a Wallet on its cursor.
     */
    private boolean isWalletClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        ItemStack cursorItem = player.getItemOnCursor();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedInventory.getType() != InventoryType.PLAYER && event.getSlot() != clickedInventory.getSize() - 5) {
            return false;
        }
        return Wallet.isWallet(clickedItem) || Wallet.isWallet(cursorItem);
    }

    /**
     * Purchases the passed MachineItem from the passed VendingMachine if the machine has enough stock and the
     * player has enough cash.
     *
     * @param player            Player that is executing the transaction.
     * @param shopItem          MachineItem involved in the transaction.
     * @param shop              Shop where the transaction is being made.
     * @param transactionAmount Amount of the transaction.
     * @param inventory         Inventory where the transaction is taking place.
     */
    private void buyShopItem(Player player, ShopItem shopItem, AbstractShop shop,
                             int transactionAmount, Inventory inventory) {
        // Checking if the machine has enough stock
        transactionAmount *= shopItem.getSellAmount();
        int machineItemStock = shopItem.getStock();
        if (machineItemStock < transactionAmount) {
            Messager.sendErrorMessage(player, "&cThere's not enough stock to buy this amount of items.");
            return;
        }
        // Calculating price of the transaction
        double transactionPrice = shopItem.getPrice() * transactionAmount;
        int walletSlot = inventory.getSize() - 5;
        ItemStack walletItemStack = inventory.getItem(walletSlot);
        Wallet wallet = Wallet.fromItemStack(walletItemStack);
        // Getting balance from either the player inventory or placed wallet if available
        double availableCash;
        if (wallet == null) {
            availableCash = BalanceUtil.computeInventoryBalance(player.getInventory());
        } else {
            availableCash = wallet.getBalance();
        }
        // Checking if the player has enough cash
        if (availableCash < transactionPrice) {
            Messager.sendErrorMessage(player, "&cYou don't have enough funds to execute this transaction.");
            return;
        }
        // Executing Transaction
        shop.decreaseShopItemStock(shopItem, transactionAmount);
        ItemStack purchasedItemStack = shopItem.getItemStack();
        purchasedItemStack.setAmount(transactionAmount);
        if (wallet == null) {
            BalanceUtil.removeBalance(player.getInventory(), transactionPrice);
        } else {
            wallet.decreaseBalance(transactionPrice);
            inventory.setItem(walletSlot, wallet.getItemStack());
        }
        InventoryUtil.giveItem(player, purchasedItemStack);
        ((VendingShop) shop).increaseProfit(transactionPrice);
        // Get and open the updated Vending Machine GUI
        Inventory newShopGUI = shop.getShopGUI();
        if (wallet != null) {
            // Adding the current wallet to the new inventory
            newShopGUI.setItem(walletSlot, inventory.getItem(walletSlot));
        }
        player.openInventory(newShopGUI);
        SFXManager.playPlayerSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 0.6F, 1.4F);
    }

    /**
     * Sells the passed Shop Item to the passed machine. The sold shop items will be taken from the passed
     * player's inventory.
     *
     * @param player            Player that is selling the Machine Items.
     * @param shopItem          Machine Item that is being sold.
     * @param shop              Shop that is receiving the Shop Item.
     * @param transactionAmount Amount of Shop Items that are being sold.
     * @param inventory         Shop's Inventory.
     */
    private void sellShopItem(Player player, ShopItem shopItem, AbstractShop shop,
                              int transactionAmount, Inventory inventory) {
        TradingShop tradingShop = (TradingShop) shop;
        // Checking if the machine has enough storage
        transactionAmount *= shopItem.getSellAmount();
        if (!shop.canReceiveShopItemStock(shopItem, transactionAmount)) {
            Messager.sendErrorMessage(player, "&cThere's not enough storage in order for this machine to receive " +
                    "this amount of items.");
            return;
        }
        // Checking if player has enough of the item that is being sold
        ItemStack soldItemStack = shopItem.getItemStack();
        if (!player.getInventory().containsAtLeast(soldItemStack, transactionAmount)) {
            Messager.sendErrorMessage(player, "&cYou don't have enough items to sell this amount to the machine.");
            return;
        }
        // Calculating transaction price and checking if the machine has enough balance for it
        double transactionPrice = shopItem.getPrice() * transactionAmount;
        if (tradingShop.getBalance() < transactionPrice) {
            Messager.sendErrorMessage(player, "&cThere's not enough cash on the machine to purchase this amount of " +
                    "items.");
            return;
        }
        // Executing transaction
        shop.increaseShopItemStock(shopItem, transactionAmount);
        int walletSlot = inventory.getSize() - 5;
        ItemStack walletItemStack = inventory.getItem(walletSlot);
        Wallet wallet = Wallet.fromItemStack(walletItemStack);
        tradingShop.decreaseBalance(transactionPrice);
        // Adding cash to the player's inventory or the placed wallet if available
        if (wallet == null) {
            BalanceUtil.giveBalance(player, transactionPrice);
        } else {
            wallet.increaseWalletBalance(transactionPrice);
            inventory.setItem(walletSlot, wallet.getItemStack());
        }
        soldItemStack.setAmount(transactionAmount);
        player.getInventory().removeItem(soldItemStack);
        // Get and open the updated Vending Machine GUI
        Inventory newShopGUI = shop.getShopGUI();
        if (wallet != null) {
            // Adding the current wallet to the new inventory
            newShopGUI.setItem(walletSlot, inventory.getItem(walletSlot));
        }
        player.openInventory(newShopGUI);
        SFXManager.playPlayerSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 0.6F, 1.4F);
    }

    /**
     * Handles all the required operations when clicking on a Machine's Owner GUI.
     *
     * @param event InventoryClickEvent
     */
    private void handleShopOwnerGUI(InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        AbstractShop shop = ((ShopHolder) inventory.getHolder()).getShop();
        Inventory clickedInventory = event.getClickedInventory();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir() || GUIUtil.isFiller(clickedItem)) return;
        if (clickedInventory.getType() == InventoryType.PLAYER) { // Adding new Machine Item
            if (CurrencyItem.isCurrencyItem(clickedItem) && shop instanceof TradingShop) {
                // Adding balance to the machine
                addTradingMachineBalance(player, shop, clickedItem);
                return;
            }
            startShopItemAdd(player, shop, clickedItem);
            return;
        }
        if (event.getSlot() == inventory.getSize() - 5) { // Clicking profits item
            collectShopBalance(player, shop);
            return;
        }
        ClickType clickType = event.getClick();
        if (clickType.isRightClick()) { // Is deleting Machine Item
            deleteShopItem(player, shop, clickedItem);
            return;
        }
        // Is modifying Machine Item
        ShopItem shopItem = shop.getShopItem(clickedItem);
        if (shopItem == null) return;

        player.openInventory(new ShopItemEditorGUI().getGUI(shopItem, shop));
        SFXManager.playPlayerSound(player, Sound.UI_BUTTON_CLICK, 0.6F, 1.4F);
    }

    /**
     * Gets the value of the passed currency item and adds it to the passed trading shop.
     *
     * @param player       Player that is adding Trading Shop balance.
     * @param shop         TradingShop that is receiving balance.
     * @param currencyItem Currency item that is being inserted into the Shop.
     */
    private void addTradingMachineBalance(Player player, AbstractShop shop, ItemStack currencyItem) {
        double currencyItemValue = CurrencyItem.getValueFromItem(currencyItem);
        currencyItemValue *= currencyItem.getAmount();
        currencyItem.setAmount(0);
        ((TradingShop) shop).increaseBalance(currencyItemValue);
        player.openInventory(shop.getOwnerGUI());
        SFXManager.playPlayerSound(player, Sound.ITEM_ARMOR_EQUIP_CHAIN, 0.6F, 1.4F);
    }

    /**
     * Checks if the Shop can hold new Shop Items, if it can then create a new ShopItemAdder instance and
     * store it in the shopItemAdders local Map
     *
     * @param player The player that is adding the new Shop Item.
     * @param shop   The shop that will receive a new Shop Item.
     * @param item   The ItemStack that will be turned into a Shop Item.
     */
    private void startShopItemAdd(Player player, AbstractShop shop, ItemStack item) {
        if (Wallet.isWallet(item) || CurrencyItem.isCurrencyItem(item)) {
            Messager.sendErrorMessage(player, "&cYou can't put this item in the market.");
            return;
        }
        // Checking if the machine can hold more machine items
        int machineItemAmount = shop.getShopItems().size();
        if (machineItemAmount == 28) { // The machine can't hold more items
            Messager.sendErrorMessage(player, "&cThis commerce can't hold more items.");
            return;
        }
        UUID playerUuid = player.getUniqueId();
        if (shopItemAdders.containsKey(playerUuid)) {
            Messager.sendErrorMessage(player, "&cYou are already adding items to a commerce.");
            return;
        }
        ShopItemAdder shopItemAdder = new ShopItemAdder(shop, item.clone());
        shopItemAdders.put(playerUuid, shopItemAdder);
        player.closeInventory();
        Messager.sendMessage(player, "&eType the Sell Amount and Price of this item as a message in the chat " +
                "following the format: &l<SellAmount> - <Price>." +
                "\n&eExample: &l32 - 100\n" +
                "&eYou can cancel this operation any time by typing &ocancel &einstead.");
        SFXManager.playPlayerSound(player, Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 0.6F, 1.3F);
    }

    /**
     * Gives the collected balance of the passed Shop to the passed Player in currency items.
     *
     * @param player Player that will receive the Shop Balance.
     * @param shop   Shop which balance will be withdrawn.
     */
    private void collectShopBalance(Player player, AbstractShop shop) {
        double shopBalance;
        if (shop instanceof VendingShop) {
            VendingShop vendingShop = (VendingShop) shop;
            shopBalance = vendingShop.getProfit();
            vendingShop.setProfit(0);
        } else if (shop instanceof TradingShop) {
            TradingShop tradingShop = (TradingShop) shop;
            shopBalance = tradingShop.getBalance();
            tradingShop.setBalance(0);
        } else {
            return;
        }
        if (shopBalance == 0) {
            Messager.sendErrorMessage(player, "&cThere's no balance to withdraw.");
            return;
        }
        // Convert the balance to currency
        List<ItemStack> currencyItems = BalanceUtil.balanceToCurrency(shopBalance);
        InventoryUtil.giveItems(player, currencyItems);
        player.openInventory(shop.getOwnerGUI());
        SFXManager.playPlayerSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 0.6F, 1.3F);
    }

    /**
     * Attempts to delete the passed ItemStack from the passed Shop if the item corresponds to a Shop Item.
     * If it is the first time the player attempts to delete the item then ask for confirmation first and add it to
     * the deletionConfirmations Set. After that allow the player to normally delete the Shop Item.
     *
     * @param player The player that is deleting the Shop Item.
     * @param shop   The shop that is getting a Shop Item deleted.
     * @param item   The ItemStack corresponding to the Shop Item that is going to be deleted.
     */
    private void deleteShopItem(Player player, AbstractShop shop, ItemStack item) {
        ShopItem shopItem = shop.getShopItem(item);
        if (shopItem == null) return;

        UUID playerUuid = player.getUniqueId();
        if (!deletionConfirmations.contains(playerUuid)) {
            deletionConfirmations.add(playerUuid);
            Bukkit.getScheduler().runTaskLater(plugin, () -> deletionConfirmations.remove(playerUuid), 100L);
            Messager.sendMessage(player, "&eRight click again to confirm the delete operation.");
            SFXManager.playPlayerSound(player, Sound.UI_BUTTON_CLICK, 0.6F, 1.4F);
            return;
        }
        deletionConfirmations.remove(playerUuid);
        shop.removeShopItem(shopItem);
        player.openInventory(shop.getOwnerGUI());
        Messager.sendSuccessMessage(player, "&aMachine Item successfully deleted.");
    }

    /**
     * Handles all the operations regarding the Shop Item editor GUI.
     * If a player clicks the back button then take it back to the Shop Owner GUI.
     * If a player clicks a Property Item then start modifying that Shop Item with a ShopItemEditor instance.
     *
     * @param event InventoryClickEvent
     */
    private void handleShopItemEditorGUI(InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ShopItemEditorHolder editorHolder = (ShopItemEditorHolder) event.getInventory().getHolder();
        AbstractShop shop = editorHolder.getShop();
        int clickedSlot = event.getSlot();
        if (clickedSlot == 0) { // Pressing back button
            player.openInventory(shop.getOwnerGUI());
            SFXManager.playPlayerSound(player, Sound.UI_BUTTON_CLICK, 0.6F, 0.7F);
            return;
        }
        // Editing Machine Item Property
        if (clickedSlot != 12 && clickedSlot != 14) return;
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;
        ShopItemProperty clickedProperty = ShopItemProperty.fromIcon(clickedItem.getType());
        if (clickedProperty == null) return;

        ShopItem shopItem = editorHolder.getShopItem();
        ShopItemEditor shopItemEditor = new ShopItemEditor(shopItem, shop, clickedProperty);
        machineItemEditors.put(player.getUniqueId(), shopItemEditor);
        player.closeInventory();
        Messager.sendMessage(player, "&eEnter the new value of &l" + StringUtil.formatEnum(clickedProperty) +
                " &eas a message in the chat.\n" +
                "You can cancel this operation in any moment by typing &ocancel &einstead.");
        SFXManager.playPlayerSound(player, Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 0.6F, 1.3F);
    }

    /**
     * Listens when an inventory is closed, if it is a Machine GUI and it has a wallet then give the wallet to
     * the player.
     *
     * @param event InventoryCloseEvent.
     */
    @EventHandler
    public void onMachineGUIClose(InventoryCloseEvent event) {
        Inventory closedInventory = event.getInventory();
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof MachineHolder)) return;
        if (!((MachineHolder) holder).isShopGUI()) return;

        Player player = (Player) event.getPlayer();
        // Checking one tic later if the open inventory is another Machine GUI, if it isn't give the player the
        // previous wallet if available
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Inventory newGui = player.getOpenInventory().getTopInventory();
            if (newGui.getHolder() instanceof MachineHolder) return;

            int walletSlot = closedInventory.getSize() - 5;
            ItemStack wallet = closedInventory.getItem(walletSlot);
            if (wallet == null || wallet.getType().isAir()) return;

            InventoryUtil.giveItem(player, closedInventory.getItem(walletSlot));
        }, 0L);
    }

    /**
     * Listens when a player sends a message in the chat. If the player's UUID is in the machineItemsAdder Map
     * then procceed with the Machine Item add process.
     *
     * @param event AsyncPlayerChatEvent.
     */
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerUuid = player.getUniqueId();
        if (shopItemAdders.containsKey(playerUuid)) {
            event.setCancelled(true);
            addMachineItem(player, event.getMessage().trim());
        } else if (machineItemEditors.containsKey(playerUuid)) {
            event.setCancelled(true);
            modifyMachineItem(player, event.getMessage().trim());
        } else if (customAmountUsers.containsKey(playerUuid)) {
            event.setCancelled(true);
            executeCustomAmountTransaction(player, event.getMessage().trim());
        }
    }

    /**
     * Handles the Machine Item creation process based on the message sent by the player.
     *
     * @param player  Player that is adding a new Machine Item.
     * @param message Message that contains the parameters for the new Machine Item.
     */
    private void addMachineItem(Player player, String message) {
        UUID playerUuid = player.getUniqueId();
        ShopItemAdder shopItemAdder = shopItemAdders.get(playerUuid);
        AbstractShop shop = shopItemAdder.getShop();
        if (message.equalsIgnoreCase("cancel")) {
            shopItemAdders.remove(playerUuid);
            Bukkit.getScheduler().runTask(plugin, () -> player.openInventory(shop.getOwnerGUI()));
            Messager.sendErrorMessage(player, "&aOperation canceled.");
            return;
        }
        String[] parameters = message.split("-");
        if (parameters.length == 1 || parameters.length > 2) {
            Messager.sendErrorMessage(player, "&cBe sure that you are using the proper format to add a Machine Item.");
            return;
        }
        String sellAmountString = parameters[0].trim();
        Integer sellAmount = StringUtil.parseInteger(player, sellAmountString);
        if (sellAmount == null) return;
        if (sellAmount < 1 || sellAmount > 64) {
            Messager.sendErrorMessage(player, "&cSell amount must be between 1 and 64.");
            return;
        }
        String priceString = parameters[1].trim();
        Double price = StringUtil.parseDouble(player, priceString);
        if (price == null) return;
        if (price < 0) {
            Messager.sendErrorMessage(player, "&cPrice can't be lower than 0.");
            return;
        }
        ItemStack newItem = shopItemAdder.getNewItem();
        newItem.setAmount(1);
        ShopItem shopItem = new ShopItem(newItem, price, sellAmount);
        shop.addShopItem(shopItem);
        shopItemAdders.remove(playerUuid);
        Bukkit.getScheduler().runTask(plugin, () -> player.openInventory(shop.getOwnerGUI()));
        Messager.sendSuccessMessage(player, "&aNew Machine Item successfully added.");
    }

    /**
     * Handles the Machine Item edit process based on the message the player sent.
     *
     * @param player  Player that is editing the Machine Item.
     * @param message Message that contains the parameters of the item edition.
     */
    private void modifyMachineItem(Player player, String message) {
        UUID playerUuid = player.getUniqueId();
        ShopItemEditor shopItemEditor = machineItemEditors.get(playerUuid);
        ShopItem shopItem = shopItemEditor.getMachineItem();
        AbstractShop shop = shopItemEditor.getShop();
        if (message.equalsIgnoreCase("cancel")) {
            Bukkit.getScheduler().runTask(plugin, () -> player.openInventory(shop.getOwnerGUI()));
            Messager.sendErrorMessage(player, "&aOperation canceled.");
            return;
        }
        if (message.split(" ").length > 1) { // Message contains more than one word
            Messager.sendErrorMessage(player, "&cBe sure to send only the new value of the Machine Item's property.");
            return;
        }
        ShopItemProperty modifiedProperty = shopItemEditor.getMachineItemProperty();
        boolean couldModifyProperty;
        switch (modifiedProperty) {
            case SELL_AMOUNT:
                couldModifyProperty = modifyMachineItemSellAmount(player, message, shopItem);
                break;
            case PRICE:
                couldModifyProperty = modifyMachineItemPrice(player, message, shopItem);
                break;
            default:
                return;
        }
        if (!couldModifyProperty) return;

        machineItemEditors.remove(playerUuid);
        Bukkit.getScheduler().runTask(plugin, () -> player.openInventory(shop.getOwnerGUI()));
        Messager.sendSuccessMessage(player, "&a&l" + StringUtil.formatEnum(modifiedProperty) + " &asuccessfully " +
                "modified.");
    }

    /**
     * Modifies the passed Machine Item's sell amount based on the content of the sent message
     *
     * @param player   Player that is modifying the Machine Item.
     * @param message  Message that contains the new parameters for the sell amount.
     * @param shopItem Machine Item which sell amount will be modified.
     * @return True if the sell amount could be modified.
     */
    private boolean modifyMachineItemSellAmount(Player player, String message, ShopItem shopItem) {
        Integer sellAmount = StringUtil.parseInteger(player, message);
        if (sellAmount == null) return false;
        if (sellAmount < 1 || sellAmount > 64) {
            Messager.sendErrorMessage(player, "&cSell amount must be between 1 and 64.");
            return false;
        }
        shopItem.setSellAmount(sellAmount);
        return true;
    }

    /**
     * Modifies the passed Machine Item's price based on the content of the sent message
     *
     * @param player   Player that is modifying the price of the Machine Item.
     * @param message  Message that contains the parameters for the new Machine Item price.
     * @param shopItem Machine Item which price will be modified.
     * @return True if the price could be modified.
     */
    private boolean modifyMachineItemPrice(Player player, String message, ShopItem shopItem) {
        Double price = StringUtil.parseDouble(player, message);
        if (price == null) return false;
        if (price < 0) {
            Messager.sendErrorMessage(player, "&cPrice can't be lower than 0.");
            return false;
        }
        shopItem.setPrice(price);
        return true;
    }

    /**
     * Executes a transaction with a custom amount based on the passed message sent by the passed player.
     *
     * @param player  Player that is executing the custom transaction.
     * @param message Chat message the player sent as the arguments of the transaction.
     */
    private void executeCustomAmountTransaction(Player player, String message) {
        UUID playerUuid = player.getUniqueId();
        CustomAmountUser customAmountUser = customAmountUsers.get(playerUuid);
        AbstractShop shop = customAmountUser.getShop();
        if (message.equalsIgnoreCase("cancel")) {
            customAmountUsers.remove(playerUuid);
            Messager.sendErrorMessage(player, "&aOperation canceled.");
            Bukkit.getScheduler().runTask(plugin, () -> player.openInventory(shop.getShopGUI()));
            return;
        }
        Integer transactionAmount = StringUtil.parseInteger(player, message);
        if (transactionAmount == null) return;
        if (transactionAmount <= 0) {
            Messager.sendErrorMessage(player, "&cTransaction amount must be higher than 0.");
            return;
        }
        customAmountUsers.remove(playerUuid);
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (shop instanceof VendingShop) {
                buyShopItem(player, customAmountUser.getShopItem(), shop, transactionAmount, customAmountUser.getShopInventory());
            } else {
                sellShopItem(player, customAmountUser.getShopItem(), shop, transactionAmount, customAmountUser.getShopInventory());
            }
            player.openInventory(shop.getShopGUI());
        });
    }

    /**
     * Listens when a player breaks a block, if the block is a Machine and the breaker is the owner or an admin delete
     * the machine from the database.
     *
     * @param event BlockBreakEvent.
     */
    @EventHandler
    public void onMachineBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material blockType = block.getType();
        if (blockType != Material.DROPPER && blockType != Material.DISPENSER) return;
        AbstractMachine machine = AbstractMachine.fromLocation(block.getLocation());
        if (machine == null) return;

        deleteMachine(event.getPlayer(), machine, event);
    }

    /**
     * Attempts to delete the passed Machine, if the player isn't the owner of the machine and doesn't have admin
     * permissions then do nothing.
     * <p>
     * If the player hasn't been asked for confirmation then ask for it, if it has then delete the Machine.
     *
     * @param player  Player that is deleting the Machine.
     * @param machine Machine to be deleted.
     * @param event   BlockBreakEvent.
     */
    private void deleteMachine(Player player, AbstractMachine machine, Cancellable event) {
        UUID playerUuid = player.getUniqueId();
        if (!machine.getOwnerUuid().equals(playerUuid) && !player.hasPermission("mycteriaeconomy.admin")) {
            event.setCancelled(true);
            return;
        }
        if (!deletionConfirmations.contains(playerUuid)) {
            event.setCancelled(true);
            deletionConfirmations.add(playerUuid);
            Bukkit.getScheduler().runTaskLater(plugin, () -> deletionConfirmations.remove(playerUuid), 100L);
            Messager.sendMessage(player, "&eBreak this block again to confirm the Machine's deletion.");
            SFXManager.playPlayerSound(player, Sound.UI_BUTTON_CLICK, 0.6F, 1.4F);
            return;
        }
        deletionConfirmations.remove(playerUuid);
        machine.deleteMachine();
        Messager.sendSuccessMessage(player, "&aMachine successfully deleted.");
        SFXManager.playWorldSound(machine.getMachineLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 0.8F, 1.3F);
    }

    private class CustomAmountUser {
        private final ShopItem shopItem;
        private final AbstractShop shop;
        private final Inventory shopInventory;

        public CustomAmountUser(ShopItem shopItem, AbstractShop shop, Inventory shopInventory) {
            this.shopItem = shopItem;
            this.shop = shop;
            this.shopInventory = shopInventory;
        }

        public ShopItem getShopItem() {
            return shopItem;
        }

        public AbstractShop getShop() {
            return shop;
        }

        public Inventory getShopInventory() {
            return shopInventory;
        }
    }
}
