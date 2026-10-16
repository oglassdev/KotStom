### 0.6.0-beta.0

- **Breaking Changes**
    - Updated to Minestom 26.2, Java 25, and Kotlin 2.4.21 (#18)
    - Removed `MinecraftServer.BenchmarkManager` (removed upstream in Minestom) (#18)
    - `buildInstance` now accepts `Registries` instead of the dimension type registry (#18)
    - `Kommand`: Replaced `KommandExecutorContext` with a `context(CommandContext)` parameter and top-level `Argument.invoke()`, and removed the unused `scope` parameter from `defaultExecutorAsync` (#20)
    - `@InstanceDsl` now restricts implicit `InstanceContainer` receiver scope inside `generator { }` (#23)
    - Removed no-op `@DslMarker` annotations from functions and properties (#22)
- Features
    - Added `SchedulerDispatcher` and `Scheduler.asCoroutineDispatcher()` for running coroutines on any Minestom scheduler (#28)
- Fixes
    - `MinestomDispatcher` now counts coroutine delays in ticks (#27)
    - Included `overrideLimiter` in `ParticleBuilder` equality and hash code (#25)
    - Registered `StaticProtocolObject` serializers per concrete type in `MinestomModule` (#18)
    - Dropped `Point` extensions shadowed by Minestom members (`asVec()`, `asPos()`) and deprecated `Vec.roundToBlock()` in favor of `asBlockVec()` (#21)

### 0.5.0-alpha.0

- **Breaking Changes**
    - Moved group ID from `net.bladehunt` to `dev.oglass`
    - Updated to Minestom 1.21.7 and `DataComponents` (#16)

### 0.4.0 (Proposed/alpha)

- **Breaking Changes**
    - `MinestomRunnable` has been removed in favor of a better Scheduler extension
    - Removed unneeded/strange Adventure extensions
- Fixes
    - `MinestomDispatcher` now implements `Delay`
    - `EventNodeInventory` maps Events using Minestom's internal weak EventNodes instead of creating an unneeded global
      listener
- Features
    - Extension: `MinecraftServer.startSuspending` starts the server using Dispatchers.IO while suspending the current
      coroutine
    - Extension: `Entity.editMeta` edits the Entity's meta using a reified type parameter. This is similar to
      Minestom's `Entity.editEntityMeta`

### 0.3.0

- **Breaking Changes**
    - ItemComponents no longer use invoke in the item builder dsl
    - Removed `Schedulable.scheduler` because `scheduler()` exists
- Fixes
    - Pos and Vec now return the appropriate classes when using operations
    - Vec now rounds to BlockVec
    - Get and set operators for inventories now use the super class AbstractInventory
- Features
    - `adventure-serialization`
        - A kotlinx.serialization encoder/decoder for Adventure NBT compounds and lists
        - Usage: `AdventureNbt.encodeToCompound`, `AdventureNbt.decodeFromCompound`
        - AdventureNbt can also be instantiated to have custom properties
            - discriminator (polymorphism)
            - shouldEncodeDefaults
            - serializersModule
    - `serialization`
        - Minestom/Adventure kotlinx.serialization serializers
        - Contains `MinestomModule` and `MinestomConfigModule` for their respective serializers
            - `MinestomModule` serializes things in less human-readable formats
            - `MinestomConfigModule` serializes things in slightly more human-readable formats, such as UUIDs as strings

### 0.2.0-beta

- **Breaking Changes**
    - Moved adventure extensions
    - Removed deprecated Manager object (also renamed to MinecraftServer)
    - (Minestom) Use registries instead of managers
    - Renamed KommandBuilder's `default` to `defaultExecutor`
- Features
    - Updated row sizes
    - Async/blocking handlers for events and kommands
    - KBar (Sidebar DSL)
    - String -> Component extensions: `String.color(), String.decorate()`
    - More adventure extensions
- Bug fixes
    - Point extensions (sub, mul) only added, now they do the proper operation
- Code quality
    - Added KDoc comments
    - Formatted project with `ktfmt`
- Dependencies
    - Updated Gradle from `8.7` to `8.8`
    - Updated Kotlin to `2.0.0`
    - Updated Minestom, MiniMessage, Kotest, Kt Serialization, Kt Coroutines, Kt Reflect

### 0.1.0

- Updated to Java 21 & Minestom 1.20.5
- Schedulers use `ExecutationType.TICK_START`
- Example server rewritten
- `EventNodeContainerInventory`
    - A ContainerInventory with an EventNode
    - Registers a single global listener that calls the inventory's EventNodes upon event.
    - Usage:
        - Initialization: `EventNodeContainerInventory(InventoryType, Component)`
        - Accessing the EventNode: `eventNodeContainerInventory.eventNode()`
- Items
    - Introduced `customName` and `itemName`
    - Removed `displayName`
    - TagComponents can be used with `invoke` (e.g. `ItemComponent.DAMAGE(5)`)

### Bladehunt Rewrite

- CoroutineUtil
    - `CompletableFuture.await()` can be used instead
    - Old: `entity.suspendTeleport(pos)`
    - New: `entity.teleport(pos).await()`
    - Makes more sense
    - Schedulables now have an `await` function that suspends until the delay is finished
        - Example: `player.await(TaskSchedule.tick(12))`
- DSLs separated
    - DSLs were separated to remove any builder logic from classes
- Tag delegation changed to just use `var tagValue by tag` instead of requiring `TagDelegate(tag)`
