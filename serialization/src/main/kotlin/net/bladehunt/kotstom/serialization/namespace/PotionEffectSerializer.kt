package net.bladehunt.kotstom.serialization.namespace

import net.kyori.adventure.key.Key
import net.minestom.server.potion.PotionEffect

object PotionEffectSerializer :
    AbstractProtocolObjectSerializer<PotionEffect>(PotionEffect::class) {
    override fun fromKey(key: Key): PotionEffect? = PotionEffect.fromKey(key)
}
