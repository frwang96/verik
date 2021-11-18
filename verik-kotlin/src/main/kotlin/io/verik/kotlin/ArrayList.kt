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

package io.verik.kotlin

/**
 * Resizable array of elements of type [E].
 */
class ArrayList<E> {

    /**
     * Appends element [e] to the end of this list.
     */
    fun add(e: E) {
        throw IllegalArgumentException()
    }

    /**
     * Returns the element at the specified [index] in this list.
     */
    fun get(index: Int): E {
        throw IllegalArgumentException()
    }

    /**
     * Replaces the element at the specified [index] in this list.
     */
    fun set(index: Int, element: E) {
        throw IllegalArgumentException()
    }

    /**
     * Returns the number of elements in this list.
     */
    val size: Int by lazy<Int> {
        throw IllegalArgumentException()
    }
}
