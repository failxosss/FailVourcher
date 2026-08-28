package com.vouchers.paper.listeners;

import com.vouchers.paper.VoucherPlugin;
import com.vouchers.paper.manager.VoucherManager;
import com.vouchers.paper.manager.VoucherType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class VoucherListener implements Listener {

    private final VoucherPlugin plugin;

    public VoucherListener(VoucherPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        // Aby se event nezpracoval 2x (jednou pro hlavni a jednou pro off-hand)
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        VoucherManager manager = plugin.getVoucherManager();
        String voucherId = manager.getVoucherIdFromItem(item);
        if (voucherId == null) return;

        event.setCancelled(true);

        VoucherType type = manager.getVoucher(voucherId);
        if (type == null) {
            player.sendMessage(VoucherManager.color("&cTento voucher jiz neni platny (byl zmenen nebo odebran z configu)."));
            return;
        }

        manager.redeemVoucher(player, type);

        if (type.isConsumeOnUse()) {
            item.setAmount(item.getAmount() - 1);
        }
    }
}
