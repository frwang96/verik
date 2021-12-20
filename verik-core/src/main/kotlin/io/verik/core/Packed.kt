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

@file:Suppress("UNUSED_PARAMETER", "unused")

package io.verik.core

/**
 * A packed array of size [N] of elements of type [E].
 */
class Packed<N : `*`, E> private constructor() : Iterable<E> {

    override fun iterator(): Iterator<E> {
        throw VerikException()
    }

    /**
     * (UNIMPLEMENTED) The size of the array.
     */
    val size: Int by lazy<Int> {
        throw VerikException()
    }

    /**
     * (UNIMPLEMENTED) Returns the array element at the specified [index].
     */
    operator fun get(index: Int): E {
        throw VerikException()
    }

    /**
     * (UNIMPLEMENTED) Returns the array element at the specified [index].
     */
    operator fun get(index: Ubit<`*`>): E {
        throw VerikException()
    }

    /**
     * (UNIMPLEMENTED) Sets the array element at the specified [index] to the specified [element].
     */
    operator fun set(index: Int, element: E) {
        throw VerikException()
    }

    /**
     * (UNIMPLEMENTED) Sets the array element at the specified [index] to the specified [element].
     */
    operator fun set(index: Ubit<`*`>, element: E) {
        throw VerikException()
    }
}
