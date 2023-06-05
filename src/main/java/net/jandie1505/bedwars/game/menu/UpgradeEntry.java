package net.jandie1505.bedwars.game.menu;

import net.jandie1505.bedwars.game.player.PlayerData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class UpgradeEntry {
    private final ItemShop itemShop;
    private final List<Integer> upgradeItemIds;
    private final List<Integer> upgradePrices;
    private final List<Material> upgradeCurrencies;
    private final List<int[]> slots;

    public UpgradeEntry(ItemShop itemShop, List<Integer> upgradeItemIds, List<Integer> upgradePrices, List<Material> upgradeCurrencies, List<int[]> slots) {
        this.itemShop = itemShop;
        this.upgradeItemIds = List.copyOf(upgradeItemIds);
        this.upgradePrices = List.copyOf(upgradePrices);
        this.upgradeCurrencies = List.copyOf(upgradeCurrencies);
        this.slots = List.copyOf(slots);
    }

    public int getItemId(int upgradeLevel) {

        if (this.upgradeItemIds.isEmpty()) {
            return -1;
        }

        if (upgradeLevel >= this.upgradeItemIds.size()) {
            return this.upgradeItemIds.get(this.upgradeItemIds.size() - 1);
        }

        return this.upgradeItemIds.get(upgradeLevel);
    }

    public int getUpgradePrice(int upgradeLevel) {

        if (this.upgradePrices.isEmpty()) {
            return -1;
        }

        if (upgradeLevel >= this.upgradePrices.size()) {
            return this.upgradePrices.get(this.upgradePrices.size() - 1);
        }

        return this.upgradePrices.get(upgradeLevel);
    }

    public Material getUpgradeCurrency(int upgradeLevel) {

        if (this.upgradeCurrencies.isEmpty()) {
            return null;
        }

        if (upgradeLevel >= this.upgradeCurrencies.size()) {
            return this.upgradeCurrencies.get(this.upgradeCurrencies.size() - 1);
        }

        return this.upgradeCurrencies.get(upgradeLevel);
    }

    public int getUpgradeLevel(PlayerData playerData) {

        if (this.itemShop.getArmorUpgrade() == this) {
            return playerData.getArmorUpgrade();
        } else if (this.itemShop.getPickaxeUpgrade() == this) {
            return playerData.getPickaxeUpgrade();
        } else if (this.itemShop.getShearsUpgrade() == this) {
            return playerData.getShearsUpgrade();
        } else {
            return 0;
        }

    }

    public ItemStack getItem(int upgradeLevel) {
        ItemStack item = this.itemShop.getGame().getPlugin().getItemStorage().getItem(this.getItemId(upgradeLevel));

        if (item == null) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();

        List<String> lore = meta.getLore();

        lore.add(1, "§r§fPrice: §a" + this.getUpgradePrice(upgradeLevel) + " " + this.getUpgradeCurrency(upgradeLevel).name() + "s");
        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }

    public ItemShop getItemShop() {
        return this.itemShop;
    }

    public List<int[]> getSlots() {
        return List.copyOf(slots);
    }
}
