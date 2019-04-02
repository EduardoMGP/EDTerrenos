/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edterrenos;

import com.earth2me.essentials.api.Economy;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author EduardoMGP
 */
public class EDTerrenos extends JavaPlugin {

    private static EDTerrenos plugin;
    private static Economy econ;
    public static HashMap<String, ProtectedRegion> terrenosCoordenada = new HashMap<>();
    public static HashMap<String, Location> terrenosCoordenada2 = new HashMap<>();

    @Override
    public void onEnable() {

        if (this.getServer().getPluginManager().getPlugin("WorldGuard") == null || this.getServer().getPluginManager().getPlugin("WorldEdit") == null) {
            Bukkit.getConsoleSender().sendMessage("§f--------[ EDTerrenos ]--------");
            Bukkit.getConsoleSender().sendMessage("");
            Bukkit.getConsoleSender().sendMessage("§cEste plugin necessita do WorldGuard e WorldEdit para funcionar");
            Bukkit.getConsoleSender().sendMessage("§cInstale-o e reinicie o servidor");
            Bukkit.getConsoleSender().sendMessage("");
            Bukkit.getConsoleSender().sendMessage("§f--------[ EDTerrenos ]--------");
            this.getServer().getPluginManager().disablePlugin(this);

        } else {

            plugin = this;
            saveDefaultConfig();
            Bukkit.getConsoleSender().sendMessage("§6[EDTerrenos] §fPlugin Habilitado com sucesso");
            Bukkit.getConsoleSender().sendMessage("§f--------[ EDTerrenos ]--------");
            Bukkit.getConsoleSender().sendMessage("");
            Bukkit.getConsoleSender().sendMessage("§cVersao: 1.0");
            Bukkit.getConsoleSender().sendMessage("§cAuthor: EduardoMGP");
            Bukkit.getConsoleSender().sendMessage("");
            Bukkit.getConsoleSender().sendMessage("§f--------[ EDTerrenos ]--------");
            getCommand("terreno").setExecutor(new Comandos());
            Eventos ev = new Eventos();

        }

    }

    @Override
    public void onDisable() {

        Bukkit.getConsoleSender().sendMessage("§6[EDTerrenos] §fPlugin Desabilitado");
        saveDefaultConfig();

    }

    public WorldGuardPlugin getWorldGuard() {
        return (WorldGuardPlugin) this.getServer().getPluginManager().getPlugin("WorldGuard");

    }

    public WorldEditPlugin getWorldEdit() {
        return (WorldEditPlugin) this.getServer().getPluginManager().getPlugin("WorldEdit");
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static EDTerrenos getPlugin() {
        return plugin;
    }

    public String getMessage(String m) {
        m = this.getConfig().getString("Config.prefixo") + this.getConfig()
                .getString(m);
        return m.replaceAll("&", "§")
                .replaceAll("%nl%", "\n");
    }

    public String getConfigString(String c) {
        return this.getConfig().getString(c);
    }

    public int getConfigInt(String c) {
        return this.getConfig().getInt(c);
    }

    public double getConfigDouble(String c) {
        return this.getConfig().getDouble(c);
    }

}
