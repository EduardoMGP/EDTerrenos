/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edterrenos;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author EduardoMGP
 */
public class Eventos implements Listener {

    private static EDTerrenos plugin;

    public Eventos() {
        plugin = EDTerrenos.getPlugin();
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onPlayerItemHand(PlayerItemHeldEvent event) {
        if (plugin.terrenosCoordenada.containsKey(event.getPlayer().getName())) {
            plugin.terrenosCoordenada.remove(event.getPlayer().getName());
            plugin.terrenosCoordenada2.remove(event.getPlayer().getName());
            event.getPlayer().sendMessage(plugin.getMessage("Mensagens.Terreno.expansaoCancel"));
        }

    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        if (plugin.terrenosCoordenada.containsKey(event.getPlayer().getName())) {
            plugin.terrenosCoordenada.remove(event.getPlayer().getName());
            plugin.terrenosCoordenada2.remove(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onClickTerrenoBorda(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player p = (Player) e.getPlayer();
            if (p.getItemInHand().getType().equals(Material.STICK)) {
                Location loc = e.getClickedBlock().getLocation();
                for (ProtectedRegion r : plugin.getWorldGuard().getRegionManager(p.getWorld()).getApplicableRegions(loc)) {
                    if (!r.getOwners().contains(p.getName()) || !r.getId().contains("edterrenos_" + p.getName().toLowerCase() + "_")) {
                        return;
                    } else {

                        Location locClick = new Location(p.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

                        int tamanho = (r.getMaximumPoint().getBlockX() - r.getMinimumPoint().getBlockX());
                        if (tamanho < 0) {
                            tamanho = tamanho * -1;
                        }
                        tamanho += 1;

                        Location locClaim;
                        List<BlockVector2D> pontos = r.getPoints();
                        for (BlockVector2D o : pontos) {
                            locClaim = new Location(p.getWorld(), o.getBlockX(), loc.getBlockY(), o.getBlockZ());
                            if (locClaim.equals(locClick)) {
                                if (!plugin.terrenosCoordenada.containsKey(e.getPlayer().getName())) {
                                    plugin.terrenosCoordenada.put(p.getPlayer().getName(), r);
                                    plugin.terrenosCoordenada2.put(p.getPlayer().getName(), locClaim);
                                    p.sendMessage(plugin.getMessage("Mensagens.Terreno.expansaoAtiva"));
                                    return;

                                } else {
                                    plugin.terrenosCoordenada.remove(p.getPlayer().getName());
                                    plugin.terrenosCoordenada2.remove(e.getPlayer().getName());
                                    p.sendMessage(plugin.getMessage("Mensagens.Terreno.expansaoCancel"));
                                    return;
                                }
                            }
                        }

                    }

                }
                if (plugin.terrenosCoordenada.containsKey(p.getPlayer().getName())) {

                    ProtectedRegion r = plugin.terrenosCoordenada.get(p.getName());

                    int tamanho = plugin.terrenosCoordenada2.get(p.getName()).getBlockX() - loc.getBlockX();
                    if (tamanho < 0) {
                        tamanho = (tamanho * -1);
                    }
                    if (tamanho == 0) {
                        tamanho = plugin.terrenosCoordenada2.get(p.getName()).getBlockZ() - loc.getBlockZ();
                        if (tamanho < 0) {
                            tamanho = (tamanho * -1);
                        }
                    }

                    String locT = plugin.terrenosCoordenada2.get(p.getName()).getBlockX() + ", " + plugin.terrenosCoordenada2.get(p.getName()).getBlockZ();
                    String locTPosMax = r.getMaximumPoint().getBlockX() + ", " + r.getMaximumPoint().getBlockZ();
                    String locTPosMin = r.getMinimumPoint().getBlockX() + ", " + r.getMinimumPoint().getBlockZ();
                    BlockVector bv1 = null;
                    BlockVector bv2 = null;
                    if (locT.equals(locTPosMax)) {
                        bv1 = new BlockVector(loc.getBlockX(), 256, loc.getBlockZ());
                        bv2 = new BlockVector(r.getMinimumPoint().getBlockX(), 0, r.getMinimumPoint().getBlockZ());
                    }
                    if (locT.equals(locTPosMin)) {
                        bv1 = new BlockVector(r.getMaximumPoint().getBlockX(), 256, r.getMaximumPoint().getBlockZ());
                        bv2 = new BlockVector(loc.getBlockX(), 0, loc.getBlockZ());
                    }
                    locTPosMax = r.getMaximumPoint().getBlockX() + ", " + r.getMinimumPoint().getBlockZ();
                    locTPosMin = r.getMinimumPoint().getBlockX() + ", " + r.getMaximumPoint().getBlockZ();

                    if (locT.equals(locTPosMax)) {
                        bv1 = new BlockVector(loc.getBlockX(), 256, loc.getBlockZ());
                        bv2 = new BlockVector(r.getMinimumPoint().getBlockX(), 0, r.getMaximumPoint().getBlockZ());
                    }

                    if (locT.equals(locTPosMin)) {
                        bv1 = new BlockVector(r.getMaximumPoint().getBlockX(), 256, r.getMinimumPoint().getBlockZ());
                        bv2 = new BlockVector(loc.getBlockX(), 0, loc.getBlockZ());
                    }

                    String area = r.getId();

                    Funçoes f = new Funçoes();
                    RegionManager rm = plugin.getWorldGuard().getRegionManager(p.getWorld());

                    ProtectedCuboidRegion pr = new ProtectedCuboidRegion(area, bv1, bv2);
                    List<ProtectedRegion> overlap = pr.getIntersectingRegions(rm.getRegions().values());
                    for (ProtectedRegion opr : overlap) {
                        if (opr.getId() != area) {
                            p.sendMessage(plugin.getMessage("Mensagens.Terreno.jaExisteTerrenoProximo"));
                            return;
                        }
                    }

                    int t = r.getMaximumPoint().getBlockX() - r.getMinimumPoint().getBlockX();
                    if (t < 0) {
                        t = (t * -1) + r.getMaximumPoint().getBlockZ() - r.getMinimumPoint().getBlockZ() + 1;
                    } else {
                        t = t + r.getMaximumPoint().getBlockZ() - r.getMinimumPoint().getBlockZ() + 1;
                    }

                    int t2 = pr.getMaximumPoint().getBlockX() - pr.getMinimumPoint().getBlockX();
                    if (t2 < 0) {
                        t2 = (t2 * -1) + pr.getMaximumPoint().getBlockZ() - pr.getMinimumPoint().getBlockZ() + 1;
                    } else {
                        t2 = t2 + pr.getMaximumPoint().getBlockZ() - pr.getMinimumPoint().getBlockZ() + 1;
                    }

                    if (t < t2) {
                        try {
                            if (plugin.getEconomy().getMoney(p.getName()) < plugin.getConfigDouble("Config.valorPorBlocoExpansao") * tamanho * tamanho) {
                                double valor = plugin.getConfigDouble("Config.valorPorBloco") * tamanho * tamanho;
                                p.sendMessage(plugin.getMessage("Mensagens.semDinheiro")
                                        .replaceAll("%dinheiro%", valor + "")
                                );
                                return;
                            } else {
                                t = (pr.getMaximumPoint().getBlockX() - pr.getMinimumPoint().getBlockX())
                                        * (pr.getMaximumPoint().getBlockZ() - pr.getMinimumPoint().getBlockZ());
                                
                                if (t < 0) {
                                    t = t * -1;
                                }
                                t = t + 1;

                                if (t >= (plugin.getConfigInt("Config.Tamanho.maximo"))) {
                                    p.sendMessage(
                                            plugin.getMessage("Mensagens.UsoCorreto.tamanhoMaximoUltrapassado")
                                                    .replaceAll("%tamanhoMax%", plugin.getConfigInt("Config.Tamanho.maximo") + "")
                                                    .replaceAll("%tamanho%", t + "")
                                    );
                                    return;
                                }

                                p.sendMessage(plugin.getMessage("Mensagens.Terreno.expandido"));
                                double valor = plugin.getConfigDouble("Config.valorPorBlocoExpansao") * tamanho * tamanho;
                                plugin.getEconomy().subtract(p.getName(), valor);
                                p.sendMessage(plugin.getMessage("Mensagens.Terreno.compradoDinheiroSubtraido")
                                        .replaceAll("%dinheiro%", valor + ""));
                            }
                        } catch (Exception err) {
                            double valor = plugin.getConfigDouble("Config.valorPorBlocoExpansao") * tamanho * tamanho;
                            p.sendMessage(plugin.getMessage("Mensagens.semDinheiro")
                                    .replaceAll("%dinheiro%", valor + "")
                            );
                            return;
                        }
                    } else {
                        t = (pr.getMaximumPoint().getBlockX() - pr.getMinimumPoint().getBlockX())
                                * (pr.getMaximumPoint().getBlockZ() - pr.getMinimumPoint().getBlockZ());
                       
                        if (t < 0) {
                            t = t * -1;
                        }
                        t = t + 1;

                        if (t <= (plugin.getConfigInt("Config.Tamanho.minimo"))) {
                            p.sendMessage(
                                    plugin.getMessage("Mensagens.UsoCorreto.tamanhoMinimoExigido")
                                            .replaceAll("%tamanhoMin%", plugin.getConfigInt("Config.Tamanho.minimo") + "")
                                            .replaceAll("%tamanho%", t + "")
                            );
                            return;
                        }
                        p.sendMessage(plugin.getMessage("Mensagens.Terreno.expandidoMenor"));
                    }

                    rm.removeRegion(area);
                    Location maxCoord = new Location(p.getWorld(), r.getMaximumPoint().getBlockX(), 256, r.getMaximumPoint().getBlockZ());
                    Location minCoord = new Location(p.getWorld(), r.getMinimumPoint().getBlockX(), 0, r.getMinimumPoint().getBlockZ());

                    f.removerCercas(maxCoord, minCoord);

                    pr = new ProtectedCuboidRegion(area, bv1, bv2);
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
                    } catch (Exception err) {
                        return;
                    }

                    maxCoord = new Location(p.getWorld(), pr.getMaximumPoint().getBlockX(), 256, pr.getMaximumPoint().getBlockZ());
                    minCoord = new Location(p.getWorld(), pr.getMinimumPoint().getBlockX(), 0, pr.getMinimumPoint().getBlockZ());
                    f.adicionarCercas(maxCoord, minCoord);
                    plugin.terrenosCoordenada.remove(p.getName());
                    plugin.terrenosCoordenada2.remove(p.getName());

                }

            }

        }
    }
}
