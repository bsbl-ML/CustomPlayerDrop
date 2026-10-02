package CustomPlayerDrop.Sandrix.Dev.listener;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import CustomPlayerDrop.Sandrix.Dev.config.PluginConfig;
import CustomPlayerDrop.Sandrix.Dev.config.PluginConfig.ItemDrop;
import CustomPlayerDrop.Sandrix.Dev.service.DropService;

public class PlayerDeathListener implements Listener {
  private final PluginConfig config;
  private final DropService dropService;
  public PlayerDeathListener(PluginConfig config) {
    this.config = config;
    this.dropService = new DropService(config);
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    if (!config.isPluginEnabled()) {
      return;
    }

    Player player = event.getEntity();
    Player killer = player.getKiller();
    Location location = player.getLocation();

    if (!player.hasPermission("customplayerdrop.enabled")) {
      return;
    }
    if (config.isSpecificWorldOnly() && !config.getWorlds().contains(player.getWorld().getName())) {
      return;
    }
    if (config.isPlayerKillOnly() && killer == null) {
      return;
    }
    
    executeExtraCommands(player, killer);
    dropService.giveDrops(player, killer);
    
    if (config.isExtraDropEnabled()) {
      dropExtraItems(player, killer, location);
    }
    if (config.isXpOnKill()) {
      event.setDroppedExp(config.getXpGiveCount());
    }
    if (config.isMoneyOnKill() && killer != null) {
      giveMoney(killer);
      }
  }

  private void executeExtraCommands(Player player, Player killer) {
    if (!config.areExtraCommandsEnabled()) {
      return;
    }
    for (String command : config.getCommands()) {
      if (killer != null) {
        command = replacePlaceholders(command, player, killer);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        continue;
      }
      if (command.contains("{killer}")) {
        continue;
      }

      command = replacePlaceholders(command, player, null);
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
      }
  }

    meta.setOwningPlayer(player);
    String killerName = killer != null ? killer.getName() : config.getNonPlayerKiller();
    
    meta.setDisplayName(format(replacePlaceholders(config.getHeadName(), player, killerName)));
    List<String> lore = config.getHeadLore().stream().map(line -> format(replacePlaceholders(line, player, killerName))).toList();
        
    meta.setLore(lore);
    head.setItemMeta(meta);
    location.getWorld().dropItemNaturally(location, head);
  }
      /*
      * Clone the configured ItemStack
      *
      * This is important because we don't want to modify the
      * ItemStack stored inside the configuration when replacing
      * {player} and {killer}.
      */
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

  private void giveMoney(Player killer) {
    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + killer.getName() + " " + config.getMoneyGiveCount());
  }
}
