# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build the mod
./gradlew build

# Run unit tests (JUnit 5)
./gradlew test

# Run a specific test class
./gradlew test --tests "com.factorcraft.FactorSystemTest"

# Run game tests (requires Minecraft environment)
./gradlew runGametest

# Start development client
./gradlew runClient

# Run quick verification
./gradlew runQuickTest
```

## Module Verification Tasks

The project has custom verification tasks for each module:

```bash
./gradlew verifyCommandM0       # Command system verification
./gradlew verifyFactorM1        # Factor system verification
./gradlew verifyCreatureM1b     # Creature drop system verification
./gradlew verifyMaterialsM2     # Material system verification
./gradlew verifyMaterialsM2TierSync  # Material tier sync verification
```

## Architecture

### Module System

The codebase uses a modular architecture with dependency validation. All modules implement `FactorCraftModule` interface and are registered in `ModuleBootstrap.java`. Modules initialize in order, with dependency checking.

**Module load order** (defined in `ModuleBootstrap.DEFAULT_MODULES`):
1. ErrorModule → 2. CommandModule → 3. FactorSystemModule → 4. CycleModuleAdapter → 5. CreatureDropModule → 6. MaterialsModule → 7. TechnologyModule → 8. GearModule → 9. BuildingModule → 10. SocialModule → 11. AdvancementModule → 12. NonCoreIntegrationModule → 13. FactorWorldEventModule → 14. VfxModule → 15. ResearchModule → 16. ProfessionModule

Each module has:
- `moduleId()`: Unique identifier
- `dependencies()`: List of required module IDs
- `initialize()`: Called once at startup
- `reload()`: Optional, called on config reload
- `shutdown()`: Optional, called on server stop

### Core Systems

**Factor System** (`module/factor/`): The central energy system. `FactorService` manages chunk-level Factor concentrations with:
- Tide system: Periodic concentration fluctuations
- Day-tier transitions: Daily state changes
- Diffusion system: Factor spreads between chunks
- Effects: Player buffs/debuffs based on concentration

**Technology Module** (`module/technology/`): Machines and multiblock structures:
- Machine block entities: Extractor, Synthesizer, Consumer, Cultivator, Breeder, Transmitter
- Multiblock detection via `MultiblockDetector` and `TechnologyPatterns`
- Screen handlers for machine GUIs

**Event Bus** (`module/event/bus/`): `SimpleFactorEventBus` for inter-module communication. Events include: `FactorChangeEvent`, `FactorTierChangeEvent`, `FactorTideEvent`, `CreatureDropRollEvent`, `CommandExecutedEvent`, etc.

**Network Sync** (`module/network/`): `NetworkPackets` registers Fabric networking payloads for client-server state synchronization (Factor concentrations, quests, professions, machine states).

### Data-Driven Content

- Recipes: `src/main/resources/data/factorcraft/recipes/`
- Quests: `src/main/resources/data/factorcraft/quests/`
- Dynamic configs: `src/main/resources/factorcraft/dynamic/` (JSON files for commands, models)

Use `/reload` command in-game to hot-reload data packs.

### Project Structure

```
src/main/java/com/factorcraft/
├── FactorCraftMod.java        # Mod entry point
├── module/                    # Feature modules (each is a subsystem)
│   ├── ModuleBootstrap.java   # Module registration & initialization
│   ├── FactorCraftModule.java # Module interface
│   ├── factor/                # Factor energy system
│   ├── technology/            # Machines & multiblocks
│   ├── creature/              # Creature drops & mutations
│   ├── material/              # Materials & traits
│   ├── quest/                 # Quest system
│   ├── profession/            # Profession/class system
│   ├── event/                 # Event bus & event types
│   └── network/               # Network packets
├── registry/                  # Item groups, recipes, sounds
├── component/                 # Data components (Fabric 1.21.4)
├── config/                    # Configuration system
├── network/                   # Config sync handler
└── world/                     # World generation

src/test/java/                 # JUnit 5 unit tests
src/main/java/com/factorcraft/gametest/  # Fabric Game Tests
```

## Key Patterns

### API Provider Pattern
Modules expose functionality through API interfaces with provider classes:
- `FactorApiProvider` for Factor system
- `MaterialApiProvider` for materials
- Pattern: `ApiProvider.set(implementation)` at initialization, `ApiProvider.get()` for access

### Block Entity Registration
Block entities are registered in their respective module's `block/` packages. Screen handlers follow Fabric's `ScreenHandlerType` registration pattern.

### Test Organization
- Unit tests in `src/test/java/` use JUnit 5
- Game tests in `src/main/java/com/factorcraft/gametest/` use Fabric Game Test API
- Module verifiers are executable Java classes (run via `./gradlew verify*`)

## Version Requirements

- Minecraft: 1.21.4
- Fabric Loader: 0.16.10+
- Fabric API: 0.119.2+
- Java: 21+

## Notes

- Comments and documentation are in Chinese (项目注释和文档使用中文)
- Uses Conventional Commits for commit messages
- REI integration is compile-only due to Mixin compatibility issues (Issue #190)