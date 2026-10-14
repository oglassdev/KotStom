package net.bladehunt.kotstom.serialization.namespace

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.key.Key
import net.minestom.server.registry.StaticProtocolObject
import kotlin.reflect.KClass

abstract class AbstractProtocolObjectSerializer<T : StaticProtocolObject<T>>(
    serialName: String,
) : KSerializer<T> {
    constructor(clazz: KClass<T>) : this(requireNotNull(clazz.qualifiedName))

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(serialName, PrimitiveKind.STRING)

    protected abstract fun fromKey(key: Key): T?

    final override fun deserialize(decoder: Decoder): T = requireNotNull(fromKey(Key.key(decoder.decodeString())))

    final override fun serialize(
        encoder: Encoder,
        value: T,
    ) = encoder.encodeString(value.name())
}
