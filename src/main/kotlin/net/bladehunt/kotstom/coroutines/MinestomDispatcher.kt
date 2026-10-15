package net.bladehunt.kotstom.coroutines

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Delay
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import net.bladehunt.kotstom.SchedulerManager
import net.minestom.server.MinecraftServer
import net.minestom.server.timer.TaskSchedule
import kotlin.coroutines.CoroutineContext

/**
 * A `CoroutineDispatcher` that runs coroutines at the start of Minestom ticks using the
 * `SchedulerManager`. Delays and timeouts are counted in whole ticks, rounding up. Note: These are
 * not properly 'asynchronous.' If you need async, use Dispatchers.Default.
 *
 * @author oglassdev
 */
@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
object MinestomDispatcher : CoroutineDispatcher(), Delay {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        SchedulerManager.execute(block)
    }

    override fun scheduleResumeAfterDelay(
        timeMillis: Long,
        continuation: CancellableContinuation<Unit>
    ) {
        val resume = ResumeTask(continuation)
        val task = SchedulerManager.buildTask(resume).delay(ticksFor(timeMillis)).schedule()
        continuation.invokeOnCancellation {
            // Cancelled tasks stay queued until their tick, so release the continuation now
            resume.continuation = null
            task.cancel()
        }
    }

    override fun invokeOnTimeout(
        timeMillis: Long,
        block: Runnable,
        context: CoroutineContext
    ): DisposableHandle {
        val task = SchedulerManager.buildTask(block).delay(ticksFor(timeMillis)).schedule()
        return DisposableHandle { task.cancel() }
    }

    private fun ticksFor(timeMillis: Long): TaskSchedule {
        val ticks = timeMillis / MinecraftServer.TICK_MS + if (timeMillis % MinecraftServer.TICK_MS == 0L) 0 else 1
        return TaskSchedule.tick(ticks.coerceIn(1, Int.MAX_VALUE.toLong()).toInt())
    }

    private class ResumeTask(@Volatile var continuation: CancellableContinuation<Unit>?) : Runnable {
        override fun run() {
            continuation?.run { resumeUndispatched(Unit) }
        }
    }
}

/**
 * Delays the coroutine for [ticks] server ticks
 *
 * @author oglassdev
 */
suspend fun delayTicks(ticks: Int) = delay(ticks.toLong() * MinecraftServer.TICK_MS)
