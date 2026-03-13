# Factor-Craft 内容命名设计

> 创建日期: 2026-03-12
> 状态: 设计中

## 设计风格

- **主题**: 远古 + 废土风格
- **世界观**: 废弃文明的遗迹，玩家探索并重建远古科技

---

## 一、四大核心结构命名 (T1-T5)

### 结构类型定义

| 类型 | 功能 | 描述 |
|------|------|------|
| **提取** | 从环境中提取 Factor | 类似发电机，从世界缝隙中抽取能量 |
| **消耗** | 消耗物品获得 Factor | 投入物品燃烧/分解，转化为 Factor |
| **合成** | 用 Factor 合成物品 | 以 Factor 重塑物质 |
| **培育** | 给物品注入特性 | 将特性编织进物品 |

### 结构命名表

| Tier | 提取 | 消耗 | 合成 | 培育 |
|------|------|------|------|------|
| T1 粗坯 | 星辰收集器 | 灵魂燃烧器 | 远古合成阵 | 命运织机 |
| T2 工业 | 星辰阵列 | 灵魂熔炉 | 远古锻造台 | 灵魂编织器 |
| T3 维度 | 星云汲取器 | 深渊吞噬者 | 命运铸造炉 | 命运祭坛 |
| T4 远古 | 宇宙共鸣器 | 混沌裂隙 | 创世熔炉 | 命运圣所 |
| T5 仲裁 | 虚空漩涡 | 永恒炉心 | 本源祭坛 | 轮回之门 |

---

## 二、材料系统 (T1-T5)

### 材料命名

| Tier | 材料名 | 命名理念 |
|------|--------|----------|
| T1 粗坯 | 尘铜锭 (Dust Copper Ingot) | 尘埃中提炼的粗坯金属，废弃文明的残留 |
| T2 工业 | 暗影钢锭 (Shadow Steel Ingot) | 注入暗影能量的工业级金属 |
| T3 维度 | 星尘锭 (Stardust Ingot) | 跨维度的星辰之力凝结 |
| T4 远古 | 远古合金 (Ancient Alloy) | 远古文明的科技结晶 |
| T5 仲裁 | 虚空结晶 (Void Crystal) | 纯粹的虚空能量，接近本源 |

---

## 三、特性方块系统

### 定义

- **特性方块** = 使用 T1-T5 材料制作的建筑方块
- **特性** = 可培养的属性（与培育系统统一）
- **蓝图插槽** = 多方块结构中的特殊效果位置，放入带特性的方块触发效果

### 建筑方块表

| Tier | 建筑方块名 | 可携带特性槽位 |
|------|-----------|----------------|
| T1 | 尘铜方块 | 1 槽 |
| T2 | 暗影钢方块 | 1 槽 |
| T3 | 星尘方块 | 2 槽 |
| T4 | 远古合金方块 | 2 槽 |
| T5 | 虚空结晶方块 | 3 槽 |

---

## 四、扩展内容清单

每级材料需要扩展以下内容：

- 基础材料（锭、粒、块、粉）
- 工具（镐、斧、铲、锄）
- 武器（剑、锤、弓/弩）
- 护甲（头盔、胸甲、护腿、靴子）
- 机器核心（四大类型）
- 建筑方块
- 装饰方块（台阶、楼梯、墙、栏杆等）

---

## 五、贴图提示词设计

### 视觉风格定义

| Tier | 核心色调 | 质感特征 | 关键词 |
|------|----------|----------|--------|
| T1 尘铜 | 暗橙褐色 | 粗糙、锈迹、颗粒感 | rusty, dusty, weathered, corroded, primitive |
| T2 暗影钢 | 深灰黑色 | 金属光泽、暗影纹理 | dark steel, shadowy, metallic, industrial, sleek |
| T3 星尘 | 紫蓝渐变 | 星光点缀、能量纹路 | stardust, cosmic, glowing, ethereal, mystical |
| T4 远古合金 | 金黑色调 | 符文纹路、庄严感 | ancient, golden runes, ornate, majestic, legendary |
| T5 虚空结晶 | 紫黑透明 | 能量流动、虚空感 | void, crystalline, ethereal, transcendent, omnipotent |

---

### T1 尘铜 (Dust Copper) - 粗坯级

**视觉风格:** 废弃文明的残留，锈迹斑斑，粗糙但实用

