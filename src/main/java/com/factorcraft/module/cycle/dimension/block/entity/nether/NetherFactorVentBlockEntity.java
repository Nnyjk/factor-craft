package com.factorcraft.module.cycle.dimension.block.entity.nether;

import com.factorcraft.module.cycle.dimension.nether.block.NetherFactorVentBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 下界 Factor 喷口 BlockEntity
 * 管理高温 Factor 的产生和扩散
 */
public class NetherFactorVentBlockEntity extends BlockEntity {
    private int factorLevel = 0;
    private int maxFactorLevel = 1000;
    private int diffusionRate = 3;
    
    public NetherFactorVentBlockEntity(BlockPos pos, BlockState state) {
        super(com.factorcraft.module.cycle.dimension.block.entity.DimensionBlockEntities.NETHER_FACTOR_VENT, pos, state);
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.writeNbt(nbt, lookup);
        nbt.putInt("factorLevel", factorLevel);
        nbt.putInt("maxFactorLevel", maxFactorLevel);
        nbt.putInt("diffusionRate", diffusionRate);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.readNbt(nbt, lookup);
        factorLevel = nbt.getInt("factorLevel");
        maxFactorLevel = nbt.getInt("maxFactorLevel");
        diffusionRate = nbt.getInt("diffusionRate");
    }
    
    public static void tick(World world, BlockPos pos, BlockState state, NetherFactorVentBlockEntity blockEntity) {
        if (world.isClient) {
            return;
        }
        
        if (blockEntity.factorLevel < blockEntity.maxFactorLevel) {
            blockEntity.factorLevel += blockEntity.diffusionRate;
            blockEntity.markDirty();
            
            if (blockEntity.factorLevel > 0 && !state.get(NetherFactorVentBlock.ACTIVE)) {
                world.setBlockState(pos, state.with(NetherFactorVentBlock.ACTIVE, true));
            }
        }
        
        if (blockEntity.factorLevel >= 100) {
            blockEntity.diffuseFactor(world, pos);
        }
    }
    
    private void diffuseFactor(World world, BlockPos pos) {
        factorLevel -= 100;
        markDirty();
    }
    
    public int getFactorLevel() {
        return factorLevel;
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
