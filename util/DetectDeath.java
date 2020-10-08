package cf.hanakacraft.main.util;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import cf.hanakacraft.main.Main;

public class DetectDeath implements Listener {
	String deathMsg = null;
	
	FileConfiguration config = Main.plugin.getConfig();
	
	@EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
		String msg = config.getString("death_msg");
		Boolean debug = config.getBoolean("debug");
		deathMsg = null;
		String d_world = null;
		Integer x = 0;
		Integer y = 0;
		Integer z = 0;
        Player p = e.getEntity().getPlayer();
        if(p.hasPermission("watchdog.debug") && debug == true) {
        	p.sendMessage(ChatColor.YELLOW + "AY captain!");
        }
        if (p instanceof Player) {
        	x = p.getLocation().getBlockX();
            y = p.getLocation().getBlockY();
            z = p.getLocation().getBlockZ();
            d_world = p.getLocation().getWorld().getName();
        	msg = msg.replace("{x}", x.toString());
        	msg = msg.replace("{y}", y.toString());
        	msg = msg.replace("{z}", z.toString());
        	msg = msg.replace("{world}", d_world);
        	msg = ChatColor.translateAlternateColorCodes('&', msg);
        	deathMsg = msg;
            System.out.println(deathMsg);
        }
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		p.sendMessage(deathMsg);
	}
}
