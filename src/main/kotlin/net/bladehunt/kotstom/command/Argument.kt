package net.bladehunt.kotstom.command

import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.arguments.Argument

/**
 * Retrieves the value of an argument from the `CommandContext`
 *
 * @author oglassdev
 */
context(context: CommandContext)
operator fun <T> Argument<T>.invoke(): T = context[this]
