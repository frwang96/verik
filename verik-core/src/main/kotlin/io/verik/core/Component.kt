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
 * Components are statically instantiated hardware that may have input and output ports. They are declared with the
 * [@Make][Make] annotation.
 */
abstract class Component

/**
 * (UNIMPLEMENTED) Instantiate a [Cluster] of components [C] of size [X].
 */
fun <X : `*`, C : Component> cluster(instantiator: (Int) -> C): Cluster<X, C> {
    throw VerikException()
}

/**
 * Instantiate component [C] if the cardinal [X] takes the value one. [X] must take the value of either zero or one.
 */
fun <X : `*`, C : Component> optional(instantiator: () -> C): C? {
    throw VerikException()
}
