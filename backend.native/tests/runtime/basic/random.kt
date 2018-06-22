package runtime.basic.random

import kotlin.test.*

@Test
fun runTest() {
    println(konan.internal.random())
    konan.internal.srandom(Int.MAX_VALUE)
    println(konan.internal.random())
}