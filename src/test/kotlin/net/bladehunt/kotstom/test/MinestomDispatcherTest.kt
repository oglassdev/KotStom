package net.bladehunt.kotstom.test

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import net.bladehunt.kotstom.SchedulerManager
import net.bladehunt.kotstom.coroutines.MinestomDispatcher
import net.bladehunt.kotstom.coroutines.delayTicks
import net.bladehunt.kotstom.extension.await
import net.minestom.server.timer.TaskSchedule

private fun tick() {
    SchedulerManager.processTick()
    SchedulerManager.processTickEnd()
}

private fun ticksUntilDone(job: Job, max: Int = 10): Int {
    var ticks = 0
    while (!job.isCompleted && ticks < max) {
        tick()
        ticks++
    }
    return ticks
}

class MinestomDispatcherTest :
    FunSpec({
        beforeSpec { SERVER }

        val scope = CoroutineScope(SupervisorJob())
        afterSpec { scope.cancel() }

        fun launch(block: suspend CoroutineScope.() -> Unit) =
            scope.launch(MinestomDispatcher, CoroutineStart.UNDISPATCHED, block)

        test("delays round up to whole ticks") {
            ticksUntilDone(launch { delay(1) }) shouldBe 1
            ticksUntilDone(launch { delay(50) }) shouldBe 1
            ticksUntilDone(launch { delay(51) }) shouldBe 2
            ticksUntilDone(launch { delayTicks(3) }) shouldBe 3
        }

        test("withTimeoutOrNull times out") {
            var result: Any? = "pending"
            val job = launch { result = withTimeoutOrNull(100) { awaitCancellation() } }
            ticksUntilDone(job) shouldBe 3
            result shouldBe null
        }

        test("huge delays never fire") {
            val jobs = listOf(launch { delay(Long.MAX_VALUE - 1) }, launch { delayTicks(Int.MAX_VALUE) })
            repeat(5) { tick() }
            jobs.forEach { it.isActive shouldBe true }
            jobs.forEach { it.cancel() }
        }

        test("cancelled delay and await complete without resuming") {
            var resumed = false
            val jobs =
                listOf(
                    launch { delay(1_000).also { resumed = true } },
                    launch { SchedulerManager.await(TaskSchedule.tick(20)).also { resumed = true } },
                )
            jobs.forEach { it.cancel() }
            repeat(25) { tick() }
            jobs.forEach { it.isCompleted shouldBe true }
            resumed shouldBe false
        }
    })
