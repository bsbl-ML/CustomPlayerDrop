package CustomPlayerDrop.Sandrix.Dev.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
public class Reload implements CommandExecutor{
	@Override
	public boolean onCommand(CommandSender s, Command c, String s2, String[] s3) {
		if(c.getName().equalsIgnoreCase("CustomPlayerDrop")) {
			if(s.hasPermission("CustomPlayerDrop.admin")) {
				if(s3.length == 1 && s3[0] != null && s3[0].equalsIgnoreCase("reload")) {
					Config.Reload();
					s.sendMessage("§a[CustomPlayerDrop] Config reloaded!");
				}
				else {
					s.sendMessage("§4[CustomPlayerDrop] Correct usage /CustomPlayerDrop reload");
				}
			}
			else {
					s.sendMessage(Config.permMessage);
			}
		}
		return false;
	}
}
