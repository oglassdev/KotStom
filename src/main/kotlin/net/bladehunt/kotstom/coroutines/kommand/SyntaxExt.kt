package net.bladehunt.kotstom.coroutines.kommand

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.bladehunt.kotstom.coroutines.MinestomDispatcher
import net.bladehunt.kotstom.dsl.kommand.KommandDSL
import net.bladehunt.kotstom.dsl.kommand.KommandSyntaxBuilder
import net.minestom.server.command.builder.CommandExecutor
import kotlin.coroutines.CoroutineContext

/**
 * Sets the async executor for the command
 *
 * @param block The executor logic
 * @return The command executor
 * @author oglassdev
 */
@KommandDSL
inline fun KommandSyntaxBuilder.executorAsync(
    context: CoroutineContext = MinestomDispatcher,
    crossinline block: @KommandDSL SuspendKommandExecutor
) =
    CommandExecutor { sender, ctx ->
        CoroutineScope(context).launch { block(ctx, sender) }
    }
        .also { executor = it }

/**
 * Sets the blocking executor for the command
 *
 * @param block The executor logic
 * @return The command executor
 * @author oglassdev
 */
@KommandDSL
inline fun KommandSyntaxBuilder.executorBlocking(
    crossinline block: @KommandDSL SuspendKommandExecutor
) =
    CommandExecutor { sender, ctx ->
        runBlocking { block(ctx, sender) }
    }
        .also { executor = it }
