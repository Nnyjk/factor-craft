package com.factorcraft.module.network;

import com.factorcraft.module.quest.instance.QuestInstance;
import com.factorcraft.module.quest.template.QuestTemplate;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 任务数据同步 - 服务器 -> 客户端
 */
public record QuestSyncPayload(
    List<QuestData> activeQuests,
    Set<Identifier> completedQuests
) implements CustomPayload {
    
    public static final CustomPayload.Id<QuestSyncPayload> ID = 
        new CustomPayload.Id<>(Identifier.of("factorcraft", "quest_sync"));
    
    public static final PacketCodec<RegistryByteBuf, QuestSyncPayload> CODEC = 
        PacketCodec.of(QuestSyncPayload::write, QuestSyncPayload::read);
    
    private void write(RegistryByteBuf buf) {
        // 写入活跃任务列表
        buf.writeInt(activeQuests.size());
        for (QuestData quest : activeQuests) {
            quest.write(buf);
        }
        
        // 写入已完成任务 ID 列表
        buf.writeInt(completedQuests.size());
        for (Identifier id : completedQuests) {
            buf.writeIdentifier(id);
        }
    }
    
    private static QuestSyncPayload read(RegistryByteBuf buf) {
        // 读取活跃任务列表
        int activeCount = buf.readInt();
        List<QuestData> activeQuests = new ArrayList<>(activeCount);
        for (int i = 0; i < activeCount; i++) {
            activeQuests.add(QuestData.read(buf));
        }
        
        // 读取已完成任务 ID 列表
        int completedCount = buf.readInt();
        Set<Identifier> completedQuests = new HashSet<>(completedCount);
        for (int i = 0; i < completedCount; i++) {
            completedQuests.add(buf.readIdentifier());
        }
        
        return new QuestSyncPayload(activeQuests, completedQuests);
    }
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    
    /**
     * 发送任务同步到客户端
     */
    public static void sendToPlayer(ServerPlayerEntity player, 
                                     List<QuestInstance> activeQuests,
                                     Set<Identifier> completedQuests) {
        if (ServerPlayNetworking.canSend(player, ID)) {
            List<QuestData> questDataList = new ArrayList<>();
            for (QuestInstance instance : activeQuests) {
                questDataList.add(QuestData.from(instance));
            }
            
            ServerPlayNetworking.send(player, new QuestSyncPayload(questDataList, completedQuests));
        }
    }
    
    /**
     * 任务数据（简化版，用于网络传输）
     */
    public record QuestData(
        Identifier questId,
        String title,
        String description,
        float progress,
        int conditionCount,
        int completedConditions
    ) {
        public static QuestData from(QuestInstance instance) {
            QuestTemplate template = instance.getTemplate();
            return new QuestData(
                template.getId(),
                template.getTitle(),
                template.getDescription(),
                instance.getOverallProgress(),
                template.getConditions().size(),
                (int) (template.getConditions().size() * instance.getOverallProgress())
            );
        }
        
        public void write(RegistryByteBuf buf) {
            buf.writeIdentifier(questId);
            buf.writeString(title);
            buf.writeString(description);
            buf.writeFloat(progress);
            buf.writeInt(conditionCount);
            buf.writeInt(completedConditions);
        }
        
        public static QuestData read(RegistryByteBuf buf) {
            Identifier questId = buf.readIdentifier();
            String title = buf.readString();
            String description = buf.readString();
            float progress = buf.readFloat();
            int conditionCount = buf.readInt();
            int completedConditions = buf.readInt();
            return new QuestData(questId, title, description, progress, conditionCount, completedConditions);
        }
    }
}
