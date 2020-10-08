package cf.hanakacraft.main.util;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("deprecation")
public class Vanish implements Listener {
	
	ArrayList<String> plysInVanish;
	
	public Vanish(ArrayList<String> plysInVanish2) {
		plysInVanish = plysInVanish2;
	}
	
	@EventHandler
    public void OnEntityDamage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player) {
        	Player ply = (Player) e.getEntity();
        	if(plysInVanish.contains(ply.getName())) {
	            e.setCancelled(true);
        	}
        }
    }
	
	@EventHandler
	public void OnPlayerPickup(PlayerPickupItemEvent e) {
		Player ply = e.getPlayer();
		if(plysInVanish.contains(ply.getName())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void OnPlayerInteract(PlayerInteractEvent e) {
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player ply = e.getPlayer();
			if (e.getClickedBlock().getType().equals(Material.CHEST)) {
		        if (plysInVanish.contains(ply.getName())) {
		        	e.setCancelled(true);
		        	Chest chest = (Chest) e.getClickedBlock().getState();
	                Inventory inv = Bukkit.createInventory(null, 9, ChatColor.translateAlternateColorCodes('&', "&b&nVanish Chest"));
	                for (ItemStack is : chest.getInventory().getContents()) {
	                    inv.addItem(is);
	                }
	                ply.openInventory(inv);
		        }
			}
		}
	}
}
