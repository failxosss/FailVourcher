package com.vouchers.paper.commands;

import com.vouchers.paper.VoucherPlugin;
import com.vouchers.paper.manager.VoucherManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VoucherCommand implements CommandExecutor, TabCompleter {

    private final VoucherPlugin plugin;

    public VoucherCommand(VoucherPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "give": {
                if (!sender.hasPermission("voucher.admin")) {
                    sender.sendMessage(VoucherManager.color("&cNemas opravneni k pouziti tohoto prikazu."));
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(VoucherManager.color("&cPouziti: /voucher give <hrac> <voucher> [pocet]"));
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    sender.sendMessage(VoucherManager.color("&cHrac '" + args[1] + "' neni online."));
                    return true;
                }
                String voucherId = args[2];
                int amount = 1;
                if (args.length >= 4) {
                    try {
                        amount = Math.max(1, Integer.parseInt(args[3]));
                    } catch (NumberFormatException ignored) {
                        // pouzije se vychozi 1
                    }
                }

                boolean success = plugin.getVoucherManager().giveVoucher(target, voucherId, amount);
                if (!success) {
                    sender.sendMessage(VoucherManager.color("&cVoucher s ID '" + voucherId + "' neexistuje. Zkontroluj config.yml."));
                    return true;
                }

                sender.sendMessage(VoucherManager.color("&aHraci " + target.getName()
                        + " byl predan voucher '" + voucherId + "' (" + amount + "x)."));
                return true;
            }
            case "list": {
                if (!sender.hasPermission("voucher.admin")) {
                    sender.sendMessage(VoucherManager.color("&cNemas opravneni k pouziti tohoto prikazu."));
                    return true;
                }
                if (plugin.getVoucherManager().getVouchers().isEmpty()) {
                    sender.sendMessage(VoucherManager.color("&eZadne vouchery nejsou nastaveny v config.yml."));
                    return true;
                }
                String ids = String.join(", ", plugin.getVoucherManager().getVouchers().keySet());
                sender.sendMessage(VoucherManager.color("&6Dostupne vouchery: &f" + ids));
                return true;
            }
            case "reload": {
                if (!sender.hasPermission("voucher.admin")) {
                    sender.sendMessage(VoucherManager.color("&cNemas opravneni k pouziti tohoto prikazu."));
                    return true;
                }
                plugin.getVoucherManager().loadVouchers();
                sender.sendMessage(VoucherManager.color("&aConfig byl znovu nacten. Nacteno "
                        + plugin.getVoucherManager().getVouchers().size() + " voucheru."));
                return true;
            }
            default:
                sendUsage(sender);
                return true;
        }
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(VoucherManager.color("&6--- VoucherPlugin ---"));
        sender.sendMessage(VoucherManager.color("&e/voucher give <hrac> <voucher> [pocet] &7- da hraci voucher"));
        sender.sendMessage(VoucherManager.color("&e/voucher list &7- vypise vsechny dostupne vouchery"));
        sender.sendMessage(VoucherManager.color("&e/voucher reload &7- znovu nacte config.yml"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(List.of("give", "list", "reload"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                completions.add(p.getName());
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            completions.addAll(plugin.getVoucherManager().getVouchers().keySet());
        }

        String current = args[args.length - 1].toLowerCase();
        return completions.stream().filter(s -> s.toLowerCase().startsWith(current)).collect(Collectors.toList());
    }
}
