// @ItemDsl on the ItemStack.Builder properties only drives IDE DSL highlighting
@file:Suppress("DSL_MARKER_APPLIED_TO_WRONG_TARGET")

package net.bladehunt.kotstom.dsl.item

import net.bladehunt.kotstom.extension.adventure.asMini
import net.kyori.adventure.text.Component
import net.minestom.server.component.DataComponents
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material

@DslMarker
@Target(AnnotationTarget.TYPE, AnnotationTarget.PROPERTY)
annotation class ItemDsl

/**
 * A value class that wraps a list to make lore easier
 *
 * @author oglassdev
 */
@JvmInline
value class ItemLore(
    private val list: MutableList<Component>,
) : MutableList<Component> by list {
    /**
     * Adds a `Component` to the lore
     *
     * @author oglassdev
     */
    operator fun Component.unaryPlus() {
        add(this)
    }

    /**
     * Adds a `String`, formatted with `MiniMessage`, to the lore
     *
     * @author oglassdev
     */
    operator fun String.unaryPlus() {
        add(this.asMini())
    }
}

/**
 * Sets the `ItemComponent.LORE` of an ItemStack.Builder
 *
 * @author oglassdev
 */
inline fun ItemStack.Builder.lore(block: @ItemDsl ItemLore.() -> Unit) {
    set(DataComponents.LORE, ItemLore(arrayListOf()).apply(block))
}

/**
 * Sets the amount of an ItemStack.Builder
 *
 * @author oglassdev
 */
@ItemDsl
inline var ItemStack.Builder.amount: Int
    get() = throw NotImplementedError("Cannot get amount from ItemStack.Builder")
    set(value) {
        this.amount(value)
    }

/**
 * Sets the `ItemComponent.ITEM_NAME` of an ItemStack.Builder
 *
 * @author oglassdev
 */
@ItemDsl
inline var ItemStack.Builder.itemName: Component
    get() = throw NotImplementedError("Cannot get ItemComponent from ItemStack.Builder")
    set(value) {
        set(DataComponents.ITEM_NAME, value)
    }

/**
 * Sets the `ItemComponent.CUSTOM_NAME` of an ItemStack.Builder
 *
 * @author oglassdev
 */
@ItemDsl
inline var ItemStack.Builder.customName: Component
    get() = throw NotImplementedError("Cannot get ItemComponent from ItemStack.Builder")
    set(value) {
        set(DataComponents.CUSTOM_NAME, value)
    }

/**
 * Sets the `ItemComponent.DAMAGE` of an ItemStack.Builder
 *
 * @author oglassdev
 */
@ItemDsl
inline var ItemStack.Builder.damage: Int
    get() = throw NotImplementedError("Cannot get ItemComponent from ItemStack.Builder")
    set(value) {
        set(DataComponents.DAMAGE, value)
    }

/**
 * Sets the `ItemComponent.UNBREAKABLE` of an ItemStack.Builder
 *
 * @author oglassdev
 */
@ItemDsl
inline var ItemStack.Builder.unbreakable: Boolean
    get() = throw NotImplementedError("Cannot get ItemComponent from ItemStack.Builder")
    set(value) {
        if (value) {
            set(DataComponents.UNBREAKABLE, net.minestom.server.utils.Unit.INSTANCE)
        } else {
            remove(DataComponents.UNBREAKABLE)
        }
    }

/**
 * ItemStack builder DSL
 *
 * @author oglassdev
 */
inline fun item(
    material: Material = Material.STONE,
    block: @ItemDsl ItemStack.Builder.() -> Unit,
): ItemStack = ItemStack.builder(material).apply(block).build()

/**
 * ItemStack builder DSL
 *
 * @author oglassdev
 */
fun item(material: Material): ItemStack = ItemStack.of(material)

/**
 * ItemStack builder extension DSL
 *
 * @author oglassdev
 */
inline fun ItemStack.builder(block: @ItemDsl ItemStack.Builder.() -> Unit): ItemStack = builder().apply(block).build()
