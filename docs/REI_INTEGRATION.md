# REI Integration for Factor Craft

## Overview
This module provides REI (Roughly Enough Items) integration for Factor Craft, allowing players to view recipes in-game.

## Status
**Basic Structure Created** - Awaiting REI API dependency resolution.

## Files
- `FactorCraftREIPlugin.java` - REI plugin entry point
- `category/*.java` - Recipe category definitions
- `display/*.java` - Recipe display implementations

## How to Enable

### 1. Add REI Dependencies
Add to `build.gradle`:

```groovy
repositories {
    maven { url = 'https://maven.shedaniel.me/' }
}

dependencies {
    // REI API (compile-only, optional at runtime)
    modCompileOnly "me.shedaniel:RoughlyEnoughItems-api-fabric:VERSION"
    
    // For testing (optional)
    modLocalRuntime "me.shedaniel:RoughlyEnoughItems-fabric:VERSION"
}
```

### 2. Find Correct Version
For Minecraft 1.21.4, check:
- https://modrinth.com/mod/roughly-enough-items/versions
- https://maven.shedaniel.me/me/shedaniel/RoughlyEnoughItems-api-fabric/

### 3. Register Plugin
Add to `fabric.mod.json`:

```json
"entrypoints": {
  "rei": [
    "com.factorcraft.compat.rei.FactorCraftREIPlugin"
  ]
}
```

## Recipe Categories

| Category | Description |
|----------|-------------|
| Extractor | Factor extraction recipes (T1-T5) |
| Consumer | Factor consumption recipes (T1-T5) |
| Synthesizer | Synthesis recipes (T1-T5) |

## Localization Keys
- `factorcraft.rei.category.extractor` - "Factor Extractor"
- `factorcraft.rei.category.consumer` - "Factor Consumer"
- `factorcraft.rei.category.synthesizer` - "Factor Synthesizer"

## Notes
- REI is optional at runtime
- If REI is not installed, the plugin won't load
- Recipes are dynamically loaded from config files