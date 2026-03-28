package com.factorcraft.module.social;

import com.factorcraft.module.social.exchange.ExchangeManager;
import com.factorcraft.module.social.leaderboard.LeaderboardManager;
import com.factorcraft.module.social.market.MarketManager;
import com.factorcraft.module.social.market.TradeListing;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 社交数据持久化存储
 */
public class SocialStorage extends PersistentState {
    
    private static final String DATA_ID = "factorcraft_social_data";
    
    private final MarketManager marketManager;
    private final ExchangeManager exchangeManager;
    private final LeaderboardManager leaderboardManager;
    
    public SocialStorage() {
        this.marketManager = MarketManager.getInstance();
        this.exchangeManager = ExchangeManager.getInstance();
        this.leaderboardManager = LeaderboardManager.getInstance();
    }
    
    public SocialStorage(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        this.marketManager = MarketManager.getInstance();
        this.exchangeManager = ExchangeManager.getInstance();
        this.leaderboardManager = LeaderboardManager.getInstance();
        readNbt(nbt, registryLookup);
    }
    
    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        // 保存市场数据
        NbtList listingsNbt = new NbtList();
        for (TradeListing listing : marketManager.getAllListings()) {
            listingsNbt.add(listing.toNbt(registries));
        }
        nbt.put("MarketListings", listingsNbt);
        
        // 保存交易所数据 - 简化处理，不保存临时订单
        nbt.putInt("ExchangeOrderCount", 0);
        
        // 保存排行榜数据
        nbt.put("LeaderboardData", leaderboardManager.toNbt());
        
        return nbt;
    }
    
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        // 清空现有数据
        marketManager.clear();
        
        // 加载市场数据
        if (nbt.contains("MarketListings", NbtElement.LIST_TYPE)) {
            NbtList listingsNbt = nbt.getList("MarketListings", NbtElement.COMPOUND_TYPE);
            int loaded = 0;
            for (int i = 0; i < listingsNbt.size(); i++) {
                NbtCompound listingNbt = listingsNbt.getCompound(i);
                try {
                    TradeListing listing = TradeListing.fromNbt(listingNbt, registries);
                    if (listing != null) {
                        marketManager.addListing(listing);
                        loaded++;
                    }
                } catch (Exception e) {
                    LoggerFactory.getLogger("FactorCraft:Social").warn("加载交易挂单失败：{}", listingNbt, e);
                }
            }
        }
        
        // 加载排行榜数据
        if (nbt.contains("LeaderboardData", NbtElement.COMPOUND_TYPE)) {
            leaderboardManager.fromNbt(nbt.getCompound("LeaderboardData"));
        }
    }
    
    /**
     * 从世界获取存储实例
     */
    public static SocialStorage get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
            new PersistentState.Type<>(
                SocialStorage::new,
                SocialStorage::new,
                null
            ),
            DATA_ID
        );
    }
}
