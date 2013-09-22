package me.tomski.shop;


import me.tomski.language.MessageBank;
import me.tomski.prophunt.PropHunt;
import me.tomski.prophunt.ShopSettings;
import me.tomski.utils.VaultUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class MainShop implements Listener {

    private PropHunt plugin;
    public List<Player> inMenu = new ArrayList<Player>();


    public MainShop(PropHunt plugin) {
        this.plugin = plugin;
    }


    public void openMainShop(Player p) {
        Inventory inv = Bukkit.createInventory(p, 9, MessageBank.SHOP_TITLE.getMsg());

        ItemStack customItems = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta itemMeta = customItems.getItemMeta();
        itemMeta.setDisplayName(MessageBank.ITEM_SHOP_NAME.getMsg());
        List<String> itemLore = new ArrayList<String>();
        itemLore.add(MessageBank.ITEM_SHOP_DESC.getMsg());
        itemMeta.setLore(itemLore);
        customItems.setItemMeta(itemMeta);


        ItemStack customDisguises = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta disguiseMeta = customDisguises.getItemMeta();
        disguiseMeta.setDisplayName(MessageBank.DISGUISE_SHOP_NAME.getMsg());
        List<String> disLore = new ArrayList<String>();
        disLore.add(MessageBank.DISGUISE_SHOP_DESC.getMsg());
        disguiseMeta.setLore(disLore);
        customDisguises.setItemMeta(disguiseMeta);


        ItemStack placeHolder = new ItemStack(Material.ENDER_CHEST);
        ItemMeta placeMeta = placeHolder.getItemMeta();
        placeMeta.setDisplayName(MessageBank.DISGUISE_NAME.getMsg());
        List<String> placeLore = new ArrayList<String>();
        placeLore.add(MessageBank.DISGUISE_DESC.getMsg());
        placeMeta.setLore(placeLore);
        placeHolder.setItemMeta(placeMeta);

        ItemStack loadout = new ItemStack(Material.CHEST);
        ItemMeta loadMeta = loadout.getItemMeta();
        loadMeta.setDisplayName(MessageBank.LOADOUT_NAME.getMsg());
        List<String> loadLore = new ArrayList<String>();
        loadLore.add(MessageBank.LOADOUT_DESC.getMsg());
        loadMeta.setLore(loadLore);
        loadout.setItemMeta(loadMeta);

        ItemStack currency = new ItemStack(Material.EMERALD);
        ItemMeta currencyMeta = currency.getItemMeta();
        currencyMeta.setDisplayName(ChatColor.GOLD + ShopSettings.currencyName);
        List<String> currencyLore = new ArrayList<String>();
        currencyLore.add(ChatColor.GREEN + "" + getCurrencyBalance(p));
        currencyMeta.setLore(currencyLore);
        currency.setItemMeta(currencyMeta);

        inv.setItem(2, customItems);
        inv.setItem(3, customDisguises);
        inv.setItem(4, placeHolder);
        inv.setItem(5, loadout);

        inv.setItem(8, currency);
        inMenu.add(p);
        p.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (inMenu.contains((Player) e.getWhoClicked())) {
            if (e.getCurrentItem() != null) {
                if (!e.getCurrentItem().getType().equals(Material.AIR)) {
                    if (e.getCurrentItem().getType().equals(Material.ENDER_CHEST)) {
                        e.setCancelled(true);
                        e.getView().close();
                        plugin.getServer().getScheduler().runTaskLater(plugin, new BukkitRunnable() {
                            @Override
                            public void run() {
                                plugin.getShopManager().getBlockChooser().openBlockShop((Player) e.getWhoClicked());
                            }
                        }, 2L);
                    } else if (e.getCurrentItem().getType().equals(Material.GOLD_BLOCK)) {
                        e.setCancelled(true);
                        e.getView().close();
                        plugin.getShopManager().getDisguiseShop().openDisguiseShop((Player) e.getWhoClicked());
                    } else if (e.getCurrentItem().getType().equals(Material.DIAMOND_SWORD)) {
                        e.setCancelled(true);
                        e.getView().close();
                        plugin.getShopManager().getItemShop().openMainShop((Player) e.getWhoClicked());
                    } else if (e.getCurrentItem().getType().equals(Material.CHEST)) {
                        e.setCancelled(true);
                        e.getView().close();
                        plugin.getShopManager().getLoadoutChooser().openBlockShop((Player) e.getWhoClicked());
                    } else {
                        e.setCancelled(true);
                        e.getView().close();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (inMenu.contains(e.getPlayer())) {
            inMenu.remove(e.getPlayer());
        }
    }

    public int getCurrencyBalance(Player p) {
        switch (ShopSettings.economyType) {
            case PROPHUNT:
                return plugin.SQL.getCredits(p.getName());
            case VAULT:
                return (int) plugin.vaultUtils.economy.getBalance(p.getName());
        }
        return 0;
    }

}
