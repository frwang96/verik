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
 * A queue of elements.
 * @param E the type of the elements of the queue
 */
class VQueue<E>: VCollection<E>() {

    /**
     * Adds the specified [element] to the back of the queue.
     */
    fun add(element: E) {
        throw VerikException()
    }

    /**
     * Peeks at the front of the queue.
     */
    fun peek(): E {
        throw VerikException()
    }

    /**
     * Polls from the front of the queue.
     */
    fun poll(): E {
        throw VerikException()
    }

    /**
     * Clear the contents of the queue.
     */
    fun clear() {
        throw VerikException()
    }
}
