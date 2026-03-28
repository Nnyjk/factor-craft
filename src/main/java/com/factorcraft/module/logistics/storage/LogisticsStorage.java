package com.factorcraft.module.logistics.storage;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandlerType;
import com.factorcraft.module.logistics.request.RequestTerminalScreenHandler;
import net.minecraft.util.Identifier;

/**
 * 物流仓储方块注册表
 */
public class LogisticsStorage {
    
    public static Block STORAGE_UNIT;
    public static Block STORAGE_MONITOR;
    public static Block STORAGE_BUS;
    
    public static BlockEntityType<FactorStorageUnitBlockEntity> STORAGE_UNIT_ENTITY;
    public static BlockEntityType<StorageMonitorBlockEntity> STORAGE_MONITOR_ENTITY;
    public static BlockEntityType<StorageBusBlockEntity> STORAGE_BUS_ENTITY;
    
    public static ScreenHandlerType<StorageMonitorScreenHandler> STORAGE_MONITOR_HANDLER;
    public static ScreenHandlerType<RequestTerminalScreenHandler> REQUEST_TERMINAL_HANDLER;
    
    public static void register() {
        // 创建 RegistryKey
        RegistryKey<Block> storageUnitKey = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("factorcraft", "storage_unit"));
        RegistryKey<Block> storageMonitorKey = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("factorcraft", "storage_monitor"));
        RegistryKey<Block> storageBusKey = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("factorcraft", "storage_bus"));
        
        // 创建并注册方块（传入 RegistryKey）
        STORAGE_UNIT = Registry.register(Registries.BLOCK, storageUnitKey, new FactorStorageUnitBlock(FactorStorageUnitBlock.createSettings(storageUnitKey)));
        STORAGE_MONITOR = Registry.register(Registries.BLOCK, storageMonitorKey, new StorageMonitorBlock(StorageMonitorBlock.createSettings(storageMonitorKey)));
        STORAGE_BUS = Registry.register(Registries.BLOCK, storageBusKey, new StorageBusBlock(StorageBusBlock.createSettings(storageBusKey)));
        
        // 注册 BlockEntity
        STORAGE_UNIT_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of("factorcraft", "storage_unit"),
            FabricBlockEntityTypeBuilder.create(FactorStorageUnitBlockEntity::new, STORAGE_UNIT).build()
        );
        
        STORAGE_MONITOR_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of("factorcraft", "storage_monitor"),
            FabricBlockEntityTypeBuilder.create(StorageMonitorBlockEntity::new, STORAGE_MONITOR).build()
        );
        
        STORAGE_BUS_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of("factorcraft", "storage_bus"),
            FabricBlockEntityTypeBuilder.create(StorageBusBlockEntity::new, STORAGE_BUS).build()
        );
        
        // 注册屏幕处理器 (使用 ExtendedScreenHandlerType 传递 BlockPos)
        STORAGE_MONITOR_HANDLER = Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of("factorcraft", "storage_monitor"),
            new ExtendedScreenHandlerType<>(StorageMonitorScreenHandler::new, StorageMonitorScreenHandler.SyncData.PACKET_CODEC)
        );
        
        REQUEST_TERMINAL_HANDLER = Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of("factorcraft", "request_terminal"),
            new ExtendedScreenHandlerType<>(RequestTerminalScreenHandler::new, RequestTerminalScreenHandler.SyncData.PACKET_CODEC)
        );
    }
}
