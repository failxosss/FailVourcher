package com.vouchers.paper;

import com.vouchers.paper.commands.VoucherCommand;
import com.vouchers.paper.listeners.VoucherListener;
import com.vouchers.paper.manager.VoucherManager;
import org.bukkit.plugin.java.JavaPlugin;

public class VoucherPlugin extends JavaPlugin {

    private static VoucherPlugin instance;
    private VoucherManager voucherManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.voucherManager = new VoucherManager(this);
        this.voucherManager.loadVouchers();

        VoucherCommand commandExecutor = new VoucherCommand(this);
        if (getCommand("voucher") != null) {
            getCommand("voucher").setExecutor(commandExecutor);
            getCommand("voucher").setTabCompleter(commandExecutor);
        }

        getServer().getPluginManager().registerEvents(new VoucherListener(this), this);

        getLogger().info("VoucherPlugin byl uspesne nacten! Nacteno "
                + voucherManager.getVouchers().size() + " typu voucheru.");
    }

    @Override
    public void onDisable() {
        getLogger().info("VoucherPlugin byl vypnut.");
    }

    public VoucherManager getVoucherManager() {
        return voucherManager;
    }

    public static VoucherPlugin getInstance() {
        return instance;
    }
}
