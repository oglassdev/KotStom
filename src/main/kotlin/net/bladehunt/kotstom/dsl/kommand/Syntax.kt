package net.bladehunt.kotstom.dsl.kommand

import net.bladehunt.kotstom.command.Kommand
import net.bladehunt.kotstom.command.KommandSyntax
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.CommandExecutor
import net.minestom.server.command.builder.CommandSyntax
import net.minestom.server.command.builder.arguments.Argument
import net.minestom.server.command.builder.condition.CommandCondition
import net.minestom.server.entity.Player
import java.util.function.Function

/**
 * `CommandSyntax` builder DSL
 *
 * @property conditions The list of command conditions
 * @property executor The command executor
 * @property defaultValues The map of default values for command arguments
 * @property arguments The list of command arguments
 * @author oglassdev
 */
data class KommandSyntaxBuilder(
    val conditions: MutableList<CommandCondition> = arrayListOf(),
    var executor: CommandExecutor? = null,
    val defaultValues: MutableMap<String, Function<CommandSender, Any>> = hashMapOf(),
    val arguments: MutableList<Argument<*>> = arrayListOf()
) {
    /**
     * Adds a condition to allow only players to execute the command
     *
     * @return The player-only command condition
     * @author oglassdev
     */
    fun onlyPlayers(): CommandCondition =
        CommandCondition { commandSender, _ -> commandSender is Player }.also(conditions::add)

    /**
     * Adds a condition
     *
     * @param block The condition logic
     * @return The custom command condition
     * @author oglassdev
     */
    fun condition(
        condition: CommandCondition
    ) {
        conditions += condition
    }

    /**
     * Sets the executor for the command
     *
     * @param block The executor logic
     * @return The command executor
     * @author oglassdev
     */
    inline fun executor(
        crossinline block: @KommandDSL KommandExecutor
    ): CommandExecutor =
        CommandExecutor { sender, ctx -> block(ctx, sender) }
            .also { executor = it }

    /**
     * Sets a default value for a command argument
     *
     * @param key The argument key
     * @param block The function to provide the default value
     * @return The function providing the default value
     * @author oglassdev
     */
    inline fun <T> default(
        argument: Argument<T>,
        crossinline block: @KommandDSL (CommandSender) -> T
    ): Function<CommandSender, T> =
        Function<CommandSender, T> { sender -> block(sender) }.also {
            defaultValues[argument.id] = it as Function<CommandSender, Any>
        }

    /**
     * Sets a default value for a command argument
     *
     * @param key The argument key
     * @param block The function to provide the default value
     * @return The function providing the default value
     * @author oglassdev
     */
    inline fun default(
        key: String,
        crossinline block: @KommandDSL (CommandSender) -> Any
    ): Function<CommandSender, Any> =
        Function<CommandSender, Any> { sender -> block(sender) }.also { defaultValues[key] = it }

    /**
     * Builds the command syntax from the provided conditions, executor, and arguments
     *
     * @return The constructed command syntax
     * @throws IllegalArgumentException if the executor is null.
     * @author oglassdev
     */
    fun build(): CommandSyntax {
        if (executor == null) throw IllegalArgumentException("Executor cannot be null!")
        return KommandSyntax(
            commandCondition =
                when (conditions.size) {
                    1 -> conditions.first()
                    /* Automagically returns true if nothing exists */
                    else ->
                        CommandCondition { sender, label ->
                            conditions.all { it.canUse(sender, label) }
                        }
                },
            commandExecutor = executor!!,
            defaultValues = defaultValues,
            args = arguments.toTypedArray()
        )
    }
}

/**
 * Extension function for `KommandBuilder` to define a command syntax
 *
 * @param arguments The command arguments
 * @param condition The condition to execute the command
 * @param defaultValues The map of default values for command arguments
 * @param block The executor logic
 * @author oglassdev
 */
inline fun Kommand.syntax(
    vararg arguments: Argument<*>,
    crossinline condition: @KommandDSL (sender: CommandSender, label: String?) -> Boolean =
        { _, _ ->
            true
        },
    defaultValues: Map<String, Function<CommandSender, Any>> = emptyMap(),
    crossinline block: @KommandDSL KommandExecutor,
) =
    KommandSyntax(
        commandCondition = { sender, label -> condition(sender, label) },
        commandExecutor = { sender, context ->
            block(context, sender)
        },
        defaultValues = defaultValues,
        args = arguments
    )
        .let { syntaxes.add(it) }

/**
 * Extension function for `KommandBuilder` to build a command syntax using `SyntaxBuilder`
 *
 * @param arguments The command arguments
 * @param block The `SyntaxBuilder` logic
 * @author oglassdev
 */
inline fun Kommand.buildSyntax(
    vararg arguments: Argument<*>,
    block: @KommandDSL KommandSyntaxBuilder.() -> Unit
) =
    KommandSyntaxBuilder(arguments = arguments.toMutableList()).let {
        it.block()
        syntaxes.add(it.build())
    }

/**
 * Extension function for `KommandBuilder` to build a command syntax using `SyntaxBuilder`
 *
 * @param block The `SyntaxBuilder` logic.
 * @author oglassdev
 */
inline fun Kommand.buildSyntax(block: @KommandDSL KommandSyntaxBuilder.() -> Unit) =
    KommandSyntaxBuilder().let {
        it.block()
        syntaxes.add(it.build())
    }
