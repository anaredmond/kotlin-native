package runtime.basic.random

import kotlin.test.*
import kotlin.util.Random

@Test
fun runTest() {
    println(konan.internal.random())
    konan.internal.srandom(Int.MAX_VALUE)
    println(konan.internal.random())

    val rng = Random()
    println(rng.nextInt())
    rng.seed = Int.MAX_VALUE
    println(rng.nextInt())
}