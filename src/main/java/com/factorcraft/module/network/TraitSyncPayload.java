package com.factorcraft.module.network;

import com.factorcraft.module.material.trait.TraitInstance;
import com.factorcraft.module.material.trait.TraitService;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public record TraitSyncPayload(int slot, List<TraitInstance> traits) implements CustomPayload {
    public static final CustomPayload.Id<TraitSyncPayload> ID = 
        new CustomPayload.Id<>(Identifier.of("factorcraft", "trait_sync"));
    
    public static final PacketCodec<RegistryByteBuf, TraitSyncPayload> CODEC = 
        PacketCodec.of(TraitSyncPayload::write, TraitSyncPayload::read);
    
    private void write(RegistryByteBuf buf) {
        buf.writeInt(slot);
        buf.writeInt(traits.size());
        for (TraitInstance trait : traits) {
            buf.writeString(trait.traitId());
            buf.writeInt(trait.level());
        }
    }
    
    private static TraitSyncPayload read(RegistryByteBuf buf) {
        int slot = buf.readInt();
        int size = buf.readInt();
        List<TraitInstance> traits = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String traitId = buf.readString();
            int level = buf.readInt();
            traits.add(new TraitInstance(traitId, level));
        }
        return new TraitSyncPayload(slot, traits);
    }
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    
    public static void sendToPlayer(ServerPlayerEntity player, int slot, ItemStack stack) {
        List<TraitInstance> traits = TraitService.getTraits(stack);
        TraitSyncPayload payload = new TraitSyncPayload(slot, traits);
        ServerPlayNetworking.send(player, payload);
    }
}