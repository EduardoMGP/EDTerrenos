/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edterrenos;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 *
 * @author EduardoMGP
 */
public class Funçoes {

    private static EDTerrenos plugin;

    public Funçoes() {
        plugin = EDTerrenos.getPlugin();
    }

    public void comprarTerreno(Player p, int tamanho, String area) {

        try {
            if (plugin.getEconomy().getMoney(p.getName()) < plugin.getConfigDouble("Config.valorPorBloco") * tamanho * tamanho) {
                double valor = plugin.getConfigDouble("Config.valorPorBloco") * tamanho * tamanho;
                p.sendMessage(plugin.getMessage("Mensagens.semDinheiro")
                        .replaceAll("%dinheiro%", valor + "")
                );
                return;
            }
        } catch (Exception e) {
            double valor = plugin.getConfigDouble("Config.valorPorBloco") * tamanho * tamanho;
            p.sendMessage(plugin.getMessage("Mensagens.semDinheiro")
                    .replaceAll("%dinheiro%", valor + "")
            );
            return;
        }

        RegionManager rm = plugin.getWorldGuard().getRegionManager(p.getWorld());
        Map<String, ProtectedRegion> region = rm.getRegions();
        for (String id : region.keySet()) {
            if (id.equalsIgnoreCase("EDTerrenos_" + p.getName() + "_" + area.toLowerCase())) {
                p.sendMessage(plugin.getMessage("Mensagens.Terreno.jaExiste"));
                return;
            }
        }
        int quantidadeTerreno = 0;
        for (String id : region.keySet()) {
            if (id.contains("edterrenos_" + p.getName().toLowerCase() + "_")) {
                quantidadeTerreno++;
            }
        }
        if (!p.hasPermission("EDTerrenos.vip")) {
            if (quantidadeTerreno >= plugin.getConfigInt("Config.Limite.player")) {
                p.sendMessage(plugin.getMessage("Mensagens.Terreno.limiteExcedido")
                        .replaceAll("%quantidade%", plugin.getConfigInt("Config.Limite.player") + "")
                );
                return;
            }
        } else {
            if (quantidadeTerreno >= plugin.getConfigInt("Config.Limite.vip")) {
                p.sendMessage(plugin.getMessage("Mensagens.Terreno.limiteExcedido")
                        .replaceAll("%quantidade%", plugin.getConfigInt("Config.Limite.vip") + "")
                );
                return;
            }
        }
        Location loc = p.getLocation();
        int locx = loc.getBlockX();
        int locz = loc.getBlockZ();
        int t = tamanho % 2;
        int z;
        int x;
        BlockVector bv1;
        BlockVector bv2;
        if (t != 0) {
            t = tamanho / 2;
            x = locx - t + 1;
            t = t + 1;
            z = locz + t;
            bv1 = new BlockVector(x, 256, z);
            x = locx + t;
            z = locz - t + 2;
            bv2 = new BlockVector(x, 0, z);
        } else {
            t = tamanho / 2;
            x = locx - t;
            z = locz + t;
            bv1 = new BlockVector(x, 256, z);
            x = locx + t - 1;
            z = locz - t + 1;
            bv2 = new BlockVector(x, 0, z);
        }
        
        
        
        ProtectedCuboidRegion pr = new ProtectedCuboidRegion("EDTerrenos_" + p.getName() + "_" + area.toLowerCase(), bv1, bv2);
        List<ProtectedRegion> overlap = pr.getIntersectingRegions(rm.getRegions().values());
        for (ProtectedRegion opr : overlap) {
            if (!opr.getOwners().contains(p.getUniqueId())) {
                p.sendMessage(plugin.getMessage("Mensagens.Terreno.jaExisteTerrenoProximo"));
                return;
            }
        }
        pr = new ProtectedCuboidRegion("EDTerrenos_" + p.getName() + "_" + area.toLowerCase(), bv1, bv2);
        DefaultDomain dd = new DefaultDomain();
        rm.addRegion(pr);
        pr.setPriority(100);
        dd.addPlayer(p.getName());
        pr.setOwners(dd);
        try {
            pr.setFlag(DefaultFlag.BUILD, DefaultFlag.BUILD.parseInput(plugin.getWorldGuard(), p, "deny"));
            pr.setFlag(DefaultFlag.BUILD, StateFlag.State.DENY);
            pr.setFlag(DefaultFlag.BUILD.getRegionGroupFlag(), RegionGroup.NON_OWNERS);
            pr.setFlag(DefaultFlag.PVP, DefaultFlag.PVP.parseInput(plugin.getWorldGuard(), p, "deny"));
            pr.setFlag(DefaultFlag.MOB_SPAWNING, DefaultFlag.MOB_SPAWNING.parseInput(plugin.getWorldGuard(), p, "allow"));
            pr.setFlag(DefaultFlag.ENTRY_DENY_MESSAGE, DefaultFlag.ENTRY_DENY_MESSAGE.parseInput(plugin.getWorldGuard(), p, plugin.getMessage("Mensagens.Terreno.entradaProibida").replaceAll("%owner%", p.getName())));
        } catch (Exception e1) {
            return;
        }
        try {
            rm.save();
        } catch (Exception e) {
            return;
        }
        try {
            p.sendMessage(plugin.getMessage("Mensagens.Terreno.comprado"));
            double valor = plugin.getConfigDouble("Config.valorPorBloco") * tamanho * tamanho;
            plugin.getEconomy().subtract(p.getName(), valor);
            p.sendMessage(plugin.getMessage("Mensagens.Terreno.compradoDinheiroSubtraido")
                    .replaceAll("%dinheiro%", valor + ""));
        } catch (Exception e) {
        }

        int maxX = (int) (pr.getMaximumPoint().getX());
        int minX = (int) (pr.getMinimumPoint().getX());

        int maxZ = (int) (pr.getMaximumPoint().getZ());
        int minZ = (int) (pr.getMinimumPoint().getZ());
        
        tamanho = maxX - minX;
        Location maxCoord = new Location(p.getWorld(), maxX, 256, maxZ);
        Location minCoord = new Location(p.getWorld(), minX, 0, minZ);
        adicionarCercas(maxCoord, minCoord);
    }

    

 
    

