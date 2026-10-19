package CustomPlayerDrop.Sandrix.Dev;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import CustomPlayerDrop.Sandrix.Dev.PluginConfig;
import CustomPlayerDrop.Sandrix.Dev.PluginConfig.ItemDrop;
import CustomPlayerDrop.Sandrix.Dev.DropService;

public class PlayerDeathListener implements Listener {
    private final PluginConfig config;
    private final DropService dropService;

    public PlayerDeathListener(PluginConfig config, DropService dropService) {
        this.config = config;
        this.dropService = dropService;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!config.isPluginEnabled()) {
            return;
        }

        Player player = event.getEntity();
        Player killer = player.getKiller();

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

        ConsoleCommandSender console = Bukkit.getConsoleSender();
        for (String configuredCommand : config.getCommands()) {
            // Commands requiring a killer are skipped for non-player deaths.
            if (killer == null && configuredCommand.contains("{killer}")) {
                continue;
            }

            String command = configuredCommand.replace("{player}", player.getName());
            if (killer != null) {
                command = command.replace("{killer}", killer.getName());
            }
            // Support ampersand color codes in command arguments/messages.
            command = command.replace("&", "§");
            Bukkit.dispatchCommand(console, command);
        }
    }

    private void giveMoney(Player killer) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + killer.getName() + " " + config.getMoneyGiveCount());
    }
}
