package runtime.basic.random

import kotlin.test.*
import kotlin.random.*
import kotlin.system.*

private inline fun <reified T> getRandomNumbers(producer: () -> T): List<T> {
    val list = ArrayList<T>()
    for (i in 0 .. 4) {
        list.add(producer())
    }
    return list
}

/**
 * Tests that setting the same seed make random generate the same sequence
 */
private inline fun <reified T> testReproducibility(seed: Int, producer: () -> T) {
    // Reset seed. This will make Random to start a new sequence
    Random.seed = seed
    val first: List<T> = getRandomNumbers(producer)

    // reset seed and try again
    Random.seed = seed
    val second: List<T> = getRandomNumbers(producer)
    assertTrue(first == second, "FAIL: got different sequences of generated values " +
            "first: $first, second: $second")
}

/**
 * Tests that setting seed makes random generate different sequence.
 */
private inline fun <reified T> testDifference(producer: () -> T) {
    Random.seed = 12345678
    val first: List<T> = getRandomNumbers(producer)

    Random.seed = 87654321
    val second: List<T> = getRandomNumbers(producer)
    assertTrue(first != second, "FAIL: got the same sequence of generated values " +
            "first: $first, second: $second")
}

@Test
fun testInts() {
    testReproducibility(getTimeMillis().toInt(),  { Random.nextInt() })
    testReproducibility(Int.MAX_VALUE, { Random.nextInt() })
}

@Test
fun testLong() {
    testReproducibility(getTimeMillis().toInt(), { Random.nextLong() })
    testReproducibility(Int.MAX_VALUE, { Random.nextLong() })
}

@Test
fun testDiffInt() = testDifference { Random.nextInt() }

@Test
fun testDiffLong() = testDifference { Random.nextLong() }