```
【基础材料】
- 尘铜锭: "pixel art item icon, copper ingot, rusty orange-brown color, weathered texture, dusty surface, small dark spots, 16x16, Minecraft style, top-down view, isolated on transparent background"

- 尘铜粒: "pixel art item icon, copper nugget, tiny rusty orange-brown piece, weathered texture, 16x16, Minecraft style, isolated on transparent background"

- 尘铜块: "pixel art block texture, copper block, rusty orange-brown, weathered surface with patches, slightly uneven, 16x16, Minecraft block style, seamless tileable"

- 尘铜粉: "pixel art item icon, copper dust pile, fine orange-brown powder, grainy texture, 16x16, Minecraft style, isolated on transparent background"

【工具】
- 尘铜镐: "pixel art item icon, pickaxe, rusty copper head, worn wooden handle, primitive and weathered look, 16x16, Minecraft style, diagonal angle, isolated"

- 尘铜斧: "pixel art item icon, axe, rusty copper blade, worn wooden handle, primitive and weathered look, 16x16, Minecraft style, isolated"

- 尘铜铲: "pixel art item icon, shovel, rusty copper blade, worn wooden handle, primitive look, 16x16, Minecraft style, isolated"

- 尘铜锄: "pixel art item icon, hoe, rusty copper head, worn wooden handle, primitive look, 16x16, Minecraft style, isolated"

【武器】
- 尘铜剑: "pixel art item icon, sword, rusty copper blade, worn edges, primitive leather-wrapped handle, 16x16, Minecraft style, diagonal, isolated"

- 尘铜锤: "pixel art item icon, hammer, heavy rusty copper head, thick wooden handle, primitive brutal look, 16x16, Minecraft style, isolated"

【护甲】
- 尘铜头盔: "pixel art item icon, helmet, rusty copper, weathered surface, simple design, primitive armor, 16x16, Minecraft style, isolated"

- 尘铜胸甲: "pixel art item icon, chestplate, rusty copper plates, weathered surface, leather straps, primitive armor, 16x16, Minecraft style, isolated"

- 尘铜护腿: "pixel art item icon, leggings, rusty copper segments, weathered surface, primitive armor, 16x16, Minecraft style, isolated"

- 尘铜靴子: "pixel art item icon, boots, rusty copper, weathered surface, simple design, 16x16, Minecraft style, isolated"

【建筑方块】
- 尘铜方块: "pixel art block texture, solid copper block, rusty orange-brown, weathered patches, subtle cracks, 16x16, Minecraft block style, seamless tileable"

【装饰方块】
- 尘铜台阶: "pixel art block texture, copper slab, rusty orange-brown, weathered surface, half-height block, 16x16, Minecraft style, seamless"

- 尘铜楼梯: "pixel art block texture, copper stairs, rusty orange-brown, weathered surface, L-shaped step, 16x16, Minecraft style, seamless"

- 尘铜墙: "pixel art block texture, copper wall, rusty orange-brown, weathered stone-like texture, tall and narrow, 16x16, Minecraft style, seamless"

【机器核心 - 星辰收集器 T1】
- 星辰收集器核心: "pixel art block texture, machine core, rusty copper casing, faint starlight glow inside, primitive tech, 16x16, Minecraft block style, front face"
```

---

### T2 暗影钢 (Shadow Steel) - 工业级

**视觉风格:** 深色金属，暗影能量渗透，工业感但带有神秘气息

