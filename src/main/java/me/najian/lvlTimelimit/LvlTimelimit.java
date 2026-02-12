package me.najian.lvlTimelimit;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.ArrayList;
import java.util.List;

public final class LvlTimelimit extends JavaPlugin implements Listener {

    private FileConfiguration config;
    private int DEFAULT_TIME;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();

        DEFAULT_TIME = config.getInt("default-time", 20);

        if (!config.contains("players")) {
            config.createSection("players");
            saveConfig();
        }

        Bukkit.getPluginManager().registerEvents(this, this);

        // کم شدن تایم هر دقیقه (فقط آنلاین‌ها)
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    String name = player.getName();
                    int time = config.getInt("players." + name, DEFAULT_TIME);

                    time--;
                    config.set("players." + name, time);

                    if (time == 60) player.sendMessage("§e⏰ یک ساعت از زمانت باقی مونده!");
                    if (time == 30) player.sendMessage("§e⏰ فقط 30 دقیقه مونده!");
                    if (time == 10) player.sendMessage("§c⏰ فقط 10 دقیقه مونده!");
                    if (time == 5) player.sendMessage("§c⏰ فقط 5 دقیقه مونده!");
                    if (time == 1) player.sendMessage("§4⏰ آخرین دقیقه!");

                    if (time <= 0) {
                        player.kickPlayer("§c⛔ زمان بازی شما به پایان رسید!");
                    }
                }
                saveConfig();
            }
        }.runTaskTimer(this, 1200L, 1200L); // هر 1 دقیقه
    }

    // اولین ورود یا چک تایم
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        String name = p.getName();

        if (!config.contains("players." + name)) {
            config.set("players." + name, DEFAULT_TIME);
            saveConfig();
            p.sendMessage("§a⏱️ زمان اولیه شما: " + DEFAULT_TIME + " دقیقه");
        }

        if (config.getInt("players." + name) <= 0) {
            p.kickPlayer("§c⛔ زمان شما تمام شده و اجازه ورود ندارید!");
        }
    }




    // Commands
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!command.getName().equalsIgnoreCase("timelimit")) return false;

        // help
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sender.sendMessage("§6§l=== TimeLimit Commands ===");
            sender.sendMessage("§e/timelimit left");
            sender.sendMessage("§e/timelimit set <player|all> <min>");
            sender.sendMessage("§e/timelimit add <player|all> <min>");
            sender.sendMessage("§e/timelimit remove <player|all> <min>");
            return true;
        }

        // left
        if (args[0].equalsIgnoreCase("left")) {
            if (!(sender instanceof Player p)) {
                sender.sendMessage("§cاین دستور فقط برای پلیر است.");
                return true;
            }
            int time = config.getInt("players." + p.getName(), DEFAULT_TIME);
            p.sendMessage("§a⏱️ زمان باقی‌مانده: §e" + time + " دقیقه");
            return true;
        }

        // admin check
        if (!sender.hasPermission("lvl.timelimit.admin")) {
            sender.sendMessage("§cشما دسترسی ندارید!");
            return true;
        }

        if (args.length != 3) {
            sender.sendMessage("§cفرمت دستور اشتباه است!");
            return true;
        }

        String action = args[0].toLowerCase();
        String targetName = args[1];

        int minutes;
        try {
            minutes = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cعدد معتبر وارد کن!");
            return true;
        }

        // ALL players
        if (targetName.equalsIgnoreCase("all")) {
            for (String name : config.getConfigurationSection("players").getKeys(false)) {
                int current = config.getInt("players." + name, DEFAULT_TIME);

                switch (action) {
                    case "set" -> config.set("players." + name, minutes);
                    case "add" -> config.set("players." + name, current + minutes);
                    case "remove" -> config.set("players." + name, current - minutes);
                }

                Player online = Bukkit.getPlayerExact(name);
                if (online != null && online.isOnline()) {
                    int newTime = config.getInt("players." + name);
                    if (newTime <= 0) {
                        online.kickPlayer("§c⛔ زمان بازی شما به پایان رسید!");
                    } else {
                        online.sendMessage("§a⏱️ زمان شما تنظیم شد: §e" + newTime + " دقیقه");
                    }
                }
            }
            saveConfig();
            sender.sendMessage("§a✔️ زمان برای همه پلیرها تنظیم شد.");
            return true;
        }

        // single player (online یا offline)
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        int current = config.getInt("players." + targetName, DEFAULT_TIME);

        switch (action) {
            case "set" -> config.set("players." + targetName, minutes);
            case "add" -> config.set("players." + targetName, current + minutes);
            case "remove" -> config.set("players." + targetName, current - minutes);
            default -> {
                sender.sendMessage("§cدستور ناشناخته!");
                return true;
            }
        }

        saveConfig();

        if (target.isOnline()) {
            Player online = (Player) target;
            int newTime = config.getInt("players." + targetName);
            if (newTime <= 0) {
                online.kickPlayer("§c⛔ زمان بازی شما به پایان رسید!");
            } else {
                online.sendMessage("§a⏱️ زمان شما تنظیم شد: §e" + newTime + " دقیقه");
            }
        }

        sender.sendMessage("§a✔️ زمان بازیکن با موفقیت تغییر کرد.");
        return true;
    }

    //sugestion
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (!command.getName().equalsIgnoreCase("timelimit")) return null;

        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.add("help");
            suggestions.add("left");
            suggestions.add("set");
            suggestions.add("add");
            suggestions.add("remove");
            suggestions.add("resetall");
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set") ||
                    args[0].equalsIgnoreCase("add") ||
                    args[0].equalsIgnoreCase("remove")) {

                suggestions.add("all");

                for (Player p : Bukkit.getOnlinePlayers()) {
                    suggestions.add(p.getName());
                }
            }
        }

        return suggestions;
    }


}
