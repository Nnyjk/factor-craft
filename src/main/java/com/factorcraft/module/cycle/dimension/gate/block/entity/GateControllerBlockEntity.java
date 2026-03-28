package com.factorcraft.module.cycle.dimension.block.entity.gate;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 传送控制器 BlockEntity
 * 管理维度传送门的配置和状态
 */
public class GateControllerBlockEntity extends BlockEntity {
    private int linkedDimensionId = 0;
    private BlockPos linkedPos = BlockPos.ORIGIN;
    private boolean isLinked = false;
    
    public GateControllerBlockEntity(BlockPos pos, BlockState state) {
        super(com.factorcraft.module.cycle.dimension.block.entity.DimensionBlockEntities.GATE_CONTROLLER, pos, state);
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.writeNbt(nbt, lookup);
        nbt.putInt("linkedDimensionId", linkedDimensionId);
        nbt.putLongArray("linkedPos", new long[]{linkedPos.asLong()});
        nbt.putBoolean("isLinked", isLinked);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.readNbt(nbt, lookup);
        linkedDimensionId = nbt.getInt("linkedDimensionId");
        long[] posArray = nbt.getLongArray("linkedPos");
        if (posArray.length > 0) {
            linkedPos = BlockPos.fromLong(posArray[0]);
        }
        isLinked = nbt.getBoolean("isLinked");
    }
    
    public static void tick(World world, BlockPos pos, BlockState state, GateControllerBlockEntity blockEntity) {
        if (world.isClient) {
            return;
        }
        
        // 控制器逻辑：检查连接状态，管理传送门能量
        if (blockEntity.isLinked) {
            // 已连接，可以激活传送门
        }
    }
    
    public void openUI(PlayerEntity player) {
        // TODO: 打开控制器 UI
        if (world != null && !world.isClient) {
            player.sendMessage(net.minecraft.text.Text.literal("控制器：维度 " + linkedDimensionId + " @ " + linkedPos.toShortString()), true);
        }
    }
    
    public void linkDimension(int dimensionId, BlockPos pos) {
        this.linkedDimensionId = dimensionId;
        this.linkedPos = pos;
        this.isLinked = true;
        markDirty();
    }
    
    public boolean isLinked() {
        return isLinked;
    }
    
    public int getLinkedDimensionId() {
        return linkedDimensionId;
    }
    
    public BlockPos getLinkedPos() {
        return linkedPos;
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
