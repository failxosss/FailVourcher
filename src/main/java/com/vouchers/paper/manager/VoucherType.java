package com.vouchers.paper.manager;

import org.bukkit.Material;

import java.util.List;

public class VoucherType {

    private final String id;
    private final Material material;
    private final String displayName;
    private final List<String> lore;
    private final boolean glow;
    private final List<String> commands;
    private final String broadcastMessage;
    private final String receiveMessage;
    private final String redeemMessage;
    private final boolean consumeOnUse;
    private final int customModelData;

    public VoucherType(String id, Material material, String displayName, List<String> lore,
                        boolean glow, List<String> commands, String broadcastMessage,
                        String receiveMessage, String redeemMessage, boolean consumeOnUse,
                        int customModelData) {
        this.id = id;
        this.material = material;
        this.displayName = displayName;
        this.lore = lore;
        this.glow = glow;
        this.commands = commands;
        this.broadcastMessage = broadcastMessage;
        this.receiveMessage = receiveMessage;
        this.redeemMessage = redeemMessage;
        this.consumeOnUse = consumeOnUse;
        this.customModelData = customModelData;
    }

    public String getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public boolean isGlow() {
        return glow;
    }

    public List<String> getCommands() {
        return commands;
    }

    public String getBroadcastMessage() {
        return broadcastMessage;
    }

    public String getReceiveMessage() {
        return receiveMessage;
    }

    public String getRedeemMessage() {
        return redeemMessage;
    }

    public boolean isConsumeOnUse() {
        return consumeOnUse;
    }

    public int getCustomModelData() {
        return customModelData;
    }
}
