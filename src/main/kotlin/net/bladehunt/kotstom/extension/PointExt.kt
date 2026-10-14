package net.bladehunt.kotstom.extension

import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import kotlin.math.PI

inline val Point.x
    get() = this.x()
inline val Point.y
    get() = this.y()
inline val Point.z
    get() = this.z()

operator fun Point.component1() = this.x

operator fun Point.component2() = this.y

operator fun Point.component3() = this.z

operator fun Pos.component4() = this.yaw

operator fun Pos.component5() = this.pitch

fun Pos.roundToBlock(): Pos =
    Pos(
        this.blockX().toDouble(),
        this.blockY().toDouble(),
        this.blockZ().toDouble(),
        this.yaw,
        this.pitch)

@Deprecated("Use Minestom's asBlockVec()", ReplaceWith("asBlockVec()"))
fun Vec.roundToBlock(): BlockVec = asBlockVec()

fun Vec.rotateAroundXDegrees(degrees: Double) = rotateAroundX(degrees * (PI / 180))

fun Vec.rotateAroundYDegrees(degrees: Double) = rotateAroundY(degrees * (PI / 180))

fun Vec.rotateAroundZDegrees(degrees: Double) = rotateAroundZ(degrees * (PI / 180))

fun Vec.rotateDegrees(degreesX: Double, degreesY: Double, degreesZ: Double) =
    rotate(degreesX * (PI / 180), degreesY * (PI / 180), degreesZ * (PI / 180))

// Division is not needed as it already works
operator fun Pos.plus(other: Point) = this.add(other)

operator fun Pos.minus(other: Point) = this.sub(other)

operator fun Pos.times(other: Point) = this.mul(other)

operator fun Pos.times(value: Double) = this.mul(value)

operator fun Vec.plus(other: Point) = this.add(other)

operator fun Vec.minus(other: Point) = this.sub(other)

operator fun Vec.times(other: Point) = this.mul(other)

operator fun Vec.times(value: Double) = this.mul(value)

operator fun Vec.unaryMinus() = this.neg()

operator fun Vec.unaryPlus() = this.abs()
