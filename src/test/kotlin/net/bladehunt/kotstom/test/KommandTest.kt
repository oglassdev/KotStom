package net.bladehunt.kotstom.test

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import net.bladehunt.kotstom.CommandManager
import net.bladehunt.kotstom.command.invoke
import net.bladehunt.kotstom.dsl.kommand.kommand
import net.bladehunt.kotstom.dsl.kommand.subkommand
import net.bladehunt.kotstom.dsl.kommand.syntax
import net.minestom.server.command.builder.arguments.ArgumentString

class KommandTest :
    FunSpec({
        beforeAny { SERVER }
        test("should register commands") {
            CommandManager.register(kommand("test-command") {})
            CommandManager.commandExists("test-command") shouldBe true
        }
        test("should create subcommands") {
            val command = kommand("subcommand-test") {
                subkommand("first") { }
                subkommand("second") { }
            }
            command.subcommands.size shouldBe 2
        }
        test("should create arguments") {
            val command = kommand("syntax-test") {
                val value = ArgumentString("value")

                syntax(value) { value() }
            }
            command.syntaxes.first().arguments.first().id shouldBe "value"
        }
    })
