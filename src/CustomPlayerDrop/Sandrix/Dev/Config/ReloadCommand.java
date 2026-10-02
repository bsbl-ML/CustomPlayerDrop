package CustomPlayerDrop.Sandrix.Dev.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import CustomPlayerDrop.Sandrix.Dev.config.PluginConfig;

public class ReloadCommand implements CommandExecutor {
  private final PluginConfig pluginConfig;
  
  public ReloadCommand(PluginConfig pluginConfig) {
    this.pluginConfig = pluginConfig;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!sender.hasPermission("customplayerdrop.admin")) {
      sender.sendMessage(pluginConfig.getPermissionMessage());
      return true;
    }

    if (args.length != 1 || !args[0].equalsIgnoreCase("reload")) {
      sender.sendMessage("§cUsage: /customplayerdrop reload");
      return true;
    }
    pluginConfig.reload();
    sender.sendMessage("§a[CustomPlayerDrop] Configuration reloaded!");
    return true;
  }
}
