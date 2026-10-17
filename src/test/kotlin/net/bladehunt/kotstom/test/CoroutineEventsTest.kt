package net.bladehunt.kotstom.test

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job
import net.bladehunt.kotstom.coroutines.asyncHandler
import net.bladehunt.kotstom.dsl.builder
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode

private class TestEvent : Event

class CoroutineEventsTest :
    FunSpec({
        beforeAny { SERVER }
        test("a failing async handler doesn't cancel its scope") {
            val scope = CoroutineScope(Job() + Dispatchers.Unconfined)
            val node = EventNode.all("failing")
            node.builder<TestEvent> { asyncHandler(scope) { error("boom") } }

            node.call(TestEvent())

            scope.isActive shouldBe true
        }
        test("cancelling the scope cancels running async handlers") {
            val scope = CoroutineScope(Job() + Dispatchers.Unconfined)
            val node = EventNode.all("cancelling")
            node.builder<TestEvent> { asyncHandler(scope) { delay(60_000) } }

            node.call(TestEvent())
            scope.cancel()

            scope.coroutineContext.job.children.all { it.isCancelled } shouldBe true
        }
    })
