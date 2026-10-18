package ru.cicada;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class CicadaCommand implements CommandExecutor, TabCompleter {
    private final CicadaPlugin plugin;

    public CicadaCommand(CicadaPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender s, Command c, String label, String[] a) {
        if (a.length == 0) { s.sendMessage("/cicada <spawn [x y z]|give [player]|reload>"); return true; }
        switch (a[0].toLowerCase()) {
            case "spawn" -> {
                if (a.length >= 4) {
                    if (!(s instanceof Player p)) { s.sendMessage("Specify coordinates in game only."); return true; }
                    try {
                        double x = parse(a[1], p.getLocation().getX()), y = parse(a[2], p.getLocation().getY()), z = parse(a[3], p.getLocation().getZ());
                        plugin.spawnCicada(new Location(p.getWorld(), x, y, z), p.getLocation().getYaw() + 180f);
                    } catch (NumberFormatException ex) { s.sendMessage("Bad coordinates."); return true; }
                } else if (s instanceof Player p) {
                    Block b = p.getTargetBlockExact(6);
                    Location loc = b != null ? b.getRelative(0, 1, 0).getLocation().add(0.5, 0, 0.5)
                            : p.getLocation();
                    plugin.spawnCicada(loc, p.getLocation().getYaw() + 180f);
                } else { s.sendMessage("Console: /cicada spawn <x y z> needs a player."); return true; }
                s.sendMessage("Cicada spawned.");
            }
            case "give" -> {
                Player t = a.length >= 2 ? plugin.getServer().getPlayer(a[1]) : (s instanceof Player p ? p : null);
                if (t == null) { s.sendMessage("Player not found."); return true; }
                t.getInventory().addItem(plugin.createItem());
                s.sendMessage("Given.");
            }
            case "reload" -> { plugin.reloadConfig(); s.sendMessage("Config reloaded ."); }
            default -> s.sendMessage("/cicada <spawn|give|reload>");
        }
        return true;
    }

    private static double parse(String s, double base) {
        return s.startsWith("~") ? base + (s.length() > 1 ? Double.parseDouble(s.substring(1)) : 0) : Double.parseDouble(s);
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {
        List<String> r = new ArrayList<>();
        if (a.length == 1) for (String x : List.of("spawn", "give", "reload")) if (x.startsWith(a[0].toLowerCase())) r.add(x);
        return r;
    }
}
