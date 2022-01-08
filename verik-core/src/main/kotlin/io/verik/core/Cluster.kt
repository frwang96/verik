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
 * (UNIMPLEMENTED) A cluster of components [C] of size [X].
 */
class Cluster<X : `*`, C : Component> private constructor() : Component(), Iterable<C> {

    override fun iterator(): Iterator<C> {
        throw VerikException()
    }

    /**
     * (UNIMPLEMENTED) Returns the component at the specified [index].
     */
    operator fun get(index: Int): C {
        throw VerikException()
    }
}
