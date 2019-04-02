/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edterrenos;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author EduardoMGP
 */
public class Comandos implements CommandExecutor {

    private static EDTerrenos plugin;

    public Comandos() {
        plugin = EDTerrenos.getPlugin();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
        if (args.length == 0) {
            double valor = plugin.getConfigDouble("Config.valorPorBloco");
            for (String m : EDTerrenos.getPlugin().getConfig().getStringList("Mensagens.UsoCorreto.terreno")) {
                sender.sendMessage(m
                        .replaceAll("&", "§")
                        .replaceAll("%dinheiro%", valor + "")
                );
            }
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("terreno")) {
            if (args[0].equalsIgnoreCase("comprar")
                    || args[0].equalsIgnoreCase("vender")
                    || args[0].equalsIgnoreCase("abandonar")
                    || args[0].equalsIgnoreCase("expandir")
                    || args[0].equalsIgnoreCase("addamigo")
                    || args[0].equalsIgnoreCase("removeamigo")
                    || args[0].equalsIgnoreCase("addmsg")
                    || args[0].equalsIgnoreCase("addmsgentry")
                    || args[0].equalsIgnoreCase("removemsg")
                    || args[0].equalsIgnoreCase("removemsgentry")
                    || args[0].equalsIgnoreCase("entrar")
                    || args[0].equalsIgnoreCase("addentrar")
                    || args[0].equalsIgnoreCase("removeentrar")
                    || args[0].equalsIgnoreCase("pvp")
                    || args[0].equalsIgnoreCase("spawnmobs")
                    || args[0].equalsIgnoreCase("info")
                    || args[0].equalsIgnoreCase("teleportar")
                    || args[0].equalsIgnoreCase("lista")) {

                if (args[0].equalsIgnoreCase("comprar")) {

                    if (args.length < 3) {
                        sender.sendMessage(plugin.getMessage("Mensagens.UsoCorreto.comprar"));
                        return true;
                    }

                    if (!(sender instanceof Player)) {

                        sender.sendMessage(plugin.getMessage("Mensagens.Apenas_Jogadores"));

                        return true;
                    }

                    Player p = (Player) sender;
                    int tamanho = 0;
                    try {
                        tamanho = Integer.parseInt(args[1]);
                        if (tamanho <= 0) {
                            sender.sendMessage(plugin.getMessage("Mensagens.UsoCorreto.tamanho"));
                            return true;
                        }
                    } catch (Exception e) {
                        sender.sendMessage(plugin.getMessage("Mensagens.UsoCorreto.tamanho"));
                        return true;
                    }
                    if (tamanho * tamanho > plugin.getConfigInt("Config.Tamanho.maximo")) {
                        sender.sendMessage(
                                plugin.getMessage("Mensagens.UsoCorreto.tamanhoMaximoUltrapassado")
                                        .replaceAll("%tamanhoMax%", plugin.getConfigInt("Config.Tamanho.maximo") + "")
                                        .replaceAll("%tamanho%", tamanho * tamanho + "")
                        );
                        return true;
                    }

                    if (tamanho * tamanho < plugin.getConfigInt("Config.Tamanho.minimo")) {
                        sender.sendMessage(
                                plugin.getMessage("Mensagens.UsoCorreto.tamanhoMinimoExigido")
                                        .replaceAll("%tamanhoMin%", plugin.getConfigInt("Config.Tamanho.minimo") + "")
                                        .replaceAll("%tamanho%", tamanho * tamanho + "")
                        );
                        return true;
                    }

                    Funçoes f = new Funçoes();
                    f.comprarTerreno(p, tamanho, args[2]);

                }

                if (args[0].equalsIgnoreCase("addamigo")) {
                    if (args.length <= 1) {
                        sender.sendMessage(plugin.getMessage("Mensagens.UsoCorreto.addamigo"));
                        return true;
                    }
                    Player p = (Player) sender;
                    Player p2 = Bukkit.getPlayer(args[1]);
                    if (p2 == null) {
                        sender.sendMessage(plugin.getMessage("Mensagens.Terreno.naoEstaOnline"));
                        return true;
                    }
                    for (ProtectedRegion r : plugin.getWorldGuard().getRegionManager(p.getWorld()).getApplicableRegions(p.getLocation())) {
                        if (!r.getOwners().contains(p.getName()) || !r.getId().contains("edterrenos_" + p.getName().toLowerCase() + "_")) {
                            p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoeSeu"));
                            return true;
                        } else {
                            if (r.getOwners().contains(p2.getName())) {
                                p.sendMessage(plugin.getMessage("Mensagens.Terreno.amigoJaAdicionado").replaceAll("%player%", p2.getName()));
                                return true;
                            } else {
                                r.getOwners().addPlayer(p2.getName());
                                p.sendMessage(plugin.getMessage("Mensagens.Terreno.amigoAdicionado").replaceAll("%player%", p2.getName()));
                                return true;
                            }

                        }
                    }
                    p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoTem"));
                }

                if (args[0].equalsIgnoreCase("removeamigo")) {
                    if (args.length <= 1) {
                        sender.sendMessage(plugin.getMessage("Mensagens.UsoCorreto.removeamigo"));
                        return true;
                    }
                    Player p = (Player) sender;
                    for (ProtectedRegion r : plugin.getWorldGuard().getRegionManager(p.getWorld()).getApplicableRegions(p.getLocation())) {
                        if (!r.getOwners().contains(p.getName()) || !r.getId().contains("edterrenos_" + p.getName().toLowerCase() + "_")) {
                            p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoeSeu"));
                            return true;
                        } else {
                            if (!r.getOwners().contains(args[1])) {
                                p.sendMessage(plugin.getMessage("Mensagens.Terreno.amigoNaodicionado").replaceAll("%player%", args[1]));
                                return true;
                            } else {
                                r.getOwners().removePlayer(args[1]);
                                p.sendMessage(plugin.getMessage("Mensagens.Terreno.amigoRemovido").replaceAll("%player%", args[1]));
                                return true;
                            }

                        }
                    }
                    p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoTem"));
                }

                if (args[0].equalsIgnoreCase("addentrar")) {
                    if (args.length <= 1) {
                        sender.sendMessage(plugin.getMessage("Mensagens.UsoCorreto.addamigo"));
                        return true;
                    }
                    Player p = (Player) sender;
                    Player p2 = Bukkit.getPlayer(args[1]);
                    if (p2 == null) {
                        sender.sendMessage(plugin.getMessage("Mensagens.Terreno.naoEstaOnline"));
                        return true;
                    }
                    for (ProtectedRegion r : plugin.getWorldGuard().getRegionManager(p.getWorld()).getApplicableRegions(p.getLocation())) {
                        if (!r.getOwners().contains(p.getName()) || !r.getId().contains("edterrenos_" + p.getName().toLowerCase() + "_")) {
                            p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoeSeu"));
                            return true;
                        } else {
                            if (r.getMembers().contains(p2.getName())) {
                                p.sendMessage(plugin.getMessage("Mensagens.Terreno.amigoJaAdicionadoEntry").replaceAll("%player%", p2.getName()));
                                return true;
                            } else {
                                r.getMembers().addPlayer(p2.getName());
                                p.sendMessage(plugin.getMessage("Mensagens.Terreno.amigoAdicionadoEntry").replaceAll("%player%", p2.getName()));
                                return true;
                            }

                        }
                    }
                    p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoTem"));
                }

                if (args[0].equalsIgnoreCase("removeentrar")) {
                    if (args.length <= 1) {
                        sender.sendMessage(plugin.getMessage("Mensagens.UsoCorreto.removeamigo"));
                        return true;
                    }
                    Player p = (Player) sender;
                    for (ProtectedRegion r : plugin.getWorldGuard().getRegionManager(p.getWorld()).getApplicableRegions(p.getLocation())) {
                        if (!r.getOwners().contains(p.getName()) || !r.getId().contains("edterrenos_" + p.getName().toLowerCase() + "_")) {
                            p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoeSeu"));
                            return true;
                        } else {
                            if (!r.getMembers().contains(args[1])) {
                                p.sendMessage(plugin.getMessage("Mensagens.Terreno.amigoNaodicionadoEntry").replaceAll("%player%", args[1]));
                                return true;
                            } else {
                                r.getMembers().removePlayer(args[1]);
                                p.sendMessage(plugin.getMessage("Mensagens.Terreno.amigoRemovidoEntry").replaceAll("%player%", args[1]));
                                return true;
                            }

                        }
                    }
                    p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoTem"));
                }

                if (args[0].equalsIgnoreCase("removeentrar")) {
                    if (args.length <= 1) {
                        sender.sendMessage(plugin.getMessage("Mensagens.UsoCorreto.removeamigo"));
                        return true;
                    }
                    Player p = (Player) sender;
                    for (ProtectedRegion r : plugin.getWorldGuard().getRegionManager(p.getWorld()).getApplicableRegions(p.getLocation())) {
                        if (!r.getOwners().contains(p.getName()) || !r.getId().contains("edterrenos_" + p.getName().toLowerCase() + "_")) {
                            p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoeSeu"));
                            return true;
                        } else {
                            if (!r.getMembers().contains(args[1])) {
                                p.sendMessage(plugin.getMessage("Mensagens.Terreno.amigoNaodicionadoEntry").replaceAll("%player%", args[1]));
                                return true;
                            } else {
                                r.getMembers().removePlayer(args[1]);
                                p.sendMessage(plugin.getMessage("Mensagens.Terreno.amigoRemovidoEntry").replaceAll("%player%", args[1]));
                                return true;
                            }

                        }
                    }
                    p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoTem"));
                }

                if (args[0].equalsIgnoreCase("abandonar")) {
                    Player p = (Player) sender;
                    for (ProtectedRegion r : plugin.getWorldGuard().getRegionManager(p.getWorld()).getApplicableRegions(p.getLocation())) {
                        if (!r.getOwners().contains(p.getName()) || !r.getId().contains("edterrenos_" + p.getName().toLowerCase() + "_")) {
                            p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoeSeu"));
                            return true;
                        } else {
                            Funçoes f = new Funçoes();
                            int maxX = (int) (r.getMaximumPoint().getX());
                            int minX = (int) (r.getMinimumPoint().getX());

                            int maxZ = (int) (r.getMaximumPoint().getZ());
                            int minZ = (int) (r.getMinimumPoint().getZ());

                            int tamanho = maxX - minX;
                            Location maxCoord = new Location(p.getWorld(), maxX, 256, maxZ);
                            Location minCoord = new Location(p.getWorld(), minX, 0, minZ);

                            f.removerCercas(maxCoord, minCoord);
                            plugin.getWorldGuard().getRegionManager(p.getWorld()).removeRegion(r.getId());

                            p.sendMessage(plugin.getMessage("Mensagens.Terreno.removido"));
                            return true;
                        }

                    }
                    p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoTem"));
                }

                if (args[0].equalsIgnoreCase("expandir")) {
                    for (String m : plugin.getConfig().getStringList("Mensagens.Terreno.expandirExplicacao")) {
                        sender.sendMessage(m.replaceAll("&", "§"));
                    }
                }

                if (args[0].equalsIgnoreCase("teleportar")) {
                    if (args.length <= 1) {
                        sender.sendMessage(plugin.getMessage("Mensagens.UsoCorreto.teleportar"));
                        return true;
                    }
                    Player p = (Player) sender;
                    World world = p.getWorld();
                    String id = "EDTerrenos_" + p.getName() + "_" + args[1].toLowerCase();
                    ProtectedRegion r = plugin.getWorldGuard().getRegionManager(world).getRegion(id);
                    if (r != null) {
                        double t = r.getMaximumPoint().getX() - r.getMinimumPoint().getX();
                        int x = (int) (r.getMaximumPoint().getX() - (t / 2));
                        int z = (int) (r.getMaximumPoint().getZ() - (t / 2) + 1);
                        int y = world.getMaxHeight() - 1;
                        Block block = world.getBlockAt(x, y, z);
                        for (int i = 0; i < world.getMaxHeight(); i++) {
                            if (block.getType().equals(Material.AIR)) {
                                y--;
                                block = world.getBlockAt(x, y, z);
                            } else {
                                block = world.getBlockAt(x, y + 1, z);
                                p.teleport(new Location(world, x, y + 1, z));
                                break;
                            }
                        }
                        p.sendMessage(plugin.getMessage("Mensagens.Terreno.teleportar")
                                .replaceAll("%nome%", args[1]));
                        return true;
                    } else {
                        p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoExiste"));
                        return true;
                    }

                }
                
                if (args[0].equalsIgnoreCase("removemsg")) {
                    Player p = (Player) sender;
                    for (ProtectedRegion r : plugin.getWorldGuard().getRegionManager(p.getWorld()).getApplicableRegions(p.getLocation())) {
                        if (!r.getOwners().contains(p.getName()) || !r.getId().contains("edterrenos_" + p.getName().toLowerCase() + "_")) {
                            p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoeSeu"));
                            return true;
                        } else {
                            try {
                                r.setFlag(DefaultFlag.GREET_MESSAGE, DefaultFlag.GREET_MESSAGE.parseInput(plugin.getWorldGuard(), p, ""));
                                p.sendMessage(plugin.getMessage("Mensagens.Terreno.msgRemovida"));
                                return true;
                            } catch (Exception e) {
                                return true;
                            }

                        }
                    }
                    p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoTem"));
                }

                if (args[0].equalsIgnoreCase("addmsgentry")) {
                    if (args.length <= 1) {
                        sender.sendMessage(plugin.getMessage("Mensagens.UsoCorreto.addmsgEntry"));
                        return true;
                    }
                    Player p = (Player) sender;
                    for (ProtectedRegion r : plugin.getWorldGuard().getRegionManager(p.getWorld()).getApplicableRegions(p.getLocation())) {
                        if (!r.getOwners().contains(p.getName()) || !r.getId().contains("edterrenos_" + p.getName().toLowerCase() + "_")) {
                            p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoeSeu"));
                            return true;
                        } else {
                            String msg = null;
                            for (int i = 1; i < args.length; i++) {

                                if (msg == null) {
                                    msg = args[i];
                                } else {
                                    msg += " " + args[i];
                                }

                            }
                            try {
                                r.setFlag(DefaultFlag.ENTRY_DENY_MESSAGE, DefaultFlag.ENTRY_DENY_MESSAGE.parseInput(plugin.getWorldGuard(), p, msg.replaceAll("&", "§")));
                                p.sendMessage(plugin.getMessage("Mensagens.Terreno.msgAdicionadaEntry"));
                                return true;
                            } catch (Exception e) {
                                return true;
                            }

                        }
                    }
                    p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoTem"));
                }

                if (args[0].equalsIgnoreCase("addmsg")) {
                    if (args.length <= 1) {
                        sender.sendMessage(plugin.getMessage("Mensagens.UsoCorreto.addmsg"));
                        return true;
                    }
                    Player p = (Player) sender;
                    for (ProtectedRegion r : plugin.getWorldGuard().getRegionManager(p.getWorld()).getApplicableRegions(p.getLocation())) {
                        if (!r.getOwners().contains(p.getName()) || !r.getId().contains("edterrenos_" + p.getName().toLowerCase() + "_")) {
                            p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoeSeu"));
                            return true;
                        } else {
                            String msg = null;
                            for (int i = 1; i < args.length; i++) {

                                if (msg == null) {
                                    msg = args[i];
                                } else {
                                    msg += " " + args[i];
                                }

                            }
                            try {
                                r.setFlag(DefaultFlag.GREET_MESSAGE, DefaultFlag.GREET_MESSAGE.parseInput(plugin.getWorldGuard(), p, msg.replaceAll("&", "§")));
                                p.sendMessage(plugin.getMessage("Mensagens.Terreno.msgAdicionada"));
                                return true;
                            } catch (Exception e) {
                                return true;
                            }

                        }
                    }
                    p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoTem"));
                }

                if (args[0].equalsIgnoreCase("removemsgentry")) {
                    Player p = (Player) sender;
                    for (ProtectedRegion r : plugin.getWorldGuard().getRegionManager(p.getWorld()).getApplicableRegions(p.getLocation())) {
                        if (!r.getOwners().contains(p.getName()) || !r.getId().contains("edterrenos_" + p.getName().toLowerCase() + "_")) {
                            p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoeSeu"));
                            return true;
                        } else {
                            try {
                                r.setFlag(DefaultFlag.ENTRY_DENY_MESSAGE, DefaultFlag.ENTRY_DENY_MESSAGE.parseInput(plugin.getWorldGuard(), p, ""));
                                p.sendMessage(plugin.getMessage("Mensagens.Terreno.msgRemovidaEntry"));
                                return true;
                            } catch (Exception e) {
                                return true;
                            }

                        }
                    }
                    p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoTem"));
                }

                if (args[0].equalsIgnoreCase("entrar")) {
                    if (args.length != 2) {
                        sender.sendMessage(plugin.getMessage("Mensagens.UsoCorreto.entrar"));
                        return true;
                    }
                    Player p = (Player) sender;
                    for (ProtectedRegion r : plugin.getWorldGuard().getRegionManager(p.getWorld()).getApplicableRegions(p.getLocation())) {
                        if (!r.getOwners().contains(p.getName()) || !r.getId().contains("edterrenos_" + p.getName().toLowerCase() + "_")) {
                            p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoeSeu"));
                            return true;
                        } else {

                            if (args[1].equalsIgnoreCase("on")) {
                                try {
                                    r.setFlag(DefaultFlag.ENTRY, DefaultFlag.ENTRY.parseInput(plugin.getWorldGuard(), p, "deny"));
                                    p.sendMessage(plugin.getMessage("Mensagens.Terreno.msgEntrarAtivado"));
                                    return true;
                                } catch (Exception e) {
                                    return true;
                                }

                            } else {
                                if (args[1].equalsIgnoreCase("off")) {
                                    try {
                                        r.setFlag(DefaultFlag.ENTRY, DefaultFlag.ENTRY.parseInput(plugin.getWorldGuard(), p, "allow"));
                                        p.sendMessage(plugin.getMessage("Mensagens.Terreno.msgEntrarDesativado"));
                                        return true;
                                    } catch (Exception e) {
                                        return true;
                                    }

                                } else {
                                    sender.sendMessage(plugin.getMessage("Mensagens.UsoCorreto.entrar"));
                                    return true;
                                }
                            }

                        }
                    }
                    p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoTem"));
                }

                if (args[0].equalsIgnoreCase("lista")) {
                    Player p = (Player) sender;
                    RegionManager rm = plugin.getWorldGuard().getRegionManager(p.getWorld());
                    Map<String, ProtectedRegion> region = rm.getRegions();
                    String rg = null;
                    int quantidadeTerreno = 0;
                    for (String id : region.keySet()) {
                        if (id.contains("edterrenos_" + p.getName().toLowerCase() + "_")) {
                            if (rg == null) {
                                rg = id;
                            } else {
                                rg += ", " + id;
                            }
                            quantidadeTerreno++;
                        }
                    }
                    if (quantidadeTerreno > 0) {
                        String terrenos = "edterrenos_" + p.getName().toLowerCase() + "_";
                        p.sendMessage(plugin.getMessage("Mensagens.Terreno.terrenosLista")
                                .replaceAll("%quantidade%", quantidadeTerreno + "")
                                .replaceAll("%terrenos%", rg.replaceAll(terrenos, "")));
                    } else {
                        p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoTemTerrenos"));
                    }
                }

                if (args[0].equalsIgnoreCase("pvp")) {
                    if (args.length != 2) {
                        sender.sendMessage(plugin.getMessage("Mensagens.UsoCorreto.pvp"));
                        return true;
                    }
                    Player p = (Player) sender;
                    for (ProtectedRegion r : plugin.getWorldGuard().getRegionManager(p.getWorld()).getApplicableRegions(p.getLocation())) {
                        if (!r.getOwners().contains(p.getName()) || !r.getId().contains("edterrenos_" + p.getName().toLowerCase() + "_")) {
                            p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoeSeu"));
                            return true;
                        } else {

                            if (args[1].equalsIgnoreCase("on")) {
                                try {
                                    r.setFlag(DefaultFlag.PVP, DefaultFlag.PVP.parseInput(plugin.getWorldGuard(), p, "allow"));
                                    p.sendMessage(plugin.getMessage("Mensagens.Terreno.pvpAtivado"));
                                } catch (Exception e) {
                                }

                            } else {
                                if (args[1].equalsIgnoreCase("off")) {
                                    try {
                                        r.setFlag(DefaultFlag.PVP, DefaultFlag.PVP.parseInput(plugin.getWorldGuard(), p, "deny"));
                                        p.sendMessage(plugin.getMessage("Mensagens.Terreno.pvpDesativado"));
                                        return true;
                                    } catch (Exception e) {
                                        return true;
                                    }

                                } else {
                                    sender.sendMessage(plugin.getMessage("Mensagens.UsoCorreto.pvp"));
                                    return true;
                                }
                            }

                        }
                    }
                    p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoTem"));
                }

                if (args[0].equalsIgnoreCase("spawnmobs")) {
                    if (args.length != 2) {
                        sender.sendMessage(plugin.getMessage("Mensagens.UsoCorreto.spawnmobs"));
                        return true;
                    }
                    Player p = (Player) sender;
                    for (ProtectedRegion r : plugin.getWorldGuard().getRegionManager(p.getWorld()).getApplicableRegions(p.getLocation())) {
                        if (!r.getOwners().contains(p.getName()) || !r.getId().contains("edterrenos_" + p.getName().toLowerCase() + "_")) {
                            p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoeSeu"));
                            return true;
                        } else {

                            if (args[1].equalsIgnoreCase("on")) {
                                try {
                                    r.setFlag(DefaultFlag.MOB_SPAWNING, DefaultFlag.MOB_SPAWNING.parseInput(plugin.getWorldGuard(), p, "allow"));
                                    p.sendMessage(plugin.getMessage("Mensagens.Terreno.spawnmobsAtivado"));
                                } catch (Exception e) {
                                }

                            } else {
                                if (args[1].equalsIgnoreCase("off")) {
                                    try {
                                        r.setFlag(DefaultFlag.MOB_SPAWNING, DefaultFlag.MOB_SPAWNING.parseInput(plugin.getWorldGuard(), p, "deny"));
                                        p.sendMessage(plugin.getMessage("Mensagens.Terreno.spawnmobsDesativado"));
                                        return true;
                                    } catch (Exception e) {
                                        return true;
                                    }

                                } else {
                                    sender.sendMessage(plugin.getMessage("Mensagens.UsoCorreto.spawnmobs"));
                                    return true;
                                }
                            }

                        }
                    }
                    p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoTem"));
                }

                if (args[0].equalsIgnoreCase("info")) {
                    Player p = (Player) sender;

                    for (ProtectedRegion r : plugin.getWorldGuard().getRegionManager(p.getWorld()).getApplicableRegions(p.getLocation())) {
                        if (!r.getId().contains("edterrenos_")) {
                            p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoeTerreno"));
                            return true;
                        } else {
                            int t = (int) (r.getMaximumPoint().getX() - r.getMinimumPoint().getX() + 1);
                            int t2 = (int) (r.getMaximumPoint().getZ() - r.getMinimumPoint().getZ() + 1);
                            for (String m : plugin.getConfig().getStringList("Mensagens.Terreno.info")) {

                                p.sendMessage(
                                        m.replaceAll("%nome%", r.getId().replaceAll("edterrenos_eduardomgp_", ""))
                                                .replaceAll("%owner%", r.getOwners().toPlayersString().replaceAll("name:", ""))
                                                .replaceAll("%pvp%", r.getFlag(DefaultFlag.PVP).toString().replace("DENY", "OFF").replace("ALLOW", "ON"))
                                                .replaceAll("%msg%", (r.getFlag(DefaultFlag.GREET_MESSAGE) + "").replaceAll("null", "Nenhuma"))
                                                .replaceAll("%msg2%", (r.getFlag(DefaultFlag.ENTRY_DENY_MESSAGE) + "").replaceAll("null", "Nenhuma"))
                                                .replaceAll("%spawn%", r.getFlag(DefaultFlag.MOB_SPAWNING).toString().replace("DENY", "OFF").replace("ALLOW", "ON"))
                                                .replaceAll("%size%", t + "x" + t2)
                                                .replaceAll("%amigos%", r.getOwners().toPlayersString().replaceAll("name:", ""))
                                                .replaceAll("%ep%", r.getMembers().toPlayersString().replaceAll("name:", ""))
                                                .replaceAll("&", "§")
                                );

                            }
                            return true;
                        }

                    }
                    p.sendMessage(plugin.getMessage("Mensagens.Terreno.naoTem"));

                }
            } else {

                double valor = plugin.getConfigDouble("Config.valorPorBloco");
                for (String m : EDTerrenos.getPlugin().getConfig().getStringList("Mensagens.UsoCorreto.terreno")) {
                    sender.sendMessage(m
                            .replaceAll("&", "§")
                            .replaceAll("%dinheiro%", valor + "")
                    );
                }
            }

        }

        return true;
    }

}
