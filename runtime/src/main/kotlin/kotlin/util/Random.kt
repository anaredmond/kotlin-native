/*
 * Copyright 2010-2018 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kotlin.util

import konan.worker.AtomicInt
import konan.internal.*
import kotlin.system.*

/**
 * A default presudo-random generator.
 */
val random: Random = PosixSingletonRandom()

interface Random {
    var seed: Int
    /**
     * Retruns a next pseudo-random Int number.
     */
    fun nextInt(): Int

    /**
     * Retruns a next pseudo-random Long number.
     */
    fun nextLong(): Long
}

/**
 * A simple pseudo-random generator that relies on POSIX's random/srandom (LCD generator).
 * This implementation has a singleton generator. A sequence of generated numbers
 * and seed is shared between all workers and native threads.
 */
class PosixSingletonRandom() : Random {
    constructor(seed: Int) : this() {
        this.seed = seed
    }

    companion object {
        private var seed = AtomicInt(getTimeNanos().toInt())
        private var lock = AtomicInt(0)
    }

    /**
     * Random generator seed value.
     */
    override var seed: Int
        get() = Companion.seed.get()
        set(value) {
            Companion.seed = AtomicInt(value)
            doSpinLocked { srandom(Companion.seed.get()) }
        }

    private inline fun <T> doSpinLocked(body: () -> T): T {
        while (lock.compareAndSwap(expected = 0, new = 1) != 0) { /* yield */ }
        val result = body()
        lock.decrement()
        return result
    }

    /**
     * Retruns a next pseudo-random Int number.
     */
    override fun nextInt(): Int = doSpinLocked { random() }

    /**
     * Retruns a next pseudo-random Long number.
     */
    override fun nextLong(): Long = doSpinLocked {
        val h = random().toLong()
        val l = random().toLong()
        return@doSpinLocked (h shl 32) + l
    }
}