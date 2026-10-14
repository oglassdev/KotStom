package net.bladehunt.kotstom.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import net.bladehunt.kotstom.dsl.EventBuilder
import net.minestom.server.event.Event
import kotlin.coroutines.CoroutineContext

/**
 * A coroutine handler that blocks until completion
 *
 * @param block The suspending code to run
 * @author oglassdev
 */
inline fun <T : Event> EventBuilder<T>.blockingHandler(crossinline block: suspend (T) -> Unit) = handler {
    runBlocking { block(it) }
}

/**
 * A coroutine handler that runs asynchronously in [scope]. Cancelling [scope] cancels running
 * handlers, and handler failures are reported to the `ExceptionManager` without cancelling [scope].
 *
 * @param scope The `CoroutineScope` that owns the launched coroutines
 * @param block The suspending code to run
 * @author oglassdev
 */
inline fun <T : Event> EventBuilder<T>.asyncHandler(
    scope: CoroutineScope,
    crossinline block: suspend (T) -> Unit
) = handler { scope.launchReporting { block(it) } }

/**
 * A coroutine handler that runs asynchronously using the `MinestomDispatcher`
 *
 * @param context The `CoroutineContext` to launch the coroutine in
 * @param block The suspending code to run
 * @author oglassdev
 */
inline fun <T : Event> EventBuilder<T>.asyncHandler(
    context: CoroutineContext = MinestomDispatcher,
    crossinline block: suspend (T) -> Unit
) = asyncHandler(CoroutineScope(context), block)
