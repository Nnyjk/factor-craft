package com.factorcraft.module.cycle.automation.block.entity.controller;

import com.factorcraft.module.cycle.automation.block.controller.SystemControllerBlock;
import com.factorcraft.module.cycle.automation.block.entity.AutomationBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统控制器 BlockEntity - 中央管理自动化系统
 */
public class SystemControllerBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    private final DefaultedList<ItemStack> inventory;
    private static final int INVENTORY_SIZE = 27; // 3x9 物品栏
    private int controlTimer;
    private static final int CONTROL_INTERVAL = 20; // 1 秒
    private List<BlockPos> connectedMachines;
    
    public SystemControllerBlockEntity(BlockPos pos, BlockState state) {
        super(AutomationBlockEntities.SYSTEM_CONTROLLER, pos, state);
        this.inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
        this.connectedMachines = new ArrayList<>();
        this.controlTimer = 0;
    }
    
    @Override
    public Text getDisplayName() {
        return Text.translatable("block.factorcraft.system_controller");
    }
    
    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return null; // 简化
    }
    
    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }
    
    /**
     * 每 tick 调用
     */
    public static <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState state, T blockEntity) {
        if (!(blockEntity instanceof SystemControllerBlockEntity controller)) {
            return;
        }
        
        if (world.isClient) {
            return;
        }
        
        controller.controlTimer++;
        
        if (controller.controlTimer >= CONTROL_INTERVAL) {
            controller.controlTimer = 0;
            controller.scanMachines(world, pos);
            controller.updateMachineStates(world);
        }
    }
    
    /**
     * 扫描周围的机器
     */
    private void scanMachines(World world, BlockPos pos) {
        connectedMachines.clear();
        
        // 扫描周围 32 格范围内的机器
        for (int x = -32; x <= 32; x += 16) {
            for (int y = -8; y <= 8; y += 8) {
                for (int z = -32; z <= 32; z += 16) {
                    BlockPos machinePos = pos.add(x, y, z);
                    BlockEntity machine = world.getBlockEntity(machinePos);
                    
                    // 检查是否为自动化机器
                    if (isAutomationMachine(machine)) {
                        connectedMachines.add(machinePos);
                    }
                }
            }
        }
    }
    
    private boolean isAutomationMachine(BlockEntity machine) {
        if (machine == null) return false;
        // 简化：检查是否为自动化模块的 BlockEntity
        return machine.getType() == AutomationBlockEntities.AUTO_CRAFTER ||
               machine.getType() == AutomationBlockEntities.AUTO_HARVESTER ||
               machine.getType() == AutomationBlockEntities.AUTO_DISTRIBUTOR;
    }
    
    /**
     * 更新连接的机器状态
     */
    private void updateMachineStates(World world) {
        // 简化：仅标记活跃状态
        boolean hasWork = !connectedMachines.isEmpty();
        world.setBlockState(getPos(), getCachedState().with(SystemControllerBlock.ACTIVE, hasWork));
    }
    
    /**
     * 获取连接的机器数量
     */
    public int getConnectedMachineCount() {
        return connectedMachines.size();
    }
    
    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.readNbt(nbt, lookup);
        Inventories.readNbt(nbt, inventory, lookup);
        controlTimer = nbt.getInt("ControlTimer");
    }
    
    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.writeNbt(nbt, lookup);
        Inventories.writeNbt(nbt, inventory, lookup);
        nbt.putInt("ControlTimer", controlTimer);
    }
}
