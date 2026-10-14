package net.bladehunt.kotstom.test

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import net.bladehunt.kotstom.dsl.item.amount
import net.bladehunt.kotstom.dsl.item.customName
import net.bladehunt.kotstom.dsl.item.item
import net.bladehunt.kotstom.extension.adventure.asMini
import net.bladehunt.kotstom.extension.adventure.toMiniMessage
import net.minestom.server.component.DataComponents
import net.minestom.server.item.Material

class ItemTest :
    FunSpec({
        beforeSpec { SERVER }

        test("materials & attributes should be set") {
            val item =
                item(Material.BOOK) {
                    amount = 5
                    customName = "<red>Custom Book".asMini()
                }
            item.amount() shouldBe 5
            item.get(DataComponents.CUSTOM_NAME)!!.toMiniMessage() shouldBe "<red>Custom Book"
        }
    })
