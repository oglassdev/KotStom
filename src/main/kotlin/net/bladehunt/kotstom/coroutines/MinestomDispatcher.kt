package net.bladehunt.kotstom.coroutines

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Delay
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.NonDisposableHandle
import kotlinx.coroutines.delay
import net.bladehunt.kotstom.SchedulerManager
import net.minestom.server.MinecraftServer
import net.minestom.server.timer.TaskSchedule
import kotlin.coroutines.CoroutineContext

/**
 * A `CoroutineDispatcher` that runs coroutines at the start of Minestom ticks using the
 * `SchedulerManager`. Delays and timeouts are counted in whole ticks, rounding up, so they only
 * advance while the server is ticking. Delays longer than [MAX_DELAY_TICKS] never resume. Note:
 * These are not properly 'asynchronous.' If you need async, use Dispatchers.Default.
 *
 * @author oglassdev
 */
@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
object MinestomDispatcher : CoroutineDispatcher(), Delay {
    /** Minestom stores task deadlines as an `Int` tick, so longer delays are treated as infinite */
    const val MAX_DELAY_TICKS: Long = Int.MAX_VALUE / 2L

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        SchedulerManager.execute(block)
    }

    override fun scheduleResumeAfterDelay(
        timeMillis: Long,
        continuation: CancellableContinuation<Unit>
    ) {
        val handle = schedule(timeMillis) { with(continuation) { resumeUndispatched(Unit) } }
        continuation.invokeOnCancellation { handle.dispose() }
    }

    override fun invokeOnTimeout(
        timeMillis: Long,
        block: Runnable,
        context: CoroutineContext
    ): DisposableHandle = schedule(timeMillis, block)

    private fun schedule(timeMillis: Long, block: Runnable): DisposableHandle {
        val ticks = timeMillis / MinecraftServer.TICK_MS + if (timeMillis % MinecraftServer.TICK_MS == 0L) 0 else 1
        if (ticks > MAX_DELAY_TICKS) return NonDisposableHandle
        val task = ReleasingTask(block)
        val scheduled = SchedulerManager.buildTask(task).delay(TaskSchedule.tick(ticks.coerceAtLeast(1).toInt())).schedule()
        return DisposableHandle {
            // Cancelled tasks stay queued until their tick, so release the block now
            task.block = null
            scheduled.cancel()
        }
    }
}

internal class ReleasingTask(@Volatile var block: Runnable?) : Runnable {
    override fun run() {
        block?.run()
    }
}

/**
 * Delays the coroutine for [ticks] server ticks
 *
 * @author oglassdev
 */
suspend fun delayTicks(ticks: Int) = delay(ticks.toLong() * MinecraftServer.TICK_MS)
