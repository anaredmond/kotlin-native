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
package kotlin.random

import konan.internal.*
import konan.worker.AtomicInt
import kotlin.system.getTimeNanos

abstract class Random {
    abstract fun nextInt(): Int

    abstract fun nextLong(): Long

    /**
     * A default pseudo-random generator that relies on POSIX's random/srandom (LCD generator).
     * This implementation is a singleton generator. A sequence of generated numbers
     * and seed is shared between all workers and native threads.
     */
    companion object : Random() {
        private var lock = AtomicInt(0)
        private var _seed = AtomicInt(getTimeNanos().toInt())

        init { updateSeed() }

        /**
         * Random generator seed value.
         */
        var seed: Int
            get() = _seed.get()
            set(value) {
                _seed = AtomicInt(value)
                updateSeed()
            }
        private inline fun updateSeed() = doSpinLocked { srandom(_seed.get()) }

        private inline fun <T> doSpinLocked(body: () -> T): T {
            while (lock.compareAndSwap(expected = 0, new = 1) != 0) { /* yield */ }
            val result = body()
            lock.decrement()
            return result
        }

        /**
         * Returns a pseudo-random Int number.
         */
        override fun nextInt(): Int = doSpinLocked { random() }

        /**
         * Returns a pseudo-random Long number.
         */
        override fun nextLong(): Long = doSpinLocked { (random().toLong() shl 32) + random().toLong() }
    }
}