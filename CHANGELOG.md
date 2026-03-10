# Changelog

All notable changes to Factor Craft will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]

### Planned
- TechnologyModule: Tech tree system (T1-T5)
- QuestModule: Quest system with 20+ main quests
- UIModule: Complete UI framework
- LootModule: Enhanced loot tables
- CombatModule: Monster AI and boss mechanics

---

## [0.1.0-Alpha] - 2026-03-10

### ✨ Added

#### Core Modules
- **CombatModule**: Complete weapon system
  - 15 weapons (T1-T5): Swords, Hammers, Bows
  - Balanced attributes (damage, speed, durability)
  - Factor energy bonuses
  - Armor penetration (hammers)
  - Dimensional穿透 (T2+ weapons)

- **MultiblockDetector**: Structure detection system
  - 16 structure blueprints (12 unique types)
  - T1-T5 tier structures
  - Performance optimized (<2ms detection)
  - Pattern validation system

- **FactorNetworkManager**: Cross-dimensional transfer
  - Dimension base value system (Overworld: 0.5, Nether: 1.5, End: 3.0)
  - Transfer multiplier calculation
  - Configuration support
  - Transfer logging (max 100 records)
  - Performance statistics tracking

- **CycleModule**: Factor tide cycle system
  - 24000 ticks cycle (1 Minecraft day)
  - 4 phases: Rising → Peak → Falling → Trough
  - Factor multiplier: 0.7 - 1.3 (sine wave)
  - Phase prediction system
  - Configurable amplitude (default 30%)

#### Testing
- **81 Unit Tests** (100% pass rate)
  - CombatModule: 20 tests
  - MultiblockDetector: 25 tests
  - FactorNetworkManager: 30 tests
  - CycleModule: 22 tests
  - DimensionType: 9 tests
  - TideSystem: 8 tests

#### Performance Benchmarks
- Multiblock detection: <2ms (target <10ms) ✅
- T1 structure detection: <1ms (target <2ms) ✅
- Cycle calculation (1000x): <1ms (target <2ms) ✅
- Cycle prediction (100x): <0.5ms (target <1ms) ✅
- Weapon attribute access (10000x): <0.2ms (target <0.5ms) ✅
- Blueprint loading: <3ms (target <5ms) ✅
- Comprehensive test: <10ms (target <20ms) ✅

#### Documentation
- Developer Guide (514 lines)
- Phase 3 Multi-Expert Plan
- API Documentation
- Installation Guide

#### Infrastructure
- GitHub Workflow templates
- Issue templates (Feature, Bug, Task)
- PR template
- 28 project labels
- 3 milestones (v0.1.0, v0.2.0, v1.0.0)
- Branch protection rules

### 🔧 Technical

#### Architecture
- Module-based system (ModuleBootstrap)
- Fabric 1.21.4 best practices
- Java 21 features
- Gradle 8.10 build system

#### Code Quality
- Small PR strategy (<400 lines)
- Code review process
- Automated testing
- Build verification

### 📦 Technical Details

#### Dependencies
- Minecraft: 1.21.4
- Fabric Loader: Latest
- Fabric API: Latest
- Java: 21+

#### Build
- Gradle: 8.10
- Fabric Loom: 1.8.13
- Build time: ~18 seconds

### 🎮 Gameplay

#### Factor System
- Dimension-based energy system
- No FE units (Factor is dimension stability)
- Transfer multiplier = sender_base / receiver_base
- Tide cycle affects all Factor operations

#### Combat
- 5 weapon tiers (T1-T5)
- 3 weapon types: Sword, Hammer, Bow
- Balanced attributes per tier
- Factor bonuses scale with tier

#### Multiblock Structures
- 5 tiers (T1-T5)
- 12 unique structure types
- Automatic detection and validation
- Performance optimized

---

## [0.0.1-Pre-Alpha] - 2026-03-01

### Added
- Initial project setup
- Phase 1: MVP Design (15 tasks)
- Phase 2: Detailed Implementation Framework (16 tasks)
- Core dimension system
- Basic Factor mechanics

---

## Future Releases

### [0.2.0-Alpha] - Planned 2026-03-20
- TechnologyModule: Tech tree system
- QuestModule: Quest system
- UIModule: User interface
- Enhanced documentation

### [0.3.0-Beta] - Planned 2026-03-25
- Monster AI
- Boss mechanics
- Loot system enhancements
- Balance adjustments

### [1.0.0-Release] - Planned 2026-03-30
- All features complete
- Full documentation
- Performance optimized
- Multi-platform release (CurseForge, Modrinth, GitHub)

---

## Development Workflow

### Branch Strategy
- `main` → Production ready (2 reviews required)
- `develop` → Integration branch (1 review required)
- `feature/*` → Feature branches

### PR Guidelines
- Max 400 lines per PR
- Must link to Issue/Milestone
- All tests must pass
- Code review required

### Testing
- Unit tests: 81 (100% pass)
- Target: 150+ tests by Beta
- Performance benchmarks enforced

---

**Links:**
- [GitHub Repository](https://github.com/Nnyjk/factor-craft)
- [Issue Tracker](https://github.com/Nnyjk/factor-craft/issues)
- [Developer Guide](docs/DEVELOPER_GUIDE.md)
- [Project Roadmap](docs/plans/PROJECT_ROADMAP.md)
