package cf.hanakacraft.main.util;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class Particles implements Listener {
	
	public static Inventory particle_gui = Bukkit.createInventory(null, 9, ChatColor.translateAlternateColorCodes('&', "&b&nParticles&c                  Close [E]"));
	public Inventory ply_particles_gui = Bukkit.createInventory(null, 9, ChatColor.translateAlternateColorCodes('&', "&b&nPlayer Particles"));
	public Inventory entity_particles_gui = Bukkit.createInventory(null, 9, ChatColor.translateAlternateColorCodes('&', "&b&nEntity Particles"));
	public Inventory entity_arrow_particles_gui = Bukkit.createInventory(null, 9, ChatColor.translateAlternateColorCodes('&', "&b&nArrow Particles"));
	
	ArrayList<String> plysHasOnParticles;
	ArrayList<String> plysParticleSpiral;
	ArrayList<String> plysParticleLava = new ArrayList<String>();
	ArrayList<String> plysParticleRain = new ArrayList<String>();
	
	
	public Particles(ArrayList<String> plysHasOnParticles, ArrayList<String> plysParticleSpiral) {
		this.plysHasOnParticles = plysHasOnParticles;
		this.plysParticleSpiral = plysParticleSpiral;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player ply = (Player) e.getWhoClicked();
		ItemStack clicked = e.getCurrentItem();
		InventoryView inventory = e.getView();
		
		// par_result if true if particle was successfully added
		
		// PARTICLE CATEGORYS - ROOT INV
		if(inventory.getTitle().equals(ChatColor.translateAlternateColorCodes('&', "&b&nParticles&c                  Close [E]"))) {
			
			// REMOVE PARTICLES
			if(clicked.getType() == Material.BARRIER) {
				ply.closeInventory();
				if(plysParticleLava.contains(ply.getName())) {
					plysParticleLava.remove(ply.getName());
				}
				if(plysParticleRain.contains(ply.getName())) {
					plysParticleRain.remove(ply.getName());
				}
				if(plysParticleSpiral.contains(ply.getName())) {
					plysParticleSpiral.remove(ply.getName());
				}
				if(plysHasOnParticles.contains(ply.getName())) {
					plysHasOnParticles.remove(ply.getName());
				}
			}
			
			// CATEGORY PLAYER
			if(clicked.getType() == Material.WHITE_WOOL) {
				ply.closeInventory();
				//Dripping lava
				ItemStack item = new ItemStack(Material.LAVA_BUCKET);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6Dripping Lava"));
				item.setItemMeta(meta);
				ply_particles_gui.setItem(0, new ItemStack(item));
				
				// Rain cloud
				item = new ItemStack(Material.WATER_BUCKET);
				meta = item.getItemMeta();
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&9Rain Cloud"));
				item.setItemMeta(meta);
				ply_particles_gui.setItem(1, new ItemStack(item));
				
				// Back button
				item = new ItemStack(Material.FEATHER);
				meta = item.getItemMeta();
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f<= Back"));
				item.setItemMeta(meta);
				ply_particles_gui.setItem(8, new ItemStack(item));
				ply.openInventory(ply_particles_gui);
			}
			
			// CATEGORY ENTITY
			if(clicked.getType() == Material.ORANGE_WOOL) {
				ply.closeInventory();
				
				//Bow particle
				ItemStack item = new ItemStack(Material.ARROW);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&fArrow =>"));
				item.setItemMeta(meta);
				entity_particles_gui.setItem(0, new ItemStack(item));
				
				// Back button
				item = new ItemStack(Material.FEATHER);
				meta = item.getItemMeta();
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f<= Back"));
				item.setItemMeta(meta);
				entity_particles_gui.setItem(8, new ItemStack(item));
				ply.openInventory(entity_particles_gui);
			}
		}
		
		// PLAYER PARTICLES - CATEGORY
		boolean par_result = false;
		if (inventory.getTitle().equals(ChatColor.translateAlternateColorCodes('&', "&b&nPlayer Particles"))) {
			if (clicked.getType() == Material.LAVA_BUCKET) {
				if(!plysParticleLava.contains(ply.getName())) {
					ply.closeInventory();
					if(plysParticleRain.contains(ply.getName())) {
						plysParticleRain.remove(ply.getName());
					}
					plysParticleLava.add(ply.getName());
					par_result = true;
				}
			}
			
			if(clicked.getType() == Material.WATER_BUCKET) {
				if(!plysParticleRain.contains(ply.getName())) {
					ply.closeInventory();
					if(plysParticleLava.contains(ply.getName())) {
						plysParticleLava.remove(ply.getName());
					}
					plysParticleRain.add(ply.getName());
					par_result = true;
				}
			}
			
			if(clicked.getType() == Material.FEATHER) {
				ply.closeInventory();
				ply.openInventory(OpenMainParticleGUI());
			}
			
			// check if particle was successfully added and player not registered with particle
			if(!plysHasOnParticles.contains(ply.getName()) && par_result == true) {
				plysHasOnParticles.add(ply.getName());
			}
			e.setCancelled(true);
		}
		
		// ARROW PARTICLES - CATEGORY
		if (inventory.getTitle().equals(ChatColor.translateAlternateColorCodes('&', "&b&nEntity Particles"))) {
			if(clicked.getType() == Material.ARROW) {
				//Bow particle
				ItemStack item = new ItemStack(Material.COBWEB);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&fSmoke"));
				item.setItemMeta(meta);
				entity_arrow_particles_gui.setItem(0, new ItemStack(item));
				
				// Back button
				item = new ItemStack(Material.FEATHER);
				meta = item.getItemMeta();
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f<= Back"));
				item.setItemMeta(meta);
				entity_arrow_particles_gui.setItem(8, new ItemStack(item));
				ply.openInventory(entity_arrow_particles_gui);
			}
		}
		if (inventory.getTitle().equals(ChatColor.translateAlternateColorCodes('&', "&b&nArrow Particles"))) {
			if(clicked.getType() == Material.ARROW) {
				ply.closeInventory();
			}
			if(clicked.getType() == Material.FEATHER) {
				ply.closeInventory();
				ply.openInventory(entity_particles_gui);
			}
		}
	}
	
	public static Inventory OpenMainParticleGUI() {
		//Open player particles category
		ItemStack item = new ItemStack(Material.WHITE_WOOL);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&fPlayer Particles =>"));
		item.setItemMeta(meta);
		particle_gui.setItem(0, new ItemStack(item));
		
		item = new ItemStack(Material.ORANGE_WOOL);
		meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&fEntity Particles =>"));
		item.setItemMeta(meta);
		particle_gui.setItem(1, new ItemStack(item));
		
		// Remove button
		item = new ItemStack(Material.BARRIER);
		meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cRemove Particles"));
		item.setItemMeta(meta);
		particle_gui.setItem(8, new ItemStack(item));
		return particle_gui;
	}
	
	@EventHandler
    public void onMove(PlayerMoveEvent e){
		if(plysHasOnParticles.contains(e.getPlayer().getName())) {
	        Player ply = e.getPlayer();
	        World world = ply.getWorld();
			/*Location loc = ply.getLocation().setY(5);
			ply.sendMessage("Normal loc: " + ply.getLocation());
			ply.sendMessage("New loc: " + loc);*/
			double x = ply.getLocation().getX();
			double y = ply.getLocation().getY() + 1;
			double z = ply.getLocation().getZ();
			if(plysParticleLava.contains(ply.getName())) {
				world.spawnParticle(Particle.FALLING_LAVA, x, y, z, 4, 0.5, 0.5, 0.5, 1.4);
			}
			if(plysParticleRain.contains(ply.getName())) {
				y = ply.getLocation().getY() + 4.4;
				world.spawnParticle(Particle.CLOUD, x, y, z, 100, 0.25, 0, 0.25, 0.01);
				world.spawnParticle(Particle.FALLING_WATER, x, y, z, 15, 0.9, 0.5, 0.5, 1.4);
			}
			
			// Players with the Flame Spiral particle can be found in the main class, onEnable()
			
		}
    }
}
