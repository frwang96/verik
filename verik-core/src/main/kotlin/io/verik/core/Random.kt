/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("unused", "UNUSED_PARAMETER")

package io.verik.core

/**
 * Returns a random integer.
 */
fun random(): Int {
    throw VerikException()
}

/**
 * Returns a random integer from 0 to [max] exclusive.
 */
fun random(max: Int): Int {
    throw VerikException()
}

/**
 * Returns a random integer from [min] to [max] exclusive.
 */
fun random(min: Int, max: Int): Int {
    throw VerikException()
}

/**
 * Returns a random enum.
 * @param T the type of the enum
 */
fun <T : Enum<T>> randomEnum(): T {
    throw VerikException()
}

/**
 * Returns a random [Ubit] of width [N].
 */
fun <N : `*`> randomUbit(): Ubit<N> {
    throw VerikException()
}
