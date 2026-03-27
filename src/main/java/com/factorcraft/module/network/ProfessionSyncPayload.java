package com.factorcraft.module.network;

import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 职业状态同步 Payload
 * 
 * S2C: 服务端 -> 客户端
 * 同步玩家职业状态、等级、经验、技能冷却等
 */
public record ProfessionSyncPayload(
    /** 当前职业类型 */
    ProfessionType professionType,
    /** 职业等级 */
    int level,
    /** 职业经验 */
    int experience,
    /** 天赋点数 */
    int talentPoints,
    /** 已激活的天赋节点 */
    Set<String> activeTalents,
    /** 技能冷却时间映射 (技能ID -> 剩余冷却秒数) */
    Map<String, Long> skillCooldowns,
    /** 是否是完整同步（true: 全量同步，false: 增量更新） */
    boolean fullSync
) implements CustomPayload {
    
    public static final CustomPayload.Id<ProfessionSyncPayload> ID = 
        new CustomPayload.Id<>(Identifier.of("factorcraft", "profession_sync"));
    
    public static final PacketCodec<RegistryByteBuf, ProfessionSyncPayload> CODEC = 
        PacketCodec.of(ProfessionSyncPayload::write, ProfessionSyncPayload::read);
    
    /**
     * 序列化写入
     */
    public void write(RegistryByteBuf buf) {
        // 职业类型（null 写为 -1，否则写 ordinal）
        if (professionType == null) {
            buf.writeVarInt(-1);
        } else {
            buf.writeVarInt(professionType.ordinal());
        }
        
        // 基础数据
        buf.writeVarInt(level);
        buf.writeVarInt(experience);
        buf.writeVarInt(talentPoints);
        
        // 激活的天赋
        buf.writeVarInt(activeTalents.size());
        for (String talent : activeTalents) {
            buf.writeString(talent);
        }
        
        // 技能冷却
        buf.writeVarInt(skillCooldowns.size());
        for (Map.Entry<String, Long> entry : skillCooldowns.entrySet()) {
            buf.writeString(entry.getKey());
            buf.writeLong(entry.getValue());
        }
        
        // 是否全量同步
        buf.writeBoolean(fullSync);
    }
    
    /**
     * 反序列化读取
     */
    public static ProfessionSyncPayload read(RegistryByteBuf buf) {
        // 职业类型（-1 表示 null）
        int typeOrdinal = buf.readVarInt();
        ProfessionType type = typeOrdinal >= 0 ? ProfessionType.values()[typeOrdinal] : null;
        
        // 基础数据
        int level = buf.readVarInt();
        int experience = buf.readVarInt();
        int talentPoints = buf.readVarInt();
        
        // 激活的天赋
        int talentCount = buf.readVarInt();
        Set<String> talents = new java.util.HashSet<>();
        for (int i = 0; i < talentCount; i++) {
            talents.add(buf.readString());
        }
        
        // 技能冷却
        int cooldownCount = buf.readVarInt();
        Map<String, Long> cooldowns = new HashMap<>();
        for (int i = 0; i < cooldownCount; i++) {
            String skillId = buf.readString();
            long remainingSeconds = buf.readLong();
            cooldowns.put(skillId, remainingSeconds);
        }
        
        // 是否全量同步
        boolean fullSync = buf.readBoolean();
        
        return new ProfessionSyncPayload(
            type,
            level,
            experience,
            talentPoints,
            talents,
            cooldowns,
            fullSync
        );
    }
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}