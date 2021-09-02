package me.wmorales01.mycteriaeconomy.listeners;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import me.wmorales01.mycteriaeconomy.events.RightClickNPCEvent;
import me.wmorales01.mycteriaeconomy.models.NPCManager;
import me.wmorales01.mycteriaeconomy.models.NPCShop;
import me.wmorales01.mycteriaeconomy.models.NPCTool;
import me.wmorales01.mycteriaeconomy.util.Messager;
import me.wmorales01.mycteriaeconomy.util.SFXManager;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntity;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityHeadRotation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class NPCShopHandler implements Listener {
    private MycteriaEconomy plugin;
    private Set<Player> deleteConfirmations;

    public NPCShopHandler(MycteriaEconomy plugin) {
        this.plugin = plugin;
        this.deleteConfirmations = new HashSet<>();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ())
            return;

        for (NPCShop shop : plugin.getNpcShops()) {
            EntityPlayer npc = shop.getNpc();
            net.minecraft.server.v1_16_R3.PlayerConnection connection = ((CraftPlayer) player)
                    .getHandle().playerConnection;
            Location npcLocation = npc.getBukkitEntity().getLocation();
            Location newNpcLocation = npcLocation.setDirection(player.getLocation().subtract(npcLocation).toVector());
            float yaw = newNpcLocation.getYaw();
            float pitch = newNpcLocation.getPitch();
            connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(npc.getId(),
                    (byte) ((yaw % 360.) * 256 / 360), (byte) ((pitch % 360.) * 256 / 360), false));
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) ((yaw % 360.) * 256 / 360)));
        }
    }

    @EventHandler
    public void onNPCRightClick(RightClickNPCEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = NPCTool.getItemStack();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();
        NPCShop shop = NPCShop.getByEntityPlayer(event.getNpc());
        if (shop == null)
            return;
        if ((mainHand != null && mainHand.isSimilar(tool)) || (offHand != null && offHand.isSimilar(tool))) {
            // Admin action
            if (player.isSneaking()) {
                // Configuring NPC
                if (!shop.isWorking()) {
                    Messager.sendErrorMessage(player, "&cThis shop is out of service.");
                    return;
                }
                shop.openOwnerGUI(player);

            } else {
                // Deleting NPC
                if (!deleteConfirmations.contains(player)) {
                    deleteConfirmations.add(player);
                    Messager.sendMessage(player, "&6Right click me again to confirm the deletion.");
                    SFXManager.playPlayerSound(player, Sound.UI_BUTTON_CLICK, 1, 1.5f);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> deleteConfirmations.remove(player), 100);
                    return;
                }
                new NPCManager(plugin).deleteNPC(shop, true);
                Messager.sendSuccessMessage(player, "&aNPC successfully deleted.");
            }

        } else {
            if (plugin.getNpcLinkers().containsKey(player)) {
                Location chestLocation = plugin.getNpcLinkers().get(player).getLocation();
                plugin.getNpcLinkers().remove(player);
                if (shop.getChestLocations().contains(chestLocation)) {
                    Messager.sendErrorMessage(player, "&cThis chest is already linked to this NPC.");
                    return;
                }
                shop.getChestLocations().add(chestLocation);
                Messager.sendSuccessMessage(player, "&aChest linked to NPC successfully.");
                return;
            }
            if (!shop.isWorking()) {
                Messager.sendErrorMessage(player, "&cThis shop is out of service.");
                return;
            }
            shop.openSellGUI(player);
        }
        SFXManager.playPlayerSound(player, Sound.UI_BUTTON_CLICK, 1, 1);
    }

	/*@EventHandler
	public void onNPCShopGUIClick(InventoryClickEvent event) {
		Inventory inventory = event.getInventory();
		Inventory clickedInventory = event.getClickedInventory();
		if (inventory == null || clickedInventory == null)
			return;
		if (!(inventory.getHolder() instanceof NPCShopHolder))
			return;

		event.setCancelled(true);
		Player player = (Player) event.getWhoClicked();
		ItemStack clickedItem = event.getCurrentItem();
		if (clickedItem == null || clickedItem.getType().isAir() || Checker.isFrame(clickedItem))
			return;

		NPCShopHolder holder = (NPCShopHolder) inventory.getHolder();
		NPCShop shop = holder.getNpcShop();
		boolean isConfiguring = holder.isConfiguring();
		if (isConfiguring) { // Configuring
			if (clickedInventory.getType() != InventoryType.PLAYER) { // Stock inventory click
				MachineItem machineItem = shop.getMachineItem(clickedItem.clone());
				if (machineItem == null)
					return;
				if (event.getClick().isLeftClick()) { // Open stock
					player.openInventory(shop.getItemStockGUI(machineItem));
					SFXManager.playPlayerSound(player, Sound.UI_BUTTON_CLICK, 1, 1);

				} else if (event.getClick().isRightClick()) { // Delete stock
					if (machineItem.getStockAmount() > 0) {
						Messager.sendErrorMessage(player,
								"&cYou need to empty the stock before deleting an item from the shop.");
						return;
					}
					if (!deleteConfirmations.contains(player)) {
						deleteConfirmations.add(player);
						Bukkit.getScheduler().runTaskLater(plugin, () -> deleteConfirmations.remove(player), 100);
						return;
					}
					shop.removeStock(machineItem);
					SFXManager.playPlayerSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 5);
				}

			} else { // Player inventory click
				if (inventory.firstEmpty() == -1) {
					Messager.sendErrorMessage(player, "&cThis NPC can't hold any more items to sell.");
					return;
				}
				addStockToMachine(player, shop, event);
			}

		} else { // Buying
			if (clickedInventory.getType() != InventoryType.PLAYER) // Shop inventory click
				buyItem(player, holder, shop, event);
			else // Player inventory click
				depositBalance(player, holder, event);
		}
	}

	private void addStockToMachine(Player player, NPCShop shop, InventoryClickEvent event) {
		if (event.getCurrentItem() == null)
			return;
		ItemStack clickedItem = event.getCurrentItem().clone();
		if (Checker.isBill(clickedItem) || Checker.isCoin(clickedItem))
			return;

		clickedItem.setAmount(1);
		NPCOperator operator = new NPCOperator(player, clickedItem, shop);
		plugin.getNpcOperators().add(operator);
		Messager.sendMessage(player, "&aType the sell amount and price for this item in chat separated by \"-\".\n"
				+ "&lEx. &a64 - 200\n" + "&6You can type \"cancel\" in any moment to cancel the process.");
		player.closeInventory();
	}

	private void depositBalance(Player player, NPCShopHolder holder, InventoryClickEvent event) {
		ItemStack clickedItem = event.getCurrentItem();
		double value = 0;
		if (Checker.isBill(clickedItem))
			value = Getter.getValueFromBill(clickedItem);
		else if (Checker.isCoin(clickedItem))
			value = Getter.getValueFromCoin(clickedItem);
		else
			return;

		value += holder.getBalance();
		holder.setBalance(value);
		player.getInventory().setItem(event.getSlot(), null);
		updateBalanceItem(event.getInventory().getItem(48), value);
		SFXManager.playPlayerSound(player, Sound.UI_BUTTON_CLICK, 1, 3);
	}

	private void buyItem(Player player, NPCShopHolder holder, NPCShop shop, InventoryClickEvent event) {
		Inventory inventory = event.getInventory();
		Inventory clickedInventory = event.getClickedInventory();
		double balance = holder.getBalance();
		ItemStack clickedItem = event.getCurrentItem();

		if (clickedItem == null)
			return;
		if (Checker.isFrame(clickedItem))
			return;
		MachineItem machineItem = shop.getMachineItem(clickedItem.clone());
		if (machineItem == null)
			return;

		int itemAmount = machineItem.getStockAmount();
		int sellAmount = machineItem.getSellAmount();
		if (itemAmount <= 0 || itemAmount - sellAmount < 0) {
			Messager.sendMessage(player, "&cThis item is out of stock.");
			SFXManager.playPlayerSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 2);
			return;
		}
		double itemPrice = machineItem.getPrice();
		if (balance < itemPrice) {
			Messager.sendMessage(player, "&cYou don't have enough funds to execute this transaction.");
			SFXManager.playPlayerSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 2);
			return;
		}
		balance -= itemPrice;
		holder.setBalance(balance);
		updateBalanceItem(inventory.getItem(48), balance);
		clickedInventory.setItem(event.getSlot(), machineItem.getSellItem(true));

		ItemStack purchasedItem = machineItem.getItemStack().clone();
		purchasedItem.setAmount(sellAmount);
		machineItem.setStockAmount(machineItem.getStockAmount() - sellAmount);
		player.getInventory().addItem(purchasedItem);
		SFXManager.playPlayerSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 2);
	}

	private void updateBalanceItem(ItemStack item, double newBalance) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eBalance: " + newBalance));
		item.setItemMeta(meta);
	}*/
}