```
【基础材料】
- 暗影钢锭: "pixel art item icon, dark steel ingot, deep gray-black metallic, shadowy aura, sleek surface, subtle purple undertone, 16x16, Minecraft style, isolated"

- 暗影钢粒: "pixel art item icon, dark steel nugget, tiny gray-black metallic piece, shadowy edges, 16x16, Minecraft style, isolated"

- 暗影钢块: "pixel art block texture, dark steel block, deep gray-black metallic, shadowy wisps, sleek polished surface, 16x16, Minecraft block style, seamless tileable"

- 暗影钢粉: "pixel art item icon, dark steel dust, fine gray-black powder, shadowy particles, 16x16, Minecraft style, isolated"

【工具】
- 暗影钢镐: "pixel art item icon, pickaxe, dark steel head, matte black handle, shadowy aura, industrial design, 16x16, Minecraft style, isolated"

- 暗影钢斧: "pixel art item icon, axe, dark steel blade, matte black handle, sharp edge, shadowy glow, 16x16, Minecraft style, isolated"

- 暗影钢铲: "pixel art item icon, shovel, dark steel blade, matte black handle, sleek design, 16x16, Minecraft style, isolated"

- 暗影钢锄: "pixel art item icon, hoe, dark steel head, matte black handle, industrial look, 16x16, Minecraft style, isolated"

【武器】
- 暗影钢剑: "pixel art item icon, sword, dark steel blade, shadowy edge, black leather handle, sleek design, 16x16, Minecraft style, isolated"

- 暗影钢锤: "pixel art item icon, hammer, dark steel head, shadowy impact surface, black handle, industrial brutal look, 16x16, Minecraft style, isolated"

【护甲】
- 暗影钢头盔: "pixel art item icon, helmet, dark steel, shadowy visor, sleek industrial design, purple glow in crevices, 16x16, Minecraft style, isolated"

- 暗影钢胸甲: "pixel art item icon, chestplate, dark steel plates, shadowy aura, sleek industrial design, purple accents, 16x16, Minecraft style, isolated"

- 暗影钢护腿: "pixel art item icon, leggings, dark steel segments, shadowy joints, sleek industrial look, 16x16, Minecraft style, isolated"

- 暗影钢靴子: "pixel art item icon, boots, dark steel, shadowy sole, sleek industrial design, 16x16, Minecraft style, isolated"

【建筑方块】
- 暗影钢方块: "pixel art block texture, dark steel block, deep gray-black, shadowy wisps on surface, polished metal, 16x16, Minecraft block style, seamless tileable"

【装饰方块】
- 暗影钢台阶: "pixel art block texture, dark steel slab, deep gray-black, shadowy edges, polished surface, 16x16, Minecraft style, seamless"

- 暗影钢楼梯: "pixel art block texture, dark steel stairs, deep gray-black, shadowy steps, polished metal, 16x16, Minecraft style, seamless"

- 暗影钢墙: "pixel art block texture, dark steel wall, deep gray-black, shadowy surface, polished metal, 16x16, Minecraft style, seamless"

【机器核心 - 星辰阵列 T2】
- 星辰阵列核心: "pixel art block texture, machine core, dark steel casing, star array pattern, faint cosmic glow, industrial tech, 16x16, Minecraft block style, front face"
```

---

### T3 星尘 (Stardust) - 维度级

**视觉风格:** 星辰之力，紫蓝渐变，能量纹路，神秘而优雅

```
【基础材料】
- 星尘锭: "pixel art item icon, stardust ingot, purple-blue gradient, twinkling star particles, cosmic energy swirls, 16x16, Minecraft style, isolated"

- 星尘粒: "pixel art item icon, stardust nugget, tiny purple-blue piece, twinkling surface, cosmic particle, 16x16, Minecraft style, isolated"

- 星尘块: "pixel art block texture, stardust block, purple-blue gradient, embedded star particles, cosmic energy veins, 16x16, Minecraft block style, seamless tileable"

- 星尘粉: "pixel art item icon, stardust powder, fine purple-blue powder, twinkling particles, cosmic dust, 16x16, Minecraft style, isolated"

【工具】
- 星尘镐: "pixel art item icon, pickaxe, stardust head, purple-blue crystal, twinkling surface, cosmic energy lines, 16x16, Minecraft style, isolated"

- 星尘斧: "pixel art item icon, axe, stardust blade, purple-blue crystal, star particle edge, elegant design, 16x16, Minecraft style, isolated"

- 星尘铲: "pixel art item icon, shovel, stardust blade, purple-blue crystal, cosmic glow, elegant design, 16x16, Minecraft style, isolated"

- 星尘锄: "pixel art item icon, hoe, stardust head, purple-blue crystal, star particle accents, 16x16, Minecraft style, isolated"

【武器】
- 星尘剑: "pixel art item icon, sword, stardust blade, purple-blue crystal, twinkling edge, cosmic energy trail, elegant handle, 16x16, Minecraft style, isolated"

- 星尘锤: "pixel art item icon, hammer, stardust head, purple-blue crystal, cosmic impact surface, star particles, 16x16, Minecraft style, isolated"

【护甲】
- 星尘头盔: "pixel art item icon, helmet, stardust crystal, purple-blue, cosmic visor, star particles embedded, elegant design, 16x16, Minecraft style, isolated"

- 星尘胸甲: "pixel art item icon, chestplate, stardust plates, purple-blue gradient, cosmic energy veins, star particles, elegant, 16x16, Minecraft style, isolated"

- 星尘护腿: "pixel art item icon, leggings, stardust segments, purple-blue, cosmic joints, star particles, elegant design, 16x16, Minecraft style, isolated"

- 星尘靴子: "pixel art item icon, boots, stardust crystal, purple-blue, cosmic sole, star particle accents, 16x16, Minecraft style, isolated"

【建筑方块】
- 星尘方块: "pixel art block texture, stardust block, purple-blue gradient, embedded star particles, cosmic energy veins, 16x16, Minecraft block style, seamless tileable"

【装饰方块】
- 星尘台阶: "pixel art block texture, stardust slab, purple-blue, embedded stars, cosmic edge, 16x16, Minecraft style, seamless"

- 星尘楼梯: "pixel art block texture, stardust stairs, purple-blue, embedded stars, cosmic steps, 16x16, Minecraft style, seamless"

- 星尘墙: "pixel art block texture, stardust wall, purple-blue, embedded stars, cosmic surface, 16x16, Minecraft style, seamless"

【机器核心 - 星云汲取器 T3】
- 星云汲取器核心: "pixel art block texture, machine core, stardust casing, nebula swirl pattern, cosmic energy vortex, mystical tech, 16x16, Minecraft block style, front face"
```

