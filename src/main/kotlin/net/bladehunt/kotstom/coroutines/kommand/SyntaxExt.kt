package net.bladehunt.kotstom.coroutines.kommand

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import net.bladehunt.kotstom.coroutines.MinestomDispatcher
import net.bladehunt.kotstom.coroutines.launchReporting
import net.bladehunt.kotstom.dsl.kommand.KommandDSL
import net.bladehunt.kotstom.dsl.kommand.KommandSyntaxBuilder
import net.minestom.server.command.builder.CommandExecutor
import kotlin.coroutines.CoroutineContext

/**
 * Sets the async executor for the command. Cancelling [scope] cancels running executions, and
 * failures are reported to the `ExceptionManager` without cancelling [scope].
 *
 * @param scope The `CoroutineScope` that owns the launched coroutines
 * @param block The executor logic
 * @return The command executor
 * @author oglassdev
 */
inline fun KommandSyntaxBuilder.executorAsync(
    scope: CoroutineScope,
    crossinline block: @KommandDSL SuspendKommandExecutor
) =
    CommandExecutor { sender, ctx ->
        scope.launchReporting { block(ctx, sender) }
    }
        .also { executor = it }

/**
 * Sets the async executor for the command
 *
 * @param block The executor logic
 * @return The command executor
 * @author oglassdev
 */
inline fun KommandSyntaxBuilder.executorAsync(
    context: CoroutineContext = MinestomDispatcher,
    crossinline block: @KommandDSL SuspendKommandExecutor
) = executorAsync(CoroutineScope(context), block)

/**
 * Sets the blocking executor for the command
 *
 * @param block The executor logic
 * @return The command executor
 * @author oglassdev
 */
inline fun KommandSyntaxBuilder.executorBlocking(
    crossinline block: @KommandDSL SuspendKommandExecutor
) =
    CommandExecutor { sender, ctx ->
        runBlocking { block(ctx, sender) }
    }
        .also { executor = it }
