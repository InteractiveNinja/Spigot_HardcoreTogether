package eu.imninja.hardcoretogether;

import net.minecraft.server.v1_16_R1.World;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ExecutorService;


public class Main extends JavaPlugin implements Listener  {

    public static final String pluginName = "HardcoreTogether";
    private boolean modeEnabled = false;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this,this);
        System.out.println(pluginName + ", aktiviert");
    }

    @Override
    public void onDisable() {
        System.out.println(pluginName + ", deaktiviert");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equals("hardcore")  && !(sender instanceof Player)) {
            /*if(modeEnabled) {
                modeEnabled = false;
                sender.sendMessage("Hardcore wurde deaktiviert");
                Collection<? extends Player> p = this.getServer().getOnlinePlayers();

                p.forEach(player -> {
                    sendTitleToPlayer(player,"Hardcore", "Sterben ist erlaubt",ChatColor.GREEN);
                });
                Player player = p.iterator().next();
                player.getWorld().setHardcore(false);
                player.getWorld().setDifficulty(Difficulty.EASY);

                return true;

            } else {*/
                sender.sendMessage("Hardcore wurde aktiviert");
                modeEnabled = true;
                Collection<? extends Player> p = this.getServer().getOnlinePlayers();

                p.forEach(player -> {
                    sendTitleToPlayer(player,"Hardcore", "Versuch nicht zu sterben",ChatColor.RED);
                });
                timer("start");
                Player player = p.iterator().next();
                player.getWorld().setHardcore(true);
                player.getWorld().setDifficulty(Difficulty.HARD);
                return true;
           // }
        }
        return true;
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) throws IllegalStateException, IOException {
        if(modeEnabled) {
            String playerName = e.getEntity().getName();
            String playerMessage = e.getDeathMessage();
            int time =  timer("end");
            Collection<? extends Player> p = this.getServer().getOnlinePlayers();
                    p.forEach(player -> {
                        player.kickPlayer(ChatColor.RED + "Der Spieler, " + ChatColor.GOLD + playerName + ChatColor.RED + ", ist gestorben, RIP an die Welt\n " + " Der Server wird heruntergefahren\n" + e.getDeathMessage()+ "\n "+ ChatColor.GOLD + formatToTime(time));
                    });
                    changeFile();
                    this.getServer().shutdown();
                }
            };

    private void sendTitleToPlayer(Player p, String msg,String msg2,ChatColor color) {
        p.sendTitle(color + msg,color +msg2);
        p.playSound(p.getLocation(),Sound.ENTITY_WITHER_SPAWN,1f,1f);
    }



    private void changeFile() throws IOException {
        FileInputStream in = new FileInputStream("server.properties");
        Properties props = new Properties();
        props.load(in);
        String wordlname = props.getProperty("level-name");
        in.close();

        FileOutputStream out = new FileOutputStream("server.properties");
        props.setProperty("level-name", wordlname + "_dead");
        props.store(out, null);
        out.close();
    }

    int time = 0;
    int t = 0;
    private int timer(String type){

        if(type.equals("start")) {
            t = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                @Override
                public void run() {
                    time++;
                }
            }, 0L, 20L);
        }
        if(type.equals("end")) {
            Bukkit.getScheduler().cancelTask(t);
            return time;
        }

        return 0;

    }

    private String formatToTime (int seconds) {
        int p1 = seconds % 60;
        int p2 = seconds / 60;
        int p3 = p2 % 60;
        p2 = p2 / 60;
        return p2 + ":" + p3 + ":" + p1 +" überlebt";
    }
}