---

### T4 远古合金 (Ancient Alloy) - 远古级

**视觉风格:** 金黑配色，符文纹路，庄严神圣，远古文明的智慧

```
【基础材料】
- 远古合金锭: "pixel art item icon, ancient alloy ingot, black metal with golden runes, ornate carvings, majestic aura, 16x16, Minecraft style, isolated"

- 远古合金粒: "pixel art item icon, ancient alloy nugget, tiny black-gold piece, golden rune fragment, 16x16, Minecraft style, isolated"

- 远古合金块: "pixel art block texture, ancient alloy block, black metal base, golden rune patterns, ornate design, majestic presence, 16x16, Minecraft block style, seamless tileable"

- 远古合金粉: "pixel art item icon, ancient alloy dust, fine black-gold powder, golden rune fragments, 16x16, Minecraft style, isolated"

【工具】
- 远古合金镐: "pixel art item icon, pickaxe, ancient alloy head, black metal with golden runes, ornate design, majestic look, 16x16, Minecraft style, isolated"

- 远古合金斧: "pixel art item icon, axe, ancient alloy blade, black metal with golden rune edge, ornate design, 16x16, Minecraft style, isolated"

- 远古合金铲: "pixel art item icon, shovel, ancient alloy blade, black metal with golden runes, ornate design, 16x16, Minecraft style, isolated"

- 远古合金锄: "pixel art item icon, hoe, ancient alloy head, black metal with golden runes, ornate design, 16x16, Minecraft style, isolated"

【武器】
- 远古合金剑: "pixel art item icon, sword, ancient alloy blade, black metal with golden runes along edge, ornate handle, majestic, 16x16, Minecraft style, isolated"

- 远古合金锤: "pixel art item icon, hammer, ancient alloy head, black metal with golden runes, ornate impact surface, majestic, 16x16, Minecraft style, isolated"

【护甲】
- 远古合金头盔: "pixel art item icon, helmet, ancient alloy, black metal with golden rune visor, ornate design, majestic presence, 16x16, Minecraft style, isolated"

- 远古合金胸甲: "pixel art item icon, chestplate, ancient alloy plates, black metal with golden runes, ornate design, majestic aura, 16x16, Minecraft style, isolated"

- 远古合金护腿: "pixel art item icon, leggings, ancient alloy segments, black metal with golden runes at joints, ornate, 16x16, Minecraft style, isolated"

- 远古合金靴子: "pixel art item icon, boots, ancient alloy, black metal with golden rune accents, ornate design, majestic, 16x16, Minecraft style, isolated"

【建筑方块】
- 远古合金方块: "pixel art block texture, ancient alloy block, black metal base, golden rune patterns, ornate carved surface, 16x16, Minecraft block style, seamless tileable"

【装饰方块】
- 远古合金台阶: "pixel art block texture, ancient alloy slab, black metal, golden runes on top, ornate edge, 16x16, Minecraft style, seamless"

- 远古合金楼梯: "pixel art block texture, ancient alloy stairs, black metal, golden runes on steps, ornate design, 16x16, Minecraft style, seamless"

- 远古合金墙: "pixel art block texture, ancient alloy wall, black metal, golden rune carvings, ornate surface, 16x16, Minecraft style, seamless"

【机器核心 - 宇宙共鸣器 T4】
- 宇宙共鸣器核心: "pixel art block texture, machine core, ancient alloy casing, cosmic resonance pattern, golden rune circuit, legendary tech, 16x16, Minecraft block style, front face"
```

---

### T5 虚空结晶 (Void Crystal) - 仲裁级

**视觉风格:** 紫黑透明，虚空能量流动，超凡脱俗，接近本源

