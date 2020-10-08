package cf.hanakacraft.main.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.BanList.Type;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.nametagedit.plugin.NametagEdit;
import com.nametagedit.plugin.api.data.Nametag;

import cf.hanakacraft.main.Main;
import cf.hanakacraft.main.plugs.ConfigManager;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.minecraft.server.v1_14_R1.Item;
import ru.tehkode.permissions.bukkit.PermissionsEx;

@SuppressWarnings("unused")
public class Vanilla implements Listener {
	ArrayList<String> plysInVanish;
	HashMap<String, Inventory> invSave;
	//ArrayList<Location> anvils;
	
	private Main plugin = Main.getPlugin(Main.class);
	
	File chat_dir = new File(plugin.getDataFolder() + "/chat/");
	File chat_file = new File(plugin.getDataFolder() + "/chat/chat.csv");
	FileConfiguration config = Main.plugin.getConfig();
	
	int kick_warn_limit = plugin.getConfig().getInt("kick_warn_limit");
	int ban_kick_limit = plugin.getConfig().getInt("ban_kick_limit");
	String warn_msg = plugin.getConfig().getString("warn_msg");
	String kick_msg = plugin.getConfig().getString("kick_msg");
	String ban_msg = plugin.getConfig().getString("ban_msg");
	String ban_bc = plugin.getConfig().getString("ban_bc");
	
	int player_warns = 0;
	int player_kicks = 0;
	
	public Vanilla(ArrayList<String> plysInVanish, HashMap<String, Inventory> invSave) {
		this.plysInVanish = plysInVanish;
		this.invSave = invSave;
	}
	
	private Player ply;
	
