package CustomPlayerDrop.Sandrix.Dev.service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import CustomPlayerDrop.Sandrix.Dev.config.PluginConfig;
import CustomPlayerDrop.Sandrix.Dev.config.PluginConfig.ItemDrop;

public class DropService {
  private final PluginConfig config;
  public DropService(PluginConfig config) {
    this.config = config;
  }

  public void giveDrops(Player player) {
    giveDrops(player, null);
  }
  
  public void giveDrops(Player player, Player killer) {
    Location location = player.getLocation();
    if (config.shouldDropPlayerHead() && roll(config.getHeadDropRate())) {
      dropPlayerHead(player, killer, location);
    }
    if (config.isExtraDropEnabled()) {
      dropExtraItems(player, killer, location);
    }
  }

  private void dropPlayerHead(Player player, Player killer,Location location) {
    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta meta = (SkullMeta) head.getItemMeta();
    if (meta == null) {
      return;
    }
    meta.setOwningPlayer(player);

    String killerName = killer != null ? killer.getName() : config.getNonPlayerKiller();
    meta.setDisplayName(format(replacePlaceholders(config.getHeadName(), player, killerName)));
    List<String> lore = config.getHeadLore().stream().map(line -> format(replacePlaceholders(line, player, killerName))).toList();
    
    meta.setLore(lore);
    head.setItemMeta(meta);
    location.getWorld().dropItemNaturally(location, head);
  }

  private void dropExtraItems(Player player, Player killer, Location location) {
    String killerName = killer != null ? killer.getName() : config.getNonPlayerKiller();
    for (ItemDrop configuredDrop : config.getExtraDrops()) {
      if (!roll(configuredDrop.getRate())) {
        continue;
      }

      ItemStack item = configuredDrop.getItem().clone();
      ItemMeta meta = item.getItemMeta();

      if (meta == null) {
        location.getWorld().dropItemNaturally(location, item);
        continue;
      }
      if (meta.hasDisplayName()) {
        meta.setDisplayName(format(replacePlaceholders(meta.getDisplayName(), player, killerName)));
      }
      if (meta.hasLore()) {
        List<String> lore = meta.getLore();
        if (lore != null) {
          lore = lore.stream().map(line -> format(replacePlaceholders(line, player, killerName))).toList();
          meta.setLore(lore);
        }
      }

      item.setItemMeta(meta);
      location.getWorld().dropItemNaturally(location, item);
    }
  }

  private String replacePlaceholders(String text, Player player, String killerName) {
    return text.replace("{player}", player.getName()).replace("{killer}", killerName);
  }

  private String format(String text) {
    return text.replace("&", "§");
  }

  private boolean roll(int percentage) {
    if (percentage <= 0) {
      return false;
    }
    if (percentage >= 100) {
      return true;
    }
    return ThreadLocalRandom.current().nextInt(100) < percentage;
  }
}
