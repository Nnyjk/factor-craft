package com.factorcraft.module.cycle.dimension.block.entity.gate;

import com.factorcraft.module.cycle.dimension.gate.block.DimensionalGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 维度之门 BlockEntity
 * 处理跨维度传送逻辑
 */
public class DimensionalGateBlockEntity extends BlockEntity {
    private int targetDimensionId = 0; // 0=主世界，-1=下界，1=末地
    private BlockPos targetPos = BlockPos.ORIGIN;
    private int energyRequired = 1000;
    private int currentEnergy = 0;
    
    public DimensionalGateBlockEntity(BlockPos pos, BlockState state) {
        super(com.factorcraft.module.cycle.dimension.block.entity.DimensionBlockEntities.DIMENSIONAL_GATE, pos, state);
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.writeNbt(nbt, lookup);
        nbt.putInt("targetDimensionId", targetDimensionId);
        nbt.putLongArray("targetPos", new long[]{targetPos.asLong()});
        nbt.putInt("energyRequired", energyRequired);
        nbt.putInt("currentEnergy", currentEnergy);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.readNbt(nbt, lookup);
        targetDimensionId = nbt.getInt("targetDimensionId");
        long[] posArray = nbt.getLongArray("targetPos");
        if (posArray.length > 0) {
            targetPos = BlockPos.fromLong(posArray[0]);
        }
        energyRequired = nbt.getInt("energyRequired");
        currentEnergy = nbt.getInt("currentEnergy");
    }
    
    public static void tick(World world, BlockPos pos, BlockState state, DimensionalGateBlockEntity blockEntity) {
        if (world.isClient) {
            return;
        }
        
        // 检查是否有足够的能量进行传送
        if (blockEntity.currentEnergy >= blockEntity.energyRequired) {
            blockEntity.activatePortal(world, pos, state);
        }
    }
    
    private void activatePortal(World world, BlockPos pos, BlockState state) {
        if (world instanceof ServerWorld serverWorld) {
            world.setBlockState(pos, state.with(DimensionalGateBlock.ACTIVATED, true));
        }
    }
    
    public void tryTeleport(PlayerEntity player) {
        if (world == null || world.isClient) {
            return;
        }
        
        if (currentEnergy < energyRequired) {
            player.sendMessage(Text.literal("传送门能量不足！需要 " + energyRequired + " Factor"), true);
            return;
        }
        
        if (player instanceof ServerPlayerEntity serverPlayer) {
            ServerWorld targetWorld = getTargetWorld((ServerWorld) world);
            if (targetWorld != null) {
                // 传送到目标维度 - 使用正确的 API 签名
                serverPlayer.teleport(
                    targetWorld,
                    targetPos.getX() + 0.5,
                    targetPos.getY() + 1.0,
                    targetPos.getZ() + 0.5,
                    java.util.Set.of(),
                    player.getYaw(),
                    player.getPitch(),
                    false
                );
                currentEnergy -= energyRequired;
                markDirty();
            }
        }
    }
    
    private ServerWorld getTargetWorld(ServerWorld currentWorld) {
        if (targetDimensionId == -1) {
            return currentWorld.getServer().getWorld(net.minecraft.world.World.NETHER);
        } else if (targetDimensionId == 1) {
            return currentWorld.getServer().getWorld(net.minecraft.world.World.END);
        } else {
            return currentWorld.getServer().getWorld(net.minecraft.world.World.OVERWORLD);
        }
    }
    
    public void setTarget(int dimensionId, BlockPos pos) {
        this.targetDimensionId = dimensionId;
        this.targetPos = pos;
        markDirty();
    }
    
    public void addEnergy(int amount) {
        this.currentEnergy = Math.min(currentEnergy + amount, energyRequired);
        markDirty();
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