	@EventHandler
    public void OnEntityDamage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player) {
        	ply = (Player) e.getEntity();
        	if(plysInVanish.contains(ply.getName())){
	        	//e.getEntity().sendMessage(e.getEntity().toString());
	            e.setCancelled(true);
        	}
        }
    }
	
	@EventHandler
    public void BlockPlace(BlockPlaceEvent e) {
		// TO GET CHEST PLACED AND WHEN OPENED BY PLAYER
		ply = (Player) e.getPlayer();
		Block block = e.getBlock();
		Material mat = block.getType();
		/*if(mat.equals(Material.CHEST) || mat.equals(Material.CHEST_MINECART) || mat.equals(Material.TRAPPED_CHEST) || mat.equals(Material.ENDER_CHEST) || mat.equals(Material.BARREL) || mat.equals(Material.SHULKER_BOX)) {
			ply.sendMessage("Block: " + block.hashCode());
		}*/
    }
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void OnPlayerJoin(PlayerJoinEvent e) {
		Boolean NametagEdit_bool = false;
		Boolean Pex_bool = false;
		String rank_sys = plugin.getConfig().getString("rank_sys");
		//sender.sendMessage("rank_sys: " + rank_sys);
		if(rank_sys.contains("NametagEdit")) {
			NametagEdit_bool = true;
			Pex_bool = false;
			//sender.sendMessage("Nametag: " + NametagEdit_bool);
		}
		if(rank_sys.contains("Pex")) {
			Pex_bool = true;
			NametagEdit_bool = false;
			//sender.sendMessage("Pex: " + Pex_bool);
		}
		Player ply = e.getPlayer();
		// If player is in vanish
		if(plysInVanish.contains(ply.getName())) {
			// Makes player in vanish invisible to every player online
			String join_msg = plugin.getConfig().getString("join_msg");
			ply.sendMessage(ChatColor.translateAlternateColorCodes('&', join_msg));
			String vanish_tab_icon = plugin.getConfig().getString("vanish_tab_icon");
			if(NametagEdit_bool == true) {
				//ply.sendMessage("Nametag: " + NametagEdit_bool);
				Nametag tag = NametagEdit.getApi().getNametag(ply);
				String team_name = tag.getPrefix();
				ply.setPlayerListName(ChatColor.translateAlternateColorCodes('&', vanish_tab_icon + team_name + ply.getName()));
			}
			if(Pex_bool == true) {
				//ply.sendMessage("Pex: " + Pex_bool);
				ply.setPlayerListName(ChatColor.translateAlternateColorCodes('&', vanish_tab_icon + PermissionsEx.getUser(ply).getGroups()[0].getPrefix() + PermissionsEx.getUser(ply).getName()));
			}
			for(Player p : Bukkit.getOnlinePlayers()) {
				p.hidePlayer(ply);
			}
		}
		// If player is not in vanish
		else {
			if(!ply.hasPermission("watchdog.admin")) {
				// Makes player in vanish invisible to every player online except player with rank watchdog.admin
				for(String name : plysInVanish) {
					Player p = Bukkit.getPlayer(name);
					ply.hidePlayer(p);					
				}
			}
		}
	}
	
	/*@SuppressWarnings("null")
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Location loc = null;
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(e.getClickedBlock().getType() == Material.ANVIL) {
				Block block = e.getClickedBlock();
				loc.add(block.getX(), block.getY(), block.getZ());
				if(loc != null) {
					block.setType(Material.ANVIL);
				}
				ply.sendMessage("You clicked an " + block.toString());
			}
			if(e.getClickedBlock().getType() == Material.CHIPPED_ANVIL) {
				Block block = e.getClickedBlock();
				loc.add(block.getX(), block.getY(), block.getZ());
				if(loc != null) {
					block.setType(Material.ANVIL);
				}
				ply.sendMessage("You clicked an " + block.toString());
			}
			if(e.getClickedBlock().getType() == Material.DAMAGED_ANVIL) {
				Block block = e.getClickedBlock();
				loc.add(block.getX(), block.getY(), block.getZ());
				if(loc != null) {
					block.setType(Material.ANVIL);
				}
				ply.sendMessage("You clicked an " + block.toString());
			}
		}
	}*/
	
	@EventHandler
	public void OnPlayerChat(AsyncPlayerChatEvent e) {
		new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				for(String p : plysInVanish) {
					Player ply = Bukkit.getPlayer(p);
					ply.setPlayerListName(ChatColor.translateAlternateColorCodes('&', "[&a✔&r] " + PermissionsEx.getUser(ply).getGroups()[0].getPrefix() + PermissionsEx.getUser(ply).getName()));
				}
			}
		}.runTaskTimerAsynchronously(plugin, 0, 0);
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(e.getMessage().contains(p.getName())) {
				if(e.getPlayer().getName() != p.getName()) {
					p.playNote(p.getLocation(), Instrument.PLING, Note.flat(1, Tone.D));
				}
			}
		}
		
		@SuppressWarnings("unchecked")
		List<String> wordList = (List<String>)plugin.getConfig().getList("word_list");
        ply = (Player) e.getPlayer();
        if(plysInVanish.contains(ply.getName())) {
        	e.setCancelled(true);
        }
        /*else {
			if(!chat_dir.exists()) {
				chat_dir.mkdirs();
			}
			String message = "";
			try (FileReader fr = new FileReader(chat_file)){
				if(chat_file.exists()) {
					int data = fr.read();
				    while(data != -1) {
				        message += ((char) data);
				        data = fr.read();
				    }
    			}
			}
			catch(IOException e1) {
    			e1.printStackTrace();
    		}
        	message = message +  ply.getName() + " | " + e.getMessage() + "\n--------------------------------------------------\n";
        	try (FileWriter fw = new FileWriter(chat_file)) {
    			if(!chat_file.exists()) {
    				chat_file.createNewFile();
    			}
    			fw.write(message);
    			fw.flush();
    			fw.close();
    		}
    		catch(IOException e1) {
    			e1.printStackTrace();
    		}
        }*/
        
        // Check for banned words
        for(String s : wordList) {
	        if(e.getMessage().toLowerCase().contains(s)){
	        	e.setCancelled(true);
	        	File warn_dir = new File(plugin.getDataFolder() + "/warn/");
	        	File warn_file = new File(plugin.getDataFolder() + "/warn/" + ply.getUniqueId() + ".txt");	        	
	        	if(!warn_dir.exists()) {
	        		warn_dir.mkdirs();
				}
				String message = "0";
				if(warn_file.exists()) {
					try (FileReader fr = new FileReader(warn_file)){
						int data = fr.read();
					    while(data != -1) {
					        message += ((char) data);
					        data = fr.read();
					    }
	    			}
					catch(IOException e1) {
						e1.printStackTrace();
					}
				}
				
				int player_warns = Integer.parseInt(message) + 1;
				message = player_warns + "";
	        	try (FileWriter fw = new FileWriter(warn_file)) {
	    			if(!warn_file.exists()) {
	    				warn_file.createNewFile();
	    			}
	    			fw.write(message);
	    			fw.flush();
	    			fw.close();
	    		}
	    		catch(IOException e1) {
	    			e1.printStackTrace();
	    		}
        		
        		int ban_limit = kick_warn_limit + ban_kick_limit;
        		/*ply.sendMessage(ban_limit + "");
        		ply.sendMessage("Ply kick: " + kick_warn_limit);
        		ply.sendMessage("Ply ban: " + ban_kick_limit);
	        	ply.sendMessage(String.valueOf(ban_limit));
	        	ply.sendMessage(kick_warn_limit + "");
	        	ply.sendMessage(message);*/
	        	
	        	// Warn player
	        	if(player_warns <= kick_warn_limit) {
	        		String msg = warn_msg.replace("{player_warns}", message);
	        		ply.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	        	}
	        	else {
	        		// Ban player
		        	if(player_warns > ban_limit) {
			        	//gets ip and port
						InetSocketAddress ipAdd = ply.getAddress();
						//removes port
						String[] sIp = ipAdd.toString().split("/");
						String sIpPort = sIp[1];
						String[] sIpNoPort = sIpPort.split(":");
						//only IP Address
						String ip = sIpNoPort[0];
			
			        	ply.sendMessage(ip);
			        	String msg = ban_bc.replace("{player}", ply.getName());
			        	Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
			        	int year = Calendar.getInstance().get(Calendar.YEAR);
			        	int month = Calendar.getInstance().get(Calendar.MONTH);
			        	Bukkit.broadcastMessage(year + " : " + month);
			        	for (int i = 0; i < 6; i++) {
			        		month++;
			        		if(month > 12) {
			        			year++;
			        		}
			        	}
			        	String date_str = Calendar.getInstance().get(Calendar.DATE) + "/" + month + "/" + year + " " + Calendar.getInstance().get(Calendar.HOUR) + ":" + Calendar.getInstance().get(Calendar.MINUTE);
			        	Bukkit.broadcastMessage(date_str);
			        	SimpleDateFormat date_format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			        	Date date = null;
						try {
							date = date_format.parse(date_str);
						} catch (java.text.ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
			        	Bukkit.getBanList(Type.NAME).addBan(ply.getName(), "Excessive language", date, "Console");
			        	Bukkit.getScheduler().runTask(plugin, new Runnable() {
			        		  public void run() {
			        			  ply.kickPlayer(ChatColor.translateAlternateColorCodes('&', ban_msg));
			        		  }
			        	});
		        	}
				}
	        	
	        	// Kick player
	        	if(player_warns > kick_warn_limit) {
	        		Bukkit.getScheduler().runTask(plugin, new Runnable() {
		        		  public void run() {
		        			  ply.kickPlayer(kick_msg);
		        		  }
		        	});
	        	}
	        }
        }
	}
	
	/*@SuppressWarnings("deprecation")
	@EventHandler
	public void OnGameModeChange(PlayerGameModeChangeEvent e) {
		if(e.getPlayer() instanceof Player) {
			Player ply = e.getPlayer();
			if(e.getNewGameMode().getValue() == 1 && !invSave.containsKey(ply.getName())) {
				invSave.put(ply.getName(), ply.getInventory());
				ply.getInventory().clear();
			}
			else {
				Inventory inv = invSave.get(ply.getName());
				for(int i = 0; i > inv.getSize(); i++) {
					ItemStack item = inv.getItem(i);
					ply.getInventory().addItem(item);
				}
			}
		}
		ply.updateInventory();
	}*/
}
