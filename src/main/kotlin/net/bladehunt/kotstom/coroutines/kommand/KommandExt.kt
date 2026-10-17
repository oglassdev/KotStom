package net.bladehunt.kotstom.coroutines.kommand

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import net.bladehunt.kotstom.command.Kommand
import net.bladehunt.kotstom.coroutines.MinestomDispatcher
import net.bladehunt.kotstom.coroutines.launchReporting
import net.bladehunt.kotstom.dsl.kommand.KommandDSL
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.CommandExecutor
import kotlin.coroutines.CoroutineContext

internal typealias SuspendKommandExecutor = suspend context(CommandContext) (CommandSender) -> Unit

/**
 * Sets the async default executor of the Kommand. Cancelling [scope] cancels running executions,
 * and failures are reported to the `ExceptionManager` without cancelling [scope].
 *
 * @param scope The `CoroutineScope` that owns the launched coroutines
 * @param block The suspending code to run
 * @author oglassdev
 */
inline fun Kommand.defaultExecutorAsync(
    scope: CoroutineScope,
    crossinline block: @KommandDSL SuspendKommandExecutor
) =
    CommandExecutor { sender, ctx ->
        scope.launchReporting { block(ctx, sender) }
    }
        .also { defaultExecutor = it }

/**
 * Sets the async default executor of the Kommand
 *
 * @param context The `CoroutineContext` to launch the coroutine in
 * @param block The suspending code to run
 * @author oglassdev
 */
inline fun Kommand.defaultExecutorAsync(
    context: CoroutineContext = MinestomDispatcher,
    crossinline block: @KommandDSL SuspendKommandExecutor
) = defaultExecutorAsync(CoroutineScope(context), block)

/**
 * Sets the blocking default executor of the Kommand
 *
 * @param block The suspending code to run
 * @author oglassdev
 */
inline fun Kommand.defaultExecutorBlocking(
    coroutineContext: CoroutineContext = Dispatchers.Default,
    crossinline block: @KommandDSL SuspendKommandExecutor
) =
    CommandExecutor { sender, ctx ->
        runBlocking(coroutineContext) { block(ctx, sender) }
    }
        .also { defaultExecutor = it }
