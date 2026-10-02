package CustomPlayerDrop.Sandrix.Dev;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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
import CustomPlayerDrop.Sandrix.Dev.Config.Config;
public class Event implements Listener{
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity().getPlayer();
		Player k = e.getEntity().getKiller();
		Location l = p.getLocation();
		if(Config.plenabled) {
			if(p.hasPermission("CustomPlayerDrop.enabled")) {
				if(Config.SpecificWorldOnly) {	
					if(!Config.worlds.contains(p.getWorld().getName())) {
						return;
					}
				}
				if(Config.PlayerKillonly) {
					if(!(k instanceof Player)) {
						return;
					}
				}
				if(Config.EnableExtraCommands) {
					for(int i = 0; i<Config.commands.size();i++) {
						if(k!=null) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Config.commands.get(i).replaceAll("\\{player}", p.getName()).replaceAll("\\{killer}", k.getName()));
						}	
						else {
							if(!Config.commands.get(i).contains("{killer}"))
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Config.commands.get(i).replaceAll("\\{player}", p.getName()));
						}
					}
				}
				if(!Config.DropPlayerInventory) {
					e.getDrops().clear();
				}
				if(Config.DropPlayerHead) {
					DropPlayerHead(p, l, k);
				}
				if(Config.EnableExtraDrop) {
					DropExtraDrop(p, l, k);
				}
				if(Config.XpOnKill) {
					e.setDroppedExp(Config.XpGiveCount);
				}
				if(Config.MoneyOnKill) {
					if(k!=null) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give "+k.getName()+" "+Config.MoneyGiveCount);
					}							
				}
			}
		}
	}
	
	public void DropPlayerHead(Player p, Location l, Player k){
		boolean NewVersion = Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
		Material type = Material.matchMaterial(NewVersion ? "PLAYER_HEAD" : "SKULL_ITEM");
		ItemStack item = new ItemStack(type, 1);
		if(!NewVersion)
			item.setDurability((short)3);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwner(p.getName());
		ArrayList<String> lore = new ArrayList<String>();
		if(k==null) {
			meta.setDisplayName(Config.HeadName.replaceAll("&", "§").replaceAll("\\{player}", p.getName()).replaceAll("\\{killer}", Config.NonPlayerKiller));
		}
		else {
			meta.setDisplayName(Config.HeadName.replaceAll("&", "§").replaceAll("\\{player}", p.getName()).replaceAll("\\{killer}", k.getName()));
		}		
		for(int i=0; i< Config.HeadLore.size(); i++) {
			if(k==null) {
				lore.add(Config.HeadLore.get(i).replaceAll("&", "§").replaceAll("\\{player}", p.getName()).replaceAll("\\{killer}",Config.NonPlayerKiller));
			}
			else {
				lore.add(Config.HeadLore.get(i).replaceAll("&", "§").replaceAll("\\{player}", p.getName()).replaceAll("\\{killer}", k.getName()));
			}			
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		int v = (int) (Math.random() * 101);
		if(v<=Config.HeadDropRate)
			p.getWorld().dropItemNaturally(l, item);
	}
	
	public void DropExtraDrop(Player p, Location l, Player k) {
		for(int i=0; i<Config.items.size();i++) {
			int v = (int) (Math.random() * 101);
			if(v<=Config.rates.get(i)) {
				ItemStack IS = Config.items.get(i);
				ItemMeta IM = Config.items.get(i).getItemMeta();
				if(k==null) {
					String im = IM.getDisplayName().replaceAll("&", "§").replaceAll("\\{player}", p.getName()).replaceAll("\\{killer}", Config.NonPlayerKiller);
					IM.setDisplayName(im);
				}
				else {
					String im = IM.getDisplayName().replaceAll("&", "§").replaceAll("\\{player}", p.getName()).replaceAll("\\{killer}", k.getName());
					IM.setDisplayName(im);
				}
				List<String> list = IM.getLore();
				for(int j = 0; j<list.size();j++) {
					if(k==null) {
						String im = list.get(j).replaceAll("&", "§").replaceAll("\\{player}", p.getName()).replaceAll("\\{killer}", Config.NonPlayerKiller);
						list.set(j, im);
					}
					else {
						String im = list.get(j).replaceAll("&", "§").replaceAll("\\{player}", p.getName()).replaceAll("\\{killer}", k.getName());
						list.set(j, im);
					}
				}
				IM.setLore(list);
				IS.setItemMeta(IM);
				p.getWorld().dropItemNaturally(l, IS);
			}
		}
	}
}
