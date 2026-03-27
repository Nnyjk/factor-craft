package com.factorcraft.module.event;

import com.factorcraft.module.factor.TideStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 随机事件管理器
 * 
 * 负责管理随机事件的触发、执行和结束
 */
public class RandomEventManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(RandomEventManager.class);
    private static RandomEventManager instance;
    
    private final Map<Identifier, List<IEvent>> activeEvents = new HashMap<>();
    private final Random random = Random.create();
    private int tickCounter = 0;
    
    // 事件触发间隔（ticks）- 每 5 分钟检查一次
    private static final int EVENT_CHECK_INTERVAL = 6000;
    
    // 基础触发概率
    private static final double BASE_TRIGGER_CHANCE = 0.3;
    
    private RandomEventManager() {
    }
    
    public static RandomEventManager getInstance() {
        if (instance == null) {
            instance = new RandomEventManager();
        }
        return instance;
    }
    
    /**
     * 每 tick 更新
     */
    public void tick(MinecraftServer server) {
        tickCounter++;
        
        if (tickCounter % EVENT_CHECK_INTERVAL == 0) {
            checkAndTriggerEvents(server);
        }
        
        updateActiveEvents(server);
    }
    
    /**
     * 检查并触发事件
     */
    private void checkAndTriggerEvents(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            // 跳过非主世界
            if (!world.getRegistryKey().getValue().equals(Identifier.of("minecraft", "overworld"))) {
                continue;
            }
            
            // 根据基础概率触发事件
            if (random.nextDouble() < BASE_TRIGGER_CHANCE) {
                triggerEvent(server, world, selectEventType());
            }
        }
    }
    
    /**
     * 手动触发事件（供命令使用）
     */
    public void triggerEvent(MinecraftServer server, ServerWorld world, EventType eventType) {
        LOGGER.info("手动触发事件：{}", eventType.getDisplayName());
        
        // 创建并启动事件
        IEvent event = createEvent(eventType, world.getRegistryKey().getValue());
        if (event != null) {
            startEvent(server, world, event);
        }
    }
    
    /**
     * 根据权重选择事件类型
     */
    private EventType selectEventType() {
        List<EventType> types = new ArrayList<>();
        double totalWeight = 0;
        
        for (EventType type : EventType.values()) {
            types.add(type);
            totalWeight += type.getBaseWeight();
        }
        
        double roll = random.nextDouble() * totalWeight;
        double current = 0;
        
        for (EventType type : types) {
            current += type.getBaseWeight();
            if (roll <= current) {
                return type;
            }
        }
        
        return types.get(0);
    }
    
    /**
     * 创建事件实例
     */
    private IEvent createEvent(EventType type, Identifier worldKey) {
        int duration = 300 * 20; // 默认 5 分钟（300 秒）
        
        return switch (type) {
            case FACTOR_STORM -> new FactorStormEvent(worldKey, duration);
            case MERCHANT_VISIT -> new MerchantVisitEvent(worldKey, duration);
            case ORE_BURST -> new OreBurstEvent(worldKey, duration);
            case MACHINE_OVERLOAD -> new MachineOverloadEvent(worldKey, duration);
            case CREATURE_FRENZY -> new CreatureFrenzyEvent(worldKey, duration);
            case ENERGY_SURGE -> new EnergySurgeEvent(worldKey, duration);
            case SPACE_TIME_WARP -> new SpaceTimeWarpEvent(worldKey, duration);
        };
    }
    
    /**
     * 启动事件
     */
    private void startEvent(MinecraftServer server, ServerWorld world, IEvent event) {
        activeEvents.computeIfAbsent(world.getRegistryKey().getValue(), k -> new ArrayList<>()).add(event);
        event.onStart(server, world);
        LOGGER.info("事件启动：{} 在世界 {}", event.getType().getDisplayName(), world.getRegistryKey().getValue());
    }
    
    /**
     * 更新所有活跃事件
     */
    private void updateActiveEvents(MinecraftServer server) {
        List<Identifier> worldsToRemove = new ArrayList<>();
        
        for (Map.Entry<Identifier, List<IEvent>> entry : activeEvents.entrySet()) {
            Identifier worldKey = entry.getKey();
            List<IEvent> events = entry.getValue();
            ServerWorld world = getWorldByKey(server, worldKey);
            
            if (world == null) {
                worldsToRemove.add(worldKey);
                continue;
            }
            
            List<IEvent> finishedEvents = new ArrayList<>();
            
            for (IEvent event : events) {
                event.onTick(server, world, event.getDuration());
                
                if (event.isFinished()) {
                    finishedEvents.add(event);
                }
            }
            
            // 移除已完成的事件
            for (IEvent event : finishedEvents) {
                events.remove(event);
                event.onEnd(server, world);
                LOGGER.info("事件结束：{}", event.getType().getDisplayName());
            }
            
            if (events.isEmpty()) {
                worldsToRemove.add(worldKey);
            }
        }
        
        // 清理空世界
        for (Identifier worldKey : worldsToRemove) {
            activeEvents.remove(worldKey);
        }
    }
    
    /**
     * 根据 RegistryKey 获取世界
     */
    private ServerWorld getWorldByKey(MinecraftServer server, Identifier worldKey) {
        for (ServerWorld world : server.getWorlds()) {
            if (world.getRegistryKey().getValue().equals(worldKey)) {
                return world;
            }
        }
        return null;
    }
    
    /**
     * 获取世界的所有活跃事件
     */
    public List<IEvent> getActiveEvents(Identifier worldKey) {
        return activeEvents.getOrDefault(worldKey, new ArrayList<>());
    }
}
