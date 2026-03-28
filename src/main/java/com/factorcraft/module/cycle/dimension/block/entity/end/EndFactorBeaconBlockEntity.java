package com.factorcraft.module.cycle.dimension.block.entity.end;

import com.factorcraft.module.cycle.dimension.end.block.EndFactorBeaconBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * 末地 Factor 信标 BlockEntity
 * 提供增益效果，稳定 Factor 浓度
 */
public class EndFactorBeaconBlockEntity extends BlockEntity {
    private boolean active = false;
    private int range = 50;
    
    public EndFactorBeaconBlockEntity(BlockPos pos, BlockState state) {
        super(com.factorcraft.module.cycle.dimension.block.entity.DimensionBlockEntities.END_FACTOR_BEACON, pos, state);
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.writeNbt(nbt, lookup);
        nbt.putBoolean("active", active);
        nbt.putInt("range", range);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.readNbt(nbt, lookup);
        active = nbt.getBoolean("active");
        range = nbt.getInt("range");
    }
    
    public static void tick(World world, BlockPos pos, BlockState state, EndFactorBeaconBlockEntity blockEntity) {
        if (world.isClient || !blockEntity.active) {
            return;
        }
        
        blockEntity.applyEffects(world, pos);
    }
    
    private void applyEffects(World world, BlockPos pos) {
        if (world.isClient) {
            return;
        }
        
        // 使用固定范围检测玩家
        world.getEntitiesByClass(PlayerEntity.class,
            new net.minecraft.util.math.Box(
                pos.getX() - range, pos.getY() - range, pos.getZ() - range,
                pos.getX() + range, pos.getY() + range, pos.getZ() + range
            ),
            entity -> entity.squaredDistanceTo(Vec3d.ofCenter(pos)) <= range * range
        ).forEach(player -> {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 0, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 100, 0, true, false));
        });
    }
    
    public void toggleActive() {
        this.active = !this.active;
        markDirty();
    }
    
    public boolean isActive() {
        return active;
    }
    
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
    
    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup lookup) {
        return createNbt(lookup);
    }
}
