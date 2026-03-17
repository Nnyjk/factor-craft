package com.factorcraft.module.network.item;

import com.factorcraft.component.FactorCraftDataComponents;
import com.factorcraft.component.type.ScanHistory;
import com.factorcraft.module.factor.FactorService;
import com.factorcraft.module.factor.TideStatus;
import com.factorcraft.module.network.FactorNetworkManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;

/**
 * Factor 网络扫描仪
 * 
 * 4 个等级：
 * - BASIC: 基础扫描（无限使用）
 * - ADVANCED: 网络拓扑（消耗少量 Factor）
 * - PROFESSIONAL: 深度分析（消耗中量 Factor）
 * - MASTER: 全功能 + 历史记录（消耗大量 Factor）
 */
public class FactorScannerItem extends Item {
    
    public final ScannerTier tier;
    
    public FactorScannerItem(ScannerTier tier, Settings settings) {
        super(settings.maxDamage(tier.maxDurability));
        this.tier = tier;
    }
    
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getStack();
        
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        
        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.PASS;
        }
        
        if (stack.getDamage() >= stack.getMaxDamage() - 1) {
            player.sendMessage(Text.literal("扫描仪已损坏！").formatted(Formatting.RED), true);
            return ActionResult.FAIL;
        }
        
        BlockPos pos = context.getBlockPos();
        double concentration = getConcentration(serverWorld, pos);
        TideStatus status = TideStatus.fromConcentration(concentration);
        
        NetworkInfo networkInfo = null;
        if (tier != ScannerTier.BASIC) {
            networkInfo = getNetworkInfo(serverWorld, pos);
        }
        
        sendScanResult(player, stack, concentration, status, networkInfo);
        
        if (tier != ScannerTier.BASIC) {
            stack.damage(tier.durabilityCost, player);
        }
        
        return ActionResult.SUCCESS;
    }
    
    /**
     * 获取 Factor 浓度
     * 
     * @param world 世界
     * @param pos 位置
     * @return Factor 浓度 (0.0-1.0)
     */
    private double getConcentration(ServerWorld world, BlockPos pos) {
        // 使用 FactorService 获取世界 Factor 浓度
        // 注意：当前实现基于世界维度，未来可扩展为区块级浓度
        FactorService service = FactorService.getInstance();
        double concentration = service.getFactor(world);
        
        // 确保浓度在有效范围内 (0.0-1.0)
        return Math.max(0.0, Math.min(1.0, concentration));
    }
    
    /**
     * 获取网络信息
     */
    private NetworkInfo getNetworkInfo(ServerWorld world, BlockPos pos) {
        FactorNetworkManager networkManager = FactorNetworkManager.getInstance();
        return new NetworkInfo(0, 0.0, 0.0, 0.0);
    }
    
    /**
     * 发送扫描结果
     */
    private void sendScanResult(PlayerEntity player, ItemStack stack, 
                                double concentration, TideStatus status, 
                                NetworkInfo networkInfo) {
        double percentage = concentration * 100.0;
        
        player.sendMessage(Text.literal("=== Factor 扫描结果 ===").formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("浓度：" + String.format("%.1f", percentage) + "%").formatted(status.getColor()), false);
        player.sendMessage(Text.literal("状态：" + status.getName()).formatted(status.getColor()), false);
        
        String trend = getTrend(stack, concentration);
        player.sendMessage(Text.literal("趋势：" + trend).formatted(Formatting.GRAY), false);
        
        if (networkInfo != null) {
            player.sendMessage(Text.literal(""), false);
            player.sendMessage(Text.literal("网络信息:").formatted(Formatting.AQUA), false);
            player.sendMessage(Text.literal("  传递器：" + networkInfo.transmitterCount).formatted(Formatting.GRAY), false);
            player.sendMessage(Text.literal("  总 Factor：" + String.format("%.1f", networkInfo.totalFactor)).formatted(Formatting.GRAY), false);
            player.sendMessage(Text.literal("  输入：" + String.format("%.2f", networkInfo.inputRate) + "/s").formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("  输出：" + String.format("%.2f", networkInfo.outputRate) + "/s").formatted(Formatting.RED), false);
        }
        
        if (tier == ScannerTier.PROFESSIONAL || tier == ScannerTier.MASTER) {
            performDeepAnalysis(player, concentration);
        }
        
        updateNbtCache(stack, concentration);
    }
    
    /**
     * 获取趋势
     * 
     * 使用 Data Component 系统读取扫描历史，计算趋势
     * 
     * @param stack 扫描仪物品
     * @param currentConcentration 当前浓度
     * @return 趋势描述
     */
    private String getTrend(ItemStack stack, double currentConcentration) {
        ScanHistory history = stack.get(FactorCraftDataComponents.SCAN_HISTORY);
        if (history == null) {
            history = ScanHistory.empty();
        }
        return history.getTrend(currentConcentration);
    }
    
    /**
     * 更新扫描历史 Data Component
     * 
     * @param stack 扫描仪物品
     * @param concentration 当前浓度
     */
    private void updateNbtCache(ItemStack stack, double concentration) {
        ScanHistory history = stack.get(FactorCraftDataComponents.SCAN_HISTORY);
        if (history == null) {
            history = ScanHistory.empty();
        }
        ScanHistory updatedHistory = history.addEntry(concentration);
        stack.set(FactorCraftDataComponents.SCAN_HISTORY, updatedHistory);
    }
    
    /**
     * 执行深度分析
     */
    private void performDeepAnalysis(PlayerEntity player, double centerConcentration) {
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("深度分析:").formatted(Formatting.LIGHT_PURPLE), false);
        
        String recommendation = getRecommendation(centerConcentration);
        player.sendMessage(Text.literal("  建议：" + recommendation).formatted(Formatting.GRAY), false);
        
        if (tier == ScannerTier.MASTER) {
            ItemStack scannerStack = player.getMainHandStack();
            ScanHistory history = scannerStack.get(FactorCraftDataComponents.SCAN_HISTORY);
            if (history != null && history.getScanCount() > 0) {
                player.sendMessage(Text.literal("  历史记录：" + history.getScanCount() + " 次扫描").formatted(Formatting.GRAY), false);
            } else {
                player.sendMessage(Text.literal("  历史记录：暂无数据").formatted(Formatting.GRAY), false);
            }
        }
    }
    
    /**
     * 获取建议
     */
    private String getRecommendation(double concentration) {
        if (concentration < 0.3) {
            return "浓度过低，建议寻找其他区域";
        } else if (concentration < 0.5) {
            return "浓度适中，适合基础机器";
        } else if (concentration < 0.7) {
            return "浓度良好，适合高级机器";
        } else if (concentration < 0.9) {
            return "浓度优秀，适合工业级设备";
        } else {
            return "浓度极高，注意过载风险！";
        }
    }
    
    /**
     * 网络信息
     */
    public record NetworkInfo(
        int transmitterCount,
        double totalFactor,
        double inputRate,
        double outputRate
    ) {}
    
    /**
     * 扫描仪等级
     */
    public enum ScannerTier {
        BASIC("基础扫描仪", Formatting.WHITE, 0, 0),
        ADVANCED("进阶扫描仪", Formatting.AQUA, 500, 1),
        PROFESSIONAL("专业扫描仪", Formatting.GREEN, 1000, 3),
        MASTER("大师扫描仪", Formatting.GOLD, 2000, 5);
        
        public final String displayName;
        public final Formatting color;
        public final int maxDurability;
        public final int durabilityCost;
        
        ScannerTier(String displayName, Formatting color, int maxDurability, int durabilityCost) {
            this.displayName = displayName;
            this.color = color;
            this.maxDurability = maxDurability;
            this.durabilityCost = durabilityCost;
        }
    }
}
