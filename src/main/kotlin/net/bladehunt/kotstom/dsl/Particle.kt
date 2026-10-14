package net.bladehunt.kotstom.dsl

import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Vec
import net.minestom.server.network.packet.server.play.ParticlePacket
import net.minestom.server.particle.Particle

data class ParticleBuilder(
    var particle: Particle = Particle.ANGRY_VILLAGER,
    var overrideLimiter: Boolean = false,
    var longDistance: Boolean = false,
    var position: Point = Vec.ZERO,
    var offset: Point = Vec.ZERO,
    var particleData: Float = 0F,
    var count: Int = 1,
) {
    fun build(): ParticlePacket = ParticlePacket(particle, overrideLimiter, longDistance, position, offset, particleData, count)
}

inline fun particle(block: ParticleBuilder.() -> Unit): ParticlePacket = ParticleBuilder().apply(block).build()
