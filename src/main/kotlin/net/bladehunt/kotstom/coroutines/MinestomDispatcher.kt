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
import net.minestom.server.timer.Schedulable
import net.minestom.server.timer.Scheduler
import net.minestom.server.timer.TaskSchedule
import kotlin.coroutines.CoroutineContext

/**
 * A `CoroutineDispatcher` that runs coroutines when [scheduler] processes its next tick. Delays and
 * timeouts are counted in whole ticks, rounding up, so they only advance while [scheduler] is being
 * ticked. Coroutines on the scheduler of a removed entity or unregistered instance never resume.
 * Delays longer than [MAX_DELAY_TICKS] never resume. Note: These are not properly 'asynchronous.'
 * If you need async, use Dispatchers.Default.
 *
 * @author oglassdev
 */
@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
abstract class SchedulerDispatcher : CoroutineDispatcher(), Delay {
    abstract val scheduler: Scheduler

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        scheduler.execute(block)
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
        val scheduled = scheduler.buildTask(task).delay(TaskSchedule.tick(ticks.coerceAtLeast(1).toInt())).schedule()
        return DisposableHandle {
            // Cancelled tasks stay queued until their tick, so release the block now
            task.block = null
            scheduled.cancel()
        }
    }

    // Equal dispatchers let withContext between them skip the extra tick
    override fun equals(other: Any?): Boolean = other is SchedulerDispatcher && other.scheduler === scheduler

    override fun hashCode(): Int = System.identityHashCode(scheduler)

    override fun toString(): String = "SchedulerDispatcher[$scheduler]"

    companion object {
        /** Minestom stores task deadlines as an `Int` tick, so longer delays are treated as infinite */
        const val MAX_DELAY_TICKS: Long = Int.MAX_VALUE / 2L
    }
}

/**
 * The [SchedulerDispatcher] for the server's `SchedulerManager`, running coroutines at the start of
 * server ticks
 *
 * @author oglassdev
 */
object MinestomDispatcher : SchedulerDispatcher() {
    override val scheduler: Scheduler
        get() = SchedulerManager

    override fun toString(): String = "MinestomDispatcher"
}

/** Returns a [SchedulerDispatcher] that runs coroutines and counts delays on this scheduler's ticks */
fun Scheduler.asCoroutineDispatcher(): SchedulerDispatcher = ScheduledDispatcher(this)

/** Returns a [SchedulerDispatcher] for this entity's or instance's own scheduler */
fun Schedulable.asCoroutineDispatcher(): SchedulerDispatcher = scheduler().asCoroutineDispatcher()

private class ScheduledDispatcher(override val scheduler: Scheduler) : SchedulerDispatcher()

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
