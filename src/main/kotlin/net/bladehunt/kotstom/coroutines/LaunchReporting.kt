package net.bladehunt.kotstom.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.bladehunt.kotstom.ExceptionManager
import kotlin.coroutines.cancellation.CancellationException

/**
 * Launches [block] in this scope, reporting failures to Minestom's `ExceptionManager` instead of
 * cancelling the scope.
 */
@PublishedApi
internal fun CoroutineScope.launchReporting(block: suspend CoroutineScope.() -> Unit): Job = launch {
    try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        ExceptionManager.handleException(e)
    }
}
