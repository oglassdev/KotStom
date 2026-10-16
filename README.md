# <img alt="logo" width="45" align="center" src=".github/assets/logo.png" /> KotStom

---

### Adding as a dependency (Gradle)

First, make sure that `mavenCentral()` is in your `build.gradle.kts`

```kotlin
repositories {
    mavenCentral()
}
```

The latest version can be found [here](https://github.com/oglassdev/KotStom/releases).

**The KotStom version for 26.2 is `0.6.0-beta.0`**

## Module Overview

### KotStom

```kotlin
implementation("dev.oglass:kotstom:<version>")
```

- Minestom Extensions
    - Tag delegates (`by Tag`)
    - Scheduler extensions (`#await`, `scheduleTask`, `asCoroutineDispatcher`)
    - Adventure extensions
        - Better `text` function
        - `String.asMini` - Converts to MiniMessage. Requires MiniMessage to be in classpath
    - CommandManager extension (Register multiple commands at once)
    - Point extensions
    - Inventory extensions (`Inventory#rowSize, get, set`)
    - EventNode extensions (Register multiple listeners)
- DSLs
    - Kommand (`kommand`) - Easier commands w/ delegates
    - Item (`item`) - Item builder
    - Particle (`particle`) - Particle packet builder
    - Event (`listen, buildListener`) - Better kotlin support with reified type parameters and better builders
    - Instance (`buildInstance`) - InstanceContainer builder
- MinecraftServer
    - Each registry and manager has a corresponding `inline val`
        - e.g. `CommandManager` means `MinecraftServer.getCommandManager()`

### adventure-serialization

```kotlin
implementation("dev.oglass:kotstom-adventure-serialization:<version>")
```

This module contains an Adventure NBT encoder and decoder.

- Supports polymorphism
- Supports collections
- Usage: `AdventureNbt.encodeToCompound`, `AdventureNbt.decodeFromCompound`
    - Initializing your own instance of `AdventureNbt` can be done using `AdventureNbt`'s constructor

### serialization

```kotlin
implementation("dev.oglass:kotstom-serialization:<version>")
```

This module contains some kotlinx.serialization serializers for Minestom.

- Adventure
    - KeySerializer
    - SoundSerializer
    - SoundSourceSerializer
    - MiniMessageSerializer
- StaticProtocolObject
    - MaterialSerializer
    - EntityTypeSerializer
    - PotionEffectSerializer
    - NamespaceSerializer
- Point
    - Supports non-content based serialization (Needs a `type` field)
    - Serial names are the simple names
    - BlockVecSerializer
    - PosSerializer
    - VecSerializer
- UUID
    - StringUuidSerializer (Serializes to string)
    - UuidSerializer (Serializes to most significant/least significant bits)
- Module
    - Note: MiniMessageSerializer is not included in these modules
    - MinestomModule
        - Prefers serializers such as UuidSerializer
    - MinestomConfigModule
        - Prefers more human-readable serializers

### For more information, visit [the wiki](https://www.bladehunt.net/developers/kotstom) or view [the example](example/src/main/kotlin)
