package com.factorcraft.module.core.achievement;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * 成就数据类
 * 定义单个成就的所有属性
 */
public class Achievement {
    
    private final Identifier id;
    private final Text title;
    private final Text description;
    private final AchievementCategory category;
    private final Identifier icon;
    private final int requiredAmount;
    private final Text reward;
    private final boolean hidden;
    private final Identifier[] prerequisites;
    
    public Achievement(
        Identifier id,
        Text title,
        Text description,
        AchievementCategory category,
        Identifier icon,
        int requiredAmount,
        Text reward,
        boolean hidden,
        Identifier... prerequisites
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.icon = icon;
        this.requiredAmount = requiredAmount;
        this.reward = reward;
        this.hidden = hidden;
        this.prerequisites = prerequisites;
    }
    
    public Identifier getId() {
        return id;
    }
    
    public Text getTitle() {
        return title;
    }
    
    public Text getDescription() {
        return description;
    }
    
    public AchievementCategory getCategory() {
        return category;
    }
    
    public Identifier getIcon() {
        return icon;
    }
    
    public int getRequiredAmount() {
        return requiredAmount;
    }
    
    public Text getReward() {
        return reward;
    }
    
    public boolean isHidden() {
        return hidden;
    }
    
    public Identifier[] getPrerequisites() {
        return prerequisites;
    }
    
    /**
     * 检查成就是否已完成
     */
    public boolean isCompleted(int currentProgress) {
        return currentProgress >= requiredAmount;
    }
    
    /**
     * 写入 NBT
     */
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("id", id.toString());
        nbt.putString("category", category.name());
        nbt.putInt("required", requiredAmount);
        nbt.putBoolean("hidden", hidden);
        return nbt;
    }
    
    /**
     * 从 NBT 读取（仅用于数据同步，不读取文本）
     */
    public static Achievement fromNbt(NbtCompound nbt) {
        // 实际使用时需要从注册表获取完整 Achievement 对象
        return null;
    }
    
    /**
     * 写入网络包
     * 注意：实际网络同步只需要 ID 和进度，文本通过本地化键在客户端获取
     */
    public void toPacket(PacketByteBuf buf) {
        buf.writeIdentifier(id);
        buf.writeEnumConstant(category);
        buf.writeInt(requiredAmount);
        buf.writeBoolean(hidden);
        buf.writeInt(prerequisites.length);
        for (Identifier prereq : prerequisites) {
            buf.writeIdentifier(prereq);
        }
    }
    
    /**
     * 从网络包读取
     */
    public static Achievement fromPacket(PacketByteBuf buf) {
        Identifier id = buf.readIdentifier();
        AchievementCategory category = buf.readEnumConstant(AchievementCategory.class);
        int required = buf.readInt();
        boolean hidden = buf.readBoolean();
        int prereqCount = buf.readInt();
        Identifier[] prerequisites = new Identifier[prereqCount];
        for (int i = 0; i < prereqCount; i++) {
            prerequisites[i] = buf.readIdentifier();
        }
        // 文本和其他数据通过 ID 从注册表获取
        return null; // 实际使用时应从注册表获取完整对象
    }
}
