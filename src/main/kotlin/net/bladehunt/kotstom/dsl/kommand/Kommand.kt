package net.bladehunt.kotstom.dsl.kommand

import net.bladehunt.kotstom.command.Kommand
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.CommandExecutor

internal typealias KommandExecutor = context(CommandContext) (CommandSender) -> Unit

@DslMarker
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.TYPE)
annotation class KommandDSL

/**
 * Sets the default executor of the Kommand
 *
 * @author oglassdev
 */
@KommandDSL
inline fun Kommand.defaultExecutor(crossinline block: @KommandDSL KommandExecutor) =
    CommandExecutor { sender, context -> block(context, sender) }
        .also { defaultExecutor = it }

/**
 * Creates a subcommand using another `Kommand` builder
 *
 * @param block The suspending code to run
 * @author oglassdev
 */
@KommandDSL
inline fun Kommand.subkommand(name: String, vararg aliases: String, block: @KommandDSL Kommand.() -> Unit): Kommand =
    Kommand(name, *aliases).apply(block).also { command ->
        addSubcommand(command)
    }

/**
 * `Kommand` builder DSL
 *
 * @see net.bladehunt.kotstom.command.Kommand
 * @author oglassdev
 */
@KommandDSL
inline fun kommand(name: String, vararg aliases: String, block: @KommandDSL Kommand.() -> Unit): Kommand =
    Kommand(name, *aliases).apply(block)