    public int containsAir(int x, int z, World w) {
        int y = w.getMaxHeight() - 1;
        Block b = w.getBlockAt(x, y, z);
        for (int i = 0; i < w.getMaxHeight() - 1; i++) {
            if (b.getType().equals(Material.AIR)) {
                y = y - 1;
                b = w.getBlockAt(x, y, z);
            } else {
                if (b.getTypeId() != 85) {
                    if(b.getType() == Material.LONG_GRASS || b.getType() == Material.DOUBLE_PLANT){
                        return y;
                    } 
                    return y + 1;
                    
                }
            }
        }
        return 0;
    }
    
    public int containsFence(int x, int z, World w) {
        int y = w.getMaxHeight() - 1;
        Block b = w.getBlockAt(x, y, z);
        for (int i = 0; i < w.getMaxHeight() - 1; i++) {
            if (b.getType().equals(Material.AIR)) {
                y = y - 1;
                b = w.getBlockAt(x, y, z);
            } else {
                if (b.getTypeId() == 85) {
                    return y;
                }
            }
        }
        return 0;
    }

    public void adicionarCercas(Location maxCoord, Location minCoord) {
        int i1 = 0;
        Block block;
        for (int i = minCoord.getBlockZ(); i <= maxCoord.getBlockZ(); i++) {
            int y = containsAir(maxCoord.getBlockX(), maxCoord.getBlockZ() - i1, maxCoord.getWorld());
            block = maxCoord.getWorld().getBlockAt(maxCoord.getBlockX(), y, maxCoord.getBlockZ() - i1);
            block.setTypeId(85);
            i1++;
        }
        i1 = 0;
        for (int i = minCoord.getBlockX(); i <= maxCoord.getBlockX(); i++) {
            int y = containsAir(maxCoord.getBlockX() - i1, maxCoord.getBlockZ(), maxCoord.getWorld());
            block = maxCoord.getWorld().getBlockAt(maxCoord.getBlockX() - i1, y, maxCoord.getBlockZ());
            block.setTypeId(85);
            i1++;
        }
        i1 = 0;
        for (int i = minCoord.getBlockX(); i <= maxCoord.getBlockX(); i++) {
            int y = containsAir(maxCoord.getBlockX() - i1, minCoord.getBlockZ(), maxCoord.getWorld());
            block = maxCoord.getWorld().getBlockAt(maxCoord.getBlockX() - i1, y, minCoord.getBlockZ());
            block.setTypeId(85);
            i1++;
        }

        i1 = 0;
        for (int i = minCoord.getBlockZ(); i <= maxCoord.getBlockZ(); i++) {
            int y = containsAir(minCoord.getBlockX(), maxCoord.getBlockZ() - i1, maxCoord.getWorld());
            block = maxCoord.getWorld().getBlockAt(minCoord.getBlockX(), y, maxCoord.getBlockZ() - i1);
            block.setTypeId(85);
            i1++;
        }

    }

    public void removerCercas(Location maxCoord, Location minCoord) {
        int i1 = 0;
        Block block;
        for (int i = minCoord.getBlockZ(); i <= maxCoord.getBlockZ(); i++) {
            int y = containsFence(maxCoord.getBlockX(), maxCoord.getBlockZ() - i1, maxCoord.getWorld());
            block = maxCoord.getWorld().getBlockAt(maxCoord.getBlockX(), y, maxCoord.getBlockZ() - i1);
            if (block.getTypeId() == 85) {
                block.setType(Material.AIR);
            }
            i1++;
        }
        i1 = 0;
        for (int i = minCoord.getBlockX(); i <= maxCoord.getBlockX(); i++) {
            int y = containsFence(maxCoord.getBlockX() - i1, maxCoord.getBlockZ(), maxCoord.getWorld());
            block = maxCoord.getWorld().getBlockAt(maxCoord.getBlockX() - i1, y, maxCoord.getBlockZ());
            if (block.getTypeId() == 85) {
                block.setType(Material.AIR);
            }
            i1++;
        }
        i1 = 0;
        for (int i = minCoord.getBlockX(); i <= maxCoord.getBlockX(); i++) {
            int y = containsFence(maxCoord.getBlockX() - i1, minCoord.getBlockZ(), maxCoord.getWorld());
            block = maxCoord.getWorld().getBlockAt(maxCoord.getBlockX() - i1, y, minCoord.getBlockZ());
            if (block.getTypeId() == 85) {
                block.setType(Material.AIR);
            }
            i1++;
        }

        i1 = 0;
        for (int i = minCoord.getBlockZ(); i <= maxCoord.getBlockZ(); i++) {
            int y = containsFence(minCoord.getBlockX(), maxCoord.getBlockZ() - i1, maxCoord.getWorld());
            block = maxCoord.getWorld().getBlockAt(minCoord.getBlockX(), y, maxCoord.getBlockZ() - i1);
            if (block.getTypeId() == 85) {
                block.setType(Material.AIR);
            }
            i1++;
        }

    }

    
}
