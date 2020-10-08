package cf.hanakacraft.main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import com.nametagedit.plugin.NametagEdit;
import com.nametagedit.plugin.api.data.Nametag;

import cf.hanakacraft.main.util.DetectDeath;
import cf.hanakacraft.main.util.Particles;
import cf.hanakacraft.main.util.Vanilla;
import cf.hanakacraft.main.util.Vanish;
import net.md_5.bungee.api.ChatColor;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Main extends JavaPlugin implements Listener {
	public static Main plugin;
	
	// MYSQL
	/*private Connection con;
	private String host, db, username, password;
	private int port;*/
	
	String no_perm_msg = this.getConfig().getString("no_perm_msg");
	
	ArrayList<String> plysInVanish = new ArrayList<String>();
	ArrayList<Player> plysVanishFx = new ArrayList<Player>();
	
	// PARTICLES
	ArrayList<String> plysHasOnParticles = new ArrayList<String>();
	ArrayList<String> plysParticleSpiral = new ArrayList<String>();
	HashMap<String, Inventory> invSave = new HashMap<>();
	Boolean NametagEdit_bool = false;
	Boolean Pex_bool = false;
	
	@Override
	public void onEnable() {
		plugin = this;
		Server server = Bukkit.getServer();
		ConsoleCommandSender console = server.getConsoleSender();
		console.sendMessage(ChatColor.WHITE + "-------" + ChatColor.RED + "Watchdog " + ChatColor.WHITE + "by " + ChatColor.GOLD + "Klump" + ChatColor.WHITE + "-------\n-------" + ChatColor.GREEN + "Loaded Successfully" + ChatColor.WHITE + "-------");
		/*TradeListener trader = new TradeListener();
		getCommand("trade").setExecutor(new TrustyTrader(trader));*/
		Bukkit.getServer().getPluginManager().registerEvents(new DetectDeath(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Vanilla(plysInVanish, invSave), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Vanish(plysInVanish), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Particles(plysHasOnParticles, plysParticleSpiral), this);
		this.saveDefaultConfig();
		//mysqlSetup();
	}
	
	/*public void mysqlSetup() {
		host = "localhost";
		port = 3306;
		db = "manager";
		username = "root";
		password = "xc2E!29x5y6?";
		
		try {
			synchronized(this){
				if(getConnection() != null && !getConnection().isClosed()) {
					return;
				}
				Class.forName("com.mysql.jdbc.Driver");
				setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.db, this.username, this.password));
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "MYSQL CONNECTED TO WATCHDOG");
			}
		}
		catch(SQLException se) {
			se.printStackTrace();
		}
		catch(ClassNotFoundException ce) {
			ce.printStackTrace();
		}
	}
	
	public Connection getConnection() {
		return con;
	}
	
	public void setConnection(Connection connection) {
		this.con = connection;
	}*/

	@SuppressWarnings({ "deprecation" })
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String rank_sys = this.getConfig().getString("rank_sys");
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
		
		if(sender instanceof Player) {
			Player ply = (Player) sender;
			
			if(cmd.getName().equalsIgnoreCase("discord")) {
				if(sender instanceof Player) {
					String invite_msg = getConfig().getString("invite_msg");
					String discord_link = getConfig().getString("discord_link");
					invite_msg = invite_msg.replace("{link}", discord_link);
					invite_msg = ChatColor.translateAlternateColorCodes('&', invite_msg);
					sender.sendMessage(invite_msg);
					return true;
				}
			}
			
			if(cmd.getName().equalsIgnoreCase("sc")) {
				if(ply.hasPermission("watchdog.staff") || sender.hasPermission("watchdog.*")) {
					String message = "";
					for(int i = 0; i < args.length; i++) {
						message += args[i] + " ";
					}
					if(!message.isEmpty()) {
						for(Player p : plugin.getServer().getOnlinePlayers()) {
							if(p.hasPermission("watchdog.staff") || p.hasPermission("watchdog.*")) {
								if(NametagEdit_bool == true) {
									Nametag tag = NametagEdit.getApi().getNametag(ply);
									String team_name = tag.getPrefix();
									ply.setPlayerListName(ChatColor.translateAlternateColorCodes('&', "&cStaff Chat&f - " + team_name + ply.getName() + ":&f " + message));
								}
								if(Pex_bool == true) {
									p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cStaff Chat&f - " + PermissionsEx.getUser(ply).getGroups()[0].getPrefix() + PermissionsEx.getUser(ply).getName() + ":&f " + message));
								}
							}
						}
					}
				}
				return true;
			}
			
			if(cmd.getName().equalsIgnoreCase("staff")) {
				String adminStr = this.getConfig().getString("showAdmins_msg");
				String noAdminStr = this.getConfig().getString("noAdminsOnline_msg");
				String admins = "";
				for(Player p : Bukkit.getOnlinePlayers()) {
					if(!plysInVanish.contains(p.getName()) || ply.hasPermission("watchdog.admin") || ply.hasPermission("watchdog.*")) {
						if(p.hasPermission("watchdog.staff") || p.hasPermission("watchdog.*")) {
							admins = admins + p.getName() + ", ";
						}
					}
				}
				if(admins.length() > 1) {
					admins = admins.substring(0, admins.length() - 2);
					adminStr = adminStr.replace("{admins}", admins);
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', adminStr));
				}
				else {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', noAdminStr));
				}
				return true;
			}
	
			if(cmd.getName().equalsIgnoreCase("vanish")) {
				if(sender.hasPermission("watchdog.vanish") || sender.hasPermission("watchdog.*")) {
					plysHasOnParticles.remove(ply.getName());
					if(!plysInVanish.contains(ply.getName())) {
						plysInVanish.add(ply.getName());
						for(Player p : Bukkit.getOnlinePlayers()) {
							p.hidePlayer(ply);
							// If player has rank watchdog.admin they can see players in vanish
							if(p.hasPermission("watchdog.admin") || p.hasPermission("watchdog.*")) {
								p.showPlayer(ply);
							}
						}
						ply.setAllowFlight(true);
						World world = ply.getWorld();
						/*Location loc = ply.getLocation().setY(5);
						ply.sendMessage("Normal loc: " + ply.getLocation());
						ply.sendMessage("New loc: " + loc);*/
						if(plysVanishFx.contains(ply)) {
							double x = ply.getLocation().getX();
							double y = ply.getLocation().getY() + 1;
							double z = ply.getLocation().getZ();
							world.spawnParticle(Particle.PORTAL, x, y, z, 540, 0.5, 0.5, 0.5, 1.4);
						}
						String returnStr = this.getConfig().getString("vanished_msg");
						ply.sendMessage(ChatColor.translateAlternateColorCodes('&', returnStr));
						String vanish_tab_icon = this.getConfig().getString("vanish_tab_icon");
						if(NametagEdit_bool == true) {
							Nametag tag = NametagEdit.getApi().getNametag(ply);
							String team_name = tag.getPrefix();
							ply.setPlayerListName(ChatColor.translateAlternateColorCodes('&', vanish_tab_icon + team_name + ply.getName()));
						}
						if(Pex_bool == true) {
							ply.setPlayerListName(ChatColor.translateAlternateColorCodes('&', vanish_tab_icon + PermissionsEx.getUser(ply).getGroups()[0].getPrefix() + PermissionsEx.getUser(ply).getName()));
						}
					}
					// Go out of vanish
					else {
						plysInVanish.remove(ply.getName());
						for(Player p : Bukkit.getOnlinePlayers()) {
							p.showPlayer(ply);
						}
						if(ply.getGameMode().getValue() == 2 || ply.getGameMode().getValue() == 0) {
							ply.setAllowFlight(false);
						}
						World world = ply.getWorld();
						if(plysVanishFx.contains(ply)) {
							world.spawnParticle(Particle.DRAGON_BREATH, ply.getLocation(), 160, 0.1, 0.2, 0.1, 0.2);
							world.spawnParticle(Particle.CRIT_MAGIC, ply.getLocation(), 160, 0.5, 1, 0.5, 0.2);
						}
						String returnStr = this.getConfig().getString("notVanished_msg");
						ply.sendMessage(ChatColor.translateAlternateColorCodes('&', returnStr));
						if(NametagEdit_bool == true) {
							Nametag tag = NametagEdit.getApi().getNametag(ply);
							String team_name = tag.getPrefix();
							ply.setPlayerListName(ChatColor.translateAlternateColorCodes('&', team_name + ply.getName()));
						}
						if(Pex_bool == true) {
							ply.setPlayerListName(ChatColor.translateAlternateColorCodes('&', PermissionsEx.getUser(ply).getGroups()[0].getPrefix() + PermissionsEx.getUser(ply).getName()));
						}
					}
				}
				return true;
			}
			
			if(cmd.getName().equalsIgnoreCase("vanisheffect")) {
				if(sender.hasPermission("watchdog.vanish") || sender.hasPermission("watchdog.*")) {
					String vanishOnStr = this.getConfig().getString("vanishFxOn_msg");
					String vanishOffStr = this.getConfig().getString("vanishFxOff_msg");
					if(!plysVanishFx.contains(ply)) {
						plysVanishFx.add(ply);
						ply.sendMessage(ChatColor.translateAlternateColorCodes('&', vanishOnStr));
					}
					else {
						plysVanishFx.remove(ply);
						ply.sendMessage(ChatColor.translateAlternateColorCodes('&', vanishOffStr));
					}
				}
				return true;
			}
			
			if(cmd.getName().equalsIgnoreCase("particles")) {
				if(!plysInVanish.contains(ply.getName())) {
					if(ply.hasPermission("particles")) {
						ply.openInventory(Particles.OpenMainParticleGUI());
					}
				}
				return true;
			}
			
			if(cmd.getName().equalsIgnoreCase("clearwarns")) {
				if(ply.hasPermission("watchdog.admin") || ply.hasPermission("watchdog.*")) {
					Player target = Bukkit.getPlayer(args[0]);
					if(Bukkit.getOnlinePlayers().contains(target)) {
						if(args[0].length() < 1) {
							target = ply;
						}
						if(args[0].length() > 1) {
							File warn_dir = new File(plugin.getDataFolder() + "/warn/");
					       	File warn_file = new File(plugin.getDataFolder() + "/warn/" + target.getUniqueId() + ".txt");	        	
				        	if(!warn_dir.exists()) {
				        		warn_dir.mkdirs();
							}
						        	
							if(warn_file.exists()) {
								warn_file.delete();
	
								String clear_warns_msg = this.getConfig().getString("clear_warns_msg");
								String msg = "";
								if(NametagEdit_bool == true) {
									Nametag tag = NametagEdit.getApi().getNametag(ply);
									String team_name = tag.getPrefix();
									String clear_msg = clear_warns_msg.replace("{staff}", team_name + ply.getName());
									msg = clear_msg.replace("{player}", team_name + ply.getName());
								}
								if(Pex_bool == true) {
									String clear_msg = clear_warns_msg.replace("{staff}", PermissionsEx.getUser(ply).getGroups()[0].getPrefix() + PermissionsEx.getUser(ply).getName());
									msg = clear_msg.replace("{player}", PermissionsEx.getUser(target).getGroups()[0].getPrefix() + PermissionsEx.getUser(target).getName());
								}
								Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
							}
							else {
								String no_warns_msg = this.getConfig().getString("no_warns_msg");
								String msg = "";
								if(NametagEdit_bool == true) {
									Nametag tag = NametagEdit.getApi().getNametag(ply);
									String team_name = tag.getPrefix();
									String clear_msg = no_warns_msg.replace("{staff}", team_name + ply.getName());
									msg = clear_msg.replace("{player}", team_name + ply.getName());
								}
								if(Pex_bool == true) {
									String clear_msg = no_warns_msg.replace("{staff}", PermissionsEx.getUser(ply).getGroups()[0].getPrefix() + PermissionsEx.getUser(ply).getName());
									msg = clear_msg.replace("{player}", PermissionsEx.getUser(target).getGroups()[0].getPrefix() + PermissionsEx.getUser(target).getName());
								}
								ply.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
							}
						}
						else {
							ply.sendMessage(ChatColor.translateAlternateColorCodes('&', no_perm_msg));
						}
					}
				}
				else {
					String ply_not_online_msg = this.getConfig().getString("ply_not_online_msg");
					String msg = ply_not_online_msg.replace("{player}", args[0]);
					ply.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
				}
				return true;
			}
			
			if(cmd.getName().equalsIgnoreCase("clearchat")) {
				if(ply.hasPermission("watchdog.staff") || ply.hasPermission("watchdog.*")) {
					String dump = "";
					for(int i = 0; i < 400; i++) {
						dump += "\n  ";
					}
					for(Player p : Bukkit.getOnlinePlayers()) {
						p.sendMessage(dump);
					}
					
					String clearchat_msg = plugin.getConfig().getString("clearchat_msg");
					String msg = "";
					if(NametagEdit_bool == true) {
						Nametag tag = NametagEdit.getApi().getNametag(ply);
						String team_name = tag.getPrefix();
						msg = clearchat_msg.replace("{player}", team_name + ply.getName());
					}
					if(Pex_bool == true) {
						msg = clearchat_msg.replace("{player}", PermissionsEx.getUser(ply).getGroups()[0].getPrefix() + PermissionsEx.getUser(ply).getName());
					}
					Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
				}
				return true;
			}
			
			if(cmd.getName().startsWith("watchdog")){
				if(ply.hasPermission("watchdog.*")) {
					if(args[0].equalsIgnoreCase("reload")) {
						this.reloadConfig();
						String msg = this.getConfig().getString("reload_msg");
						ply.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
					}
				}
				else {
					ply.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cArgs for /watchdog:&6 reload"));
				}
				return true;
			}
		}
		return false;
	}
}