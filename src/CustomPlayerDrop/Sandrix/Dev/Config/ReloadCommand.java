package CustomPlayerDrop.Sandrix.Dev.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import CustomPlayerDrop.Sandrix.Dev.config.PluginConfig;
import CustomPlayerDrop.Sandrix.Dev.service.DropService;

public class ReloadCommand implements CommandExecutor {
  private final PluginConfig pluginConfig;
  private final DropService dropService;
  
  public ReloadCommand(PluginConfig pluginConfig, DropService dropService) {
    this.pluginConfig = pluginConfig;
    this.dropService = DropService;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!sender.hasPermission("customplayerdrop.admin")) {
      sender.sendMessage(pluginConfig.getPermissionMessage());
      return true;
    }

    if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
      pluginConfig.reload();
      sender.sendMessage("§a[CustomPlayerDrop] Configuration reloaded!");
      return true;
    }

    if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
      Player target = Bukkit.getPlayer(args[1]);
      if (target == null) {
        sender.sendMessage("§c[CustomPlayerDrop] That player is not online.");
        return true;
      }

      dropService.giveDrops(target);
      sender.sendMessage("§a[CustomPlayerDrop] Drops given to " + target.getName() + "!");
      return true;
    }

    sender.sendMessage("§cUsage:" + "\n§7/customplayerdrop reload" + "\n§7/customplayerdrop give <player>");
    return true;
  }
}
