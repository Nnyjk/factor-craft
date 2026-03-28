package com.factorcraft.module.machine.extractor;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.api.IEnergyReceiver;
import com.factorcraft.factor.Factor;
import com.factorcraft.factor.FactorRarity;
import com.factorcraft.factor.FactorType;
import com.factorcraft.module.loot.FactorItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Factor 提取器方块实体
 * 
 * 将物品/方块转化为 Factor
 * 支持能量消耗、多等级、自动化
 */
public class ExtractorBlockEntity extends BlockEntity 
    implements SidedInventory, NamedScreenHandlerFactory, IEnergyReceiver {
    
    // ========== 常量 ==========
    
    /** 输入槽位 */
    public static final int SLOT_INPUT = 0;
    /** 燃料槽位 */
    public static final int SLOT_FUEL = 1;
    /** 输出槽起始 */
    public static final int SLOT_OUTPUT_START = 2;
    /** 输出槽数量 */
    public static final int OUTPUT_SLOTS = 2;
    /** 槽位总数 */
    public static final int INVENTORY_SIZE = 4; // 1 input + 1 fuel + 2 output
    
    // ========== 配置（可通过 NBT 配置不同等级） ==========
    
    /** 基础能量消耗 */
    private int baseEnergyCost = 1000;
    /** 基础处理时间 */
    private int baseProcessingTime = 200;
    /** 能量效率倍率（等级越高越高效） */
    private double energyEfficiency = 1.0;
    /** 速度倍率（等级越高越快） */
    private double speedMultiplier = 1.0;
    /** 成功概率加成 */
    private double successBonus = 0.0;
    /** 提取器等级 */
    private int tier = 1;
    /** 最大能量存储 */
    private int maxEnergy = 100000;
    /** 当前能量 */
    private int currentEnergy = 0;
    
    // ========== 运行状态 ==========
    
    /** 物品库存 */
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
    /** 当前配方 */
    @Nullable
    private ExtractionRecipe currentRecipe;
    /** 处理进度 */
    private int processingProgress = 0;
    /** 是否正在处理 */
    private boolean isProcessing = false;
    
    // ========== 属性同步 ==========
    
    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> ExtractorBlockEntity.this.currentEnergy;
                case 1 -> ExtractorBlockEntity.this.maxEnergy;
                case 2 -> ExtractorBlockEntity.this.processingProgress;
                case 3 -> ExtractorBlockEntity.this.currentRecipe != null ? 
                    ExtractorBlockEntity.this.currentRecipe.getProcessingTime() : 0;
                case 4 -> ExtractorBlockEntity.this.tier;
                case 5 -> ExtractorBlockEntity.this.isProcessing ? 1 : 0;
                default -> 0;
            };
        }
        
        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> ExtractorBlockEntity.this.currentEnergy = value;
                case 2 -> ExtractorBlockEntity.this.processingProgress = value;
            }
        }
        
        @Override
        public int size() {
            return 6;
        }
    };
    
    // ========== 构造器 ==========
    
    public ExtractorBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, 1);
    }
    
    public ExtractorBlockEntity(BlockPos pos, BlockState state, int tier) {
        super(getBlockEntityType(tier), pos, state);
        this.tier = tier;
        configureByTier(tier);
    }
    
    /**
     * 根据等级获取对应的 BlockEntityType
     */
    private static BlockEntityType<?> getBlockEntityType(int tier) {
        return switch (tier) {
            case 2 -> ModBlockEntities.EXTRACTOR_T2;
            case 3 -> ModBlockEntities.EXTRACTOR_T3;
            default -> ModBlockEntities.EXTRACTOR_T1;
        };
    }
    
    /** 根据等级配置属性 */
    private void configureByTier(int tier) {
        this.tier = tier;
        this.baseEnergyCost = 1000 * tier;
        this.baseProcessingTime = Math.max(20, 200 - (tier - 1) * 30);
        this.energyEfficiency = 1.0 / (1.0 + (tier - 1) * 0.2);
        this.speedMultiplier = 1.0 + (tier - 1) * 0.25;
        this.successBonus = (tier - 1) * 0.05;
        this.maxEnergy = 100000 * tier;
    }
    
    // ========== Tick 逻辑 ==========
    
    /**
     * BlockEntity ticker 方法
     */
    public static <E extends BlockEntity> void tick(World world, BlockPos pos, BlockState state, E blockEntity) {
        if (blockEntity instanceof ExtractorBlockEntity extractor) {
            extractor.tick();
        }
    }
    
    public void tick() {
        if (world == null || world.isClient) return;
        
        // 检查是否有输出空间
        if (!canOutput()) {
            isProcessing = false;
            return;
        }
        
        // 检查是否需要开始新处理
        if (!isProcessing || currentRecipe == null) {
            tryStartProcessing();
        }
        
        // 处理中
        if (isProcessing && currentRecipe != null) {
            processTick();
        }
        
        markDirty();
    }
    
    /** 尝试开始处理 */
    private void tryStartProcessing() {
        ItemStack input = getStack(SLOT_INPUT);
        if (input.isEmpty()) {
            currentRecipe = null;
            isProcessing = false;
            return;
        }
        
        // 查找匹配的配方
        Optional<ExtractionRecipe> recipe = ExtractionRecipeRegistry.findFirstRecipe(input);
        if (recipe.isEmpty()) {
            currentRecipe = null;
            isProcessing = false;
            return;
        }
        
        currentRecipe = recipe.get();
        
        // 检查能量是否足够
        int energyNeeded = (int) (currentRecipe.getEnergyCost() * energyEfficiency);
        if (currentEnergy >= energyNeeded) {
            isProcessing = true;
            processingProgress = 0;
        }
    }
    
    /** 每tick处理 */
    private void processTick() {
        if (currentRecipe == null) {
            isProcessing = false;
            return;
        }
        
        int energyNeeded = (int) (currentRecipe.getEnergyCost() * energyEfficiency);
        int processingTime = (int) (currentRecipe.getProcessingTime() / speedMultiplier);
        
        // 检查能量是否足够继续
        if (currentEnergy < energyNeeded / processingTime) {
            // 能量不足，暂停处理
            return;
        }
        
        // 消耗能量
        int energyPerTick = energyNeeded / processingTime;
        currentEnergy -= energyPerTick;
        
        // 增加进度
        processingProgress++;
        
        // 检查是否完成
        if (processingProgress >= processingTime) {
            completeProcessing();
        }
    }
    
    /** 完成处理 */
    private void completeProcessing() {
        if (currentRecipe == null || world == null) return;
        
        // 消耗输入物品
        getStack(SLOT_INPUT).decrement(currentRecipe.getInput().getCount());
        
        // 计算成功概率
        double successChance = currentRecipe.getOutput().getBaseChance() + successBonus;
        Random random = world.getRandom();
        
        if (random.nextDouble() < successChance) {
            // 生成 Factor
            Factor factor = generateFactor(currentRecipe.getOutput(), random);
            if (factor != null) {
                // 创建 Factor 物品并放入输出槽
                ItemStack outputStack = createFactorItem(factor);
                insertOutput(outputStack);
            }
        }
        
        // 重置状态
        isProcessing = false;
        processingProgress = 0;
        currentRecipe = null;
    }
    
    /** 生成 Factor */
    private Factor generateFactor(ExtractionRecipe.ExtractionOutput output, Random random) {
        String typeName = output.getFactorType().toUpperCase();
        FactorType type;
        try {
            type = FactorType.valueOf(typeName);
        } catch (IllegalArgumentException e) {
            type = FactorType.ELEMENTAL; // 默认类型
        }
        
        // 随机等级
        int level = output.getMinLevel() + random.nextInt(output.getMaxLevel() - output.getMinLevel() + 1);
        
        // 随机威力
        double power = output.getMinPower() + random.nextDouble() * (output.getMaxPower() - output.getMinPower());
        
        // 根据等级决定稀有度
        FactorRarity rarity = determineRarity(level, random);
        
        // 创建唯一 ID
        Identifier factorId = Identifier.of("factorcraft", 
            String.format("extracted_%s_%d_%d", type.asString().toLowerCase(), level, System.nanoTime()));
        
        return new Factor.Builder(factorId, String.format("提取的 %s Factor", type.getDisplayName()))
            .type(type)
            .rarity(rarity)
            .level(level)
            .basePower(power)
            .build();
    }
    
    /** 根据等级决定稀有度 */
    private FactorRarity determineRarity(int level, Random random) {
        double roll = random.nextDouble();
        
        if (level >= 80) {
            return roll < 0.1 ? FactorRarity.LEGENDARY : 
                   roll < 0.4 ? FactorRarity.EPIC : FactorRarity.RARE;
        } else if (level >= 50) {
            return roll < 0.05 ? FactorRarity.LEGENDARY :
                   roll < 0.2 ? FactorRarity.EPIC :
                   roll < 0.5 ? FactorRarity.RARE : FactorRarity.UNCOMMON;
        } else if (level >= 20) {
            return roll < 0.1 ? FactorRarity.EPIC :
                   roll < 0.3 ? FactorRarity.RARE : FactorRarity.UNCOMMON;
        } else {
            return roll < 0.1 ? FactorRarity.RARE :
                   roll < 0.4 ? FactorRarity.UNCOMMON : FactorRarity.COMMON;
        }
    }
    
    /** 创建 Factor 物品 */
    private ItemStack createFactorItem(Factor factor) {
        return FactorItem.createFactorStack(factor);
    }
    
    /** 检查是否可以输出 */
    private boolean canOutput() {
        // 检查输出槽是否有空间
        for (int i = 0; i < OUTPUT_SLOTS; i++) {
            ItemStack output = getStack(SLOT_OUTPUT_START + i);
            if (output.isEmpty() || output.getCount() < output.getMaxCount()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 将物品插入输出槽
     */
    private void insertOutput(ItemStack stack) {
        for (int i = 0; i < OUTPUT_SLOTS; i++) {
            ItemStack output = getStack(SLOT_OUTPUT_START + i);
            if (output.isEmpty()) {
                setStack(SLOT_OUTPUT_START + i, stack.copy());
                return;
            } else if (ItemStack.areItemsAndComponentsEqual(output, stack)) {
                int space = output.getMaxCount() - output.getCount();
                int toInsert = Math.min(space, stack.getCount());
                output.increment(toInsert);
                stack.decrement(toInsert);
                if (stack.isEmpty()) return;
            }
        }
    }
    
    // ========== 能量接口 ==========
    
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int energyReceived = Math.min(maxEnergy - currentEnergy, maxReceive);
        if (!simulate) {
            currentEnergy += energyReceived;
            markDirty();
        }
        return energyReceived;
    }
    
    @Override
    public int getEnergyStored() {
        return currentEnergy;
    }
    
    @Override
    public int getMaxEnergyStored() {
        return maxEnergy;
    }
    
    @Override
    public boolean canReceive() {
        return currentEnergy < maxEnergy;
    }
    
    // ========== 库存接口 ==========
    
    @Override
    public int size() {
        return INVENTORY_SIZE;
    }
    
    @Override
    public boolean isEmpty() {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }
    
    @Override
    public ItemStack getStack(int slot) {
        if (slot >= 0 && slot < INVENTORY_SIZE) {
            return inventory.get(slot);
        }
        return ItemStack.EMPTY;
    }
    
    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(inventory, slot, amount);
    }
    
    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(inventory, slot);
    }
    
    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot >= 0 && slot < INVENTORY_SIZE) {
            inventory.set(slot, stack);
            if (stack.getCount() > getMaxCountPerStack()) {
                stack.setCount(getMaxCountPerStack());
            }
            markDirty();
        }
    }
    
    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return world != null && 
               player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64;
    }
    
    @Override
    public void clear() {
        inventory.clear();
    }
    
    // ========== SidedInventory ==========
    
    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.UP) {
            return new int[]{SLOT_INPUT}; // 从上面输入
        } else if (side == Direction.DOWN) {
            // 从下面输出
            int[] slots = new int[OUTPUT_SLOTS];
            for (int i = 0; i < OUTPUT_SLOTS; i++) {
                slots[i] = SLOT_OUTPUT_START + i;
            }
            return slots;
        }
        // 其他方向：输入和燃料
        return new int[]{SLOT_INPUT, SLOT_FUEL};
    }
    
    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot == SLOT_INPUT || slot == SLOT_FUEL;
    }
    
    @Override
    public boolean canExtract(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot >= SLOT_OUTPUT_START && slot < SLOT_OUTPUT_START + OUTPUT_SLOTS;
    }
    
    /**
     * 方块被破坏时调用
     */
    public void onBreak() {
        // 掉落物品
        if (world != null && !world.isClient) {
            for (int i = 0; i < INVENTORY_SIZE; i++) {
                ItemStack stack = getStack(i);
                if (!stack.isEmpty()) {
                    // 掉落物品到世界中
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                }
            }
        }
    }
    
    /**
     * 获取比较器输出
     */
    public int getComparatorOutput() {
        // 基于能量存储比例
        return (int) ((float) currentEnergy / maxEnergy * 15);
    }
    
    // ========== ScreenHandler ==========
    
    @Override
    public Text getDisplayName() {
        return Text.translatable("block.factorcraft.extractor_t" + tier);
    }
    
    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ExtractorScreenHandler(syncId, playerInventory, this, propertyDelegate, ScreenHandlerContext.create(world, pos));
    }
    
    public PropertyDelegate getPropertyDelegate() {
        return propertyDelegate;
    }
    
    /** 是否正在处理 */
    public boolean isProcessing() {
        return isProcessing;
    }
    
    // ========== NBT 序列化 ==========
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putInt("Energy", currentEnergy);
        nbt.putInt("Progress", processingProgress);
        nbt.putBoolean("Processing", isProcessing);
        nbt.putInt("Tier", tier);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);
        currentEnergy = nbt.getInt("Energy");
        processingProgress = nbt.getInt("Progress");
        isProcessing = nbt.getBoolean("Processing");
        tier = nbt.getInt("Tier");
        configureByTier(tier);
    }
}