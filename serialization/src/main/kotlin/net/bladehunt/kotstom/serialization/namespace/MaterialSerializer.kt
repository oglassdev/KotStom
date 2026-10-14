package net.bladehunt.kotstom.serialization.namespace

import net.kyori.adventure.key.Key
import net.minestom.server.item.Material

object MaterialSerializer : AbstractProtocolObjectSerializer<Material>(Material::class) {
    override fun fromKey(key: Key): Material? = Material.fromKey(key)
}
