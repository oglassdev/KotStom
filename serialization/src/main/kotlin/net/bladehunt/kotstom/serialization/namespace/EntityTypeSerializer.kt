package net.bladehunt.kotstom.serialization.namespace

import net.kyori.adventure.key.Key
import net.minestom.server.entity.EntityType

object EntityTypeSerializer : AbstractProtocolObjectSerializer<EntityType>(EntityType::class) {
    override fun fromKey(key: Key): EntityType? = EntityType.fromKey(key)
}
