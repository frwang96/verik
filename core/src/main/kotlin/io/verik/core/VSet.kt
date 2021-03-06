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
 * A set of elements.
 * @param E the type of the set
 */
class VSet<E> : VCollection<E>() {

    /**
     * Adds the specified [element] to the set.
     */
    fun add(element: E) {
        throw VerikException()
    }

    /**
     * Removes the specified [element] from the set.
     */
    fun remove(element: E) {
        throw VerikException()
    }

    /**
     * Clears the contents of the set.
     */
    fun clear() {
        throw VerikException()
    }
}
