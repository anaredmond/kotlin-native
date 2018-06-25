package runtime.basic.random

import kotlin.test.*
import kotlin.util.*

@Test
fun runTest() {
    println(konan.internal.random())
    konan.internal.srandom(Int.MAX_VALUE)
    println(konan.internal.random())

    val rng = PosixSingletonRandom()
    println(rng.nextInt())
    rng.seed = Int.MAX_VALUE
    println(rng.nextInt())

    println(random.nextLong())
}