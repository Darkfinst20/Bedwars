package net.jandie1505.bedwars;

import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.menu.ShopEntry;
import net.jandie1505.bedwars.game.menu.ShopMenu;
import net.jandie1505.bedwars.game.menu.UpgradeEntry;
import net.jandie1505.bedwars.game.player.PlayerData;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EventListener implements Listener {
    private final Bedwars plugin;

    public EventListener(Bedwars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {

        if (this.plugin.getGame() instanceof Game && event.getEntity() instanceof Player && ((Game) this.plugin.getGame()).getPlayers().containsKey(event.getEntity().getUniqueId())) {

            PlayerData playerData = ((Game) this.plugin.getGame()).getPlayers().get(event.getEntity().getUniqueId());

            playerData.setAlive(false);

            if (Boolean.FALSE.equals(((Player) event.getEntity()).getPlayer().getWorld().getGameRuleValue(GameRule.DO_IMMEDIATE_RESPAWN))) {
                this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                    ((Player) event.getEntity()).spigot().respawn();
                }, 1);
            }

        }

    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        if (this.plugin.getGame() instanceof Game && ((Game) this.plugin.getGame()).getPlayers().containsKey(event.getPlayer().getUniqueId())) {

            Location location = event.getPlayer().getLocation();

            if (location.getY() < -64) {
                location.setY(-64);
            }

            event.setRespawnLocation(location);

        }

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        if (this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) {
            return;
        }

        if (this.plugin.getGame() instanceof Game) {

            if (!((Game) this.plugin.getGame()).getPlayers().containsKey(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
                return;
            }

            ((Game) this.plugin.getGame()).getPlayerPlacedBlocks().add(event.getBlockPlaced().getLocation());

        } else {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        if (this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) {

            if (this.plugin.getGame() instanceof Game) {
                ((Game) this.plugin.getGame()).getPlayerPlacedBlocks().remove(event.getBlock().getLocation());
            }

            return;
        }

        if (this.plugin.getGame() instanceof Game) {

            if (!((Game) this.plugin.getGame()).getPlayers().containsKey(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
                return;
            }

            if (((Game) this.plugin.getGame()).getPlayerPlacedBlocks().contains(event.getBlock().getLocation()) || event.getBlock().getBlockData() instanceof Bed) {

                PlayerData playerData = ((Game) this.plugin.getGame()).getPlayers().get(event.getPlayer().getUniqueId());

                if (event.getBlock().getBlockData() instanceof Bed) {

                    for (Location location : ((Game) this.plugin.getGame()).getTeams().get(playerData.getTeam()).getBedLocations()) {

                        Block otherHalf;

                        if (((Bed) event.getBlock().getBlockData()).getPart() == Bed.Part.HEAD) {
                            otherHalf = event.getBlock().getRelative(((Bed) event.getBlock().getBlockData()).getFacing().getOppositeFace());
                        } else {
                            otherHalf = event.getBlock().getRelative(((Bed) event.getBlock().getBlockData()).getFacing());
                        }

                        if (location.equals(event.getBlock().getLocation()) || location.equals(otherHalf.getLocation())) {

                            event.getPlayer().sendMessage("§cYou cannot break your own bed");
                            event.setCancelled(true);
                            return;
                        }

                    }
                }

                ((Game) this.plugin.getGame()).getPlayerPlacedBlocks().remove(event.getBlock().getLocation());

            } else {

                event.setCancelled(true);
                event.getPlayer().sendMessage("§cYou only can break blocks placed by a player");

            }

        } else {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

        if (this.plugin.getGame() instanceof Game && ((Game) this.plugin.getGame()).getPlayers().containsKey(event.getPlayer().getUniqueId())) {

            event.setCancelled(true);

            for (String tag : List.copyOf(event.getRightClicked().getScoreboardTags())) {

                if (tag.startsWith("shop")) {
                    event.getPlayer().openInventory(new ShopMenu((Game) this.plugin.getGame(), event.getPlayer().getUniqueId()).getPage(0));
                    return;
                }

            }

            return;
        }

        if (this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) {
            return;
        }

        event.setCancelled(true);

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        if (event.getInventory().getHolder() instanceof ShopMenu) {

            event.setCancelled(true);

            if (!(event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT)) {
                return;
            }

            if (!(this.plugin.getGame() instanceof Game) || !((Game) this.plugin.getGame()).getPlayers().containsKey(event.getWhoClicked().getUniqueId())) {
                return;
            }

            if (event.getCurrentItem() == null) {
                return;
            }

            int itemId = this.plugin.getItemStorage().getItemId(event.getCurrentItem());

            if (itemId < 0) {
                return;
            }

            Integer[] menuItems = ((Game) this.plugin.getGame()).getItemShop().getMenuItems();

            for (int i = 0; i < menuItems.length; i++) {

                if (menuItems[i] != null && menuItems[i] == itemId) {
                    event.getWhoClicked().openInventory(new ShopMenu((Game) this.plugin.getGame(), event.getWhoClicked().getUniqueId()).getPage(i));
                    return;
                }

            }

            ShopEntry shopEntry = ((Game) this.plugin.getGame()).getItemShop().getShopEntry(itemId);

            if (shopEntry != null) {

                int availableCurrency = 0;

                for (ItemStack item : Arrays.copyOf(event.getWhoClicked().getInventory().getContents(), event.getWhoClicked().getInventory().getContents().length)) {

                    if (item != null && item.getType() == shopEntry.getCurrency()) {
                        availableCurrency += item.getAmount();
                    }

                }

                if (availableCurrency < shopEntry.getPrice()) {
                    event.getWhoClicked().sendMessage("§cYou don't have enough " + shopEntry.getCurrency().name() + "s!");
                    return;
                }

                event.getWhoClicked().sendMessage("§aItem successfully purchased");
                Bedwars.removeSpecificAmountOfItems(event.getWhoClicked().getInventory(), shopEntry.getCurrency(), shopEntry.getPrice());
                event.getWhoClicked().getInventory().addItem(((Game) this.plugin.getGame()).getPlugin().getItemStorage().getItem(itemId));

                return;
            }

            UpgradeEntry upgradeEntry = ((Game) this.plugin.getGame()).getItemShop().getUpgradeEntry(itemId);

            if (upgradeEntry != null) {

                PlayerData playerData = ((Game) this.plugin.getGame()).getPlayers().get(event.getWhoClicked().getUniqueId());

                if (playerData == null) {
                    return;
                }

                int upgradeLevel = upgradeEntry.getUpgradeLevel(playerData) + 1;

                if (upgradeLevel < 0) {
                    return;
                }

                int price = upgradeEntry.getUpgradePrice(upgradeLevel);

                if (price < 0) {
                    return;
                }

                Material currency = upgradeEntry.getUpgradeCurrency(upgradeLevel);

                if (currency == null) {
                    return;
                }

                int availableCurrency = 0;

                for (ItemStack item : Arrays.copyOf(event.getWhoClicked().getInventory().getContents(), event.getWhoClicked().getInventory().getContents().length)) {

                    if (item != null && item.getType() == currency) {
                        availableCurrency += item.getAmount();
                    }

                }

                if (availableCurrency < price) {
                    event.getWhoClicked().sendMessage("§cYou don't have enough " + currency.name() + "s!");
                    return;
                }

                event.getWhoClicked().sendMessage("§aItem successfully purchased");
                Bedwars.removeSpecificAmountOfItems(event.getWhoClicked().getInventory(), currency, price);
                upgradeEntry.upgradePlayer(playerData);

                return;
            }

            return;
        }

        if (this.plugin.isPlayerBypassing(event.getWhoClicked().getUniqueId())) {
            return;
        }

        if (!(this.plugin.getGame() instanceof Game)) {
            event.setCancelled(true);
            return;
        }

    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        if (event.getInventory().getHolder() instanceof ShopMenu) {
            event.setCancelled(true);
            return;
        }

        if (this.plugin.isPlayerBypassing(event.getWhoClicked().getUniqueId())) {
            return;
        }

        if (!(this.plugin.getGame() instanceof Game)) {
            event.setCancelled(true);
            return;
        }

    }

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent event) {

        Player player = null;
        Inventory inventory = null;

        if (event.getSource().getHolder() instanceof Player || event.getDestination().getHolder() instanceof Player) {
            if (event.getSource().getHolder() instanceof Player) {
                player = (Player) event.getSource().getHolder();
                inventory = event.getDestination();
            } else {
                player = (Player) event.getDestination().getHolder();
                inventory = event.getSource();
            }
        }

        if (player == null || player.getInventory() == inventory) {
            return;
        }

        if (inventory.getHolder() instanceof ShopMenu) {
            event.setCancelled(true);
            return;
        }

        if (this.plugin.isPlayerBypassing(player.getUniqueId())) {
            return;
        }

        if (!(this.plugin.getGame() instanceof Game)) {
            event.setCancelled(true);
            return;
        }

    }

}
