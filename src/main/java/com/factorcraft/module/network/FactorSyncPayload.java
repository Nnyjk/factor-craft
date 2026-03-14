package com.factorcraft.module.network;

import com.factorcraft.module.factor.management.ChunkFactorManager;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;

public record FactorSyncPayload(ChunkPos chunkPos, double concentration) implements CustomPayload {
    public static final CustomPayload.Id<FactorSyncPayload> ID = 
        new CustomPayload.Id<>(Identifier.of("factorcraft", "factor_sync"));
    
    public static final PacketCodec<RegistryByteBuf, FactorSyncPayload> CODEC = 
        PacketCodec.of(FactorSyncPayload::write, FactorSyncPayload::read);
    
    private void write(RegistryByteBuf buf) {
        buf.writeInt(chunkPos.x);
        buf.writeInt(chunkPos.z);
        buf.writeDouble(concentration);
    }
    
    private static FactorSyncPayload read(RegistryByteBuf buf) {
        int x = buf.readInt();
        int z = buf.readInt();
        double concentration = buf.readDouble();
        return new FactorSyncPayload(new ChunkPos(x, z), concentration);
    }
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    
    public static void sendToPlayer(ServerPlayerEntity player, ChunkPos chunkPos) {
        ChunkFactorManager.getState(chunkPos).ifPresent(state -> {
            FactorSyncPayload payload = new FactorSyncPayload(chunkPos, state.getCurrentConcentration());
            ServerPlayNetworking.send(player, payload);
        });
    }
}