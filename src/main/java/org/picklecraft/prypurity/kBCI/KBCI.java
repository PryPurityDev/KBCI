package org.picklecraft.prypurity.kBCI;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class KBCI extends JavaPlugin implements Listener {

    private Set<Material> blacklistItems = new HashSet<>();

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        loadbanneditems();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
    }

    private void loadbanneditems() {
        File bannedItemsFile = new File(getDataFolder(), "banned-items.yml");
        if (!bannedItemsFile.exists()) {
            saveResource("banned-items.yml", false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(bannedItemsFile);
        List<String> bannedItemList = config.getStringList("banned-items");

        for (String itemName : bannedItemList) {
            try {
                Material material = Material.valueOf(itemName);
                blacklistItems.add(material);
            } catch (IllegalArgumentException e) {
                getLogger().warning("Invalid Material in the banned-items.yml" + itemName);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked().hasPermission("kbci.bypass")) {
            return;
        }
        if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 9; i++) {
                        if (event.getWhoClicked().getInventory().getItem(i) != null) {
                            Material itemType = event.getWhoClicked().getInventory().getItem(i).getType();
                            if (blacklistItems.contains(itemType)) {
                                event.getWhoClicked().getInventory().clear(i);
                            }
                        }
                    }
                }
            }.runTaskLater(this, 1L);
        }
    }
}
