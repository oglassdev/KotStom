package net.bladehunt.kotstom.extension

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import net.minestom.server.timer.ExecutionType
import net.minestom.server.timer.Schedulable
import net.minestom.server.timer.Scheduler
import net.minestom.server.timer.TaskSchedule

/** Suspends a coroutine until a delay is met */
suspend fun Scheduler.await(
    delay: TaskSchedule,
    executionType: ExecutionType = ExecutionType.TICK_START
): Unit = suspendCancellableCoroutine { continuation ->
    val task = buildTask { continuation.resume(Unit) }.delay(delay).executionType(executionType).schedule()
    continuation.invokeOnCancellation { task.cancel() }
}

/** Suspends a coroutine until a delay is met */
suspend fun Schedulable.await(
    delay: TaskSchedule,
    executionType: ExecutionType = ExecutionType.TICK_START
) = scheduler().await(delay, executionType)