```
【基础材料】
- 虚空结晶: "pixel art item icon, void crystal, translucent purple-black, ethereal glow, void energy swirling inside, transcendent, 16x16, Minecraft style, isolated"

- 虚空结晶粒: "pixel art item icon, void crystal shard, tiny translucent purple-black piece, ethereal glow, 16x16, Minecraft style, isolated"

- 虚空结晶块: "pixel art block texture, void crystal block, translucent purple-black, void energy veins flowing, ethereal presence, 16x16, Minecraft block style, seamless tileable"

- 虚空结晶粉: "pixel art item icon, void crystal dust, fine purple-black powder, ethereal particles, void energy, 16x16, Minecraft style, isolated"

【工具】
- 虚空结晶镐: "pixel art item icon, pickaxe, void crystal head, translucent purple-black, ethereal glow, void energy lines, transcendent design, 16x16, Minecraft style, isolated"

- 虚空结晶斧: "pixel art item icon, axe, void crystal blade, translucent purple-black, ethereal edge, void energy trail, 16x16, Minecraft style, isolated"

- 虚空结晶铲: "pixel art item icon, shovel, void crystal blade, translucent purple-black, ethereal glow, transcendent design, 16x16, Minecraft style, isolated"

- 虚空结晶锄: "pixel art item icon, hoe, void crystal head, translucent purple-black, ethereal accents, transcendent design, 16x16, Minecraft style, isolated"

【武器】
- 虚空结晶剑: "pixel art item icon, sword, void crystal blade, translucent purple-black, ethereal edge, void energy flowing through, transcendent handle, 16x16, Minecraft style, isolated"

- 虚空结晶锤: "pixel art item icon, hammer, void crystal head, translucent purple-black, ethereal impact surface, void energy swirling, transcendent, 16x16, Minecraft style, isolated"

【护甲】
- 虚空结晶头盔: "pixel art item icon, helmet, void crystal, translucent purple-black, ethereal visor, void energy swirls, transcendent design, 16x16, Minecraft style, isolated"

- 虚空结晶胸甲: "pixel art item icon, chestplate, void crystal plates, translucent purple-black, ethereal glow, void energy veins, transcendent aura, 16x16, Minecraft style, isolated"

- 虚空结晶护腿: "pixel art item icon, leggings, void crystal segments, translucent purple-black, ethereal joints, void energy lines, transcendent, 16x16, Minecraft style, isolated"

- 虚空结晶靴子: "pixel art item icon, boots, void crystal, translucent purple-black, ethereal sole, void energy accents, transcendent design, 16x16, Minecraft style, isolated"

【建筑方块】
- 虚空结晶方块: "pixel art block texture, void crystal block, translucent purple-black, void energy veins flowing, ethereal presence, 16x16, Minecraft block style, seamless tileable"

【装饰方块】
- 虚空结晶台阶: "pixel art block texture, void crystal slab, translucent purple-black, ethereal edge, void energy veins, 16x16, Minecraft style, seamless"

- 虚空结晶楼梯: "pixel art block texture, void crystal stairs, translucent purple-black, ethereal steps, void energy veins, 16x16, Minecraft style, seamless"

- 虚空结晶墙: "pixel art block texture, void crystal wall, translucent purple-black, ethereal surface, void energy veins, 16x16, Minecraft style, seamless"

【机器核心 - 虚空漩涡 T5】
- 虚空漩涡核心: "pixel art block texture, machine core, void crystal casing, swirling vortex pattern, ethereal void energy, transcendent tech, 16x16, Minecraft block style, front face"
```

---

## 六、贴图生成指南

### 通用提示词结构

```
[类型] + [材质风格] + [特征细节] + [尺寸规格]
```

### 推荐生成参数

- 尺寸: 16x16 或 32x32 (可放大使用)
- 风格: pixel art, Minecraft style
- 背景: transparent/isolated
- 抗锯齿: 关闭 (保持像素锐利)

### 颜色参考

| Tier | 主色 | 辅色 | 高光 |
|------|------|------|------|
| T1 | #8B5A2B (暗橙) | #654321 (褐) | #CD853F (金橙) |
| T2 | #2F2F2F (深灰) | #1A1A2E (暗影) | #6B5B95 (淡紫) |
| T3 | #4B0082 (靛紫) | #191970 (深蓝) | #E6E6FA (淡紫白) |
| T4 | #1C1C1C (黑) | #DAA520 (金) | #FFD700 (亮金) |
| T5 | #2D0A3E (虚空紫) | #0D0D0D (虚空黑) | #9B59B6 (亮紫) |

---

> **文档版本:** 1.1
> **最后更新:** 2026-03-12