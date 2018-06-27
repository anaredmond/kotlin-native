package runtime.basic.worker_random

import kotlin.test.*
import kotlin.random.*
import konan.worker.*

@Test
fun testRandomWorkers() {
    val COUNT = 5
    val workers = Array(COUNT, { _ -> startWorker()})

    for (attempt in 1 .. 3) {
        val futures = Array(workers.size, { workerIndex ->
            workers[workerIndex].schedule(TransferMode.CHECKED, { workerIndex }) { input ->
                IntArray(input * 1000, { Random.nextInt() })
            }
        })
        val futureSet = futures.toSet()
        var consumed = ArrayList<Int>()
        for (i in 0 .. futureSet.size - 1) {
            val ready = futureSet.waitForMultipleFutures(10000)
            ready.forEach {
                it.consume { result -> result.forEach { consumed.add(it) } }
            }
        }
    }

    workers.forEach {
        it.requestTermination().consume { _ -> }
    }

    println("OK")
}