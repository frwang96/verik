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

@file:Suppress("unused", "UNUSED_TYPEALIAS_PARAMETER")

package io.verik.core

sealed class Logical

typealias `?` = Logical

/**
 * Returns the value of the logical [X] as a boolean.
 */
fun <X : `?`> b(): Boolean {
    throw VerikException()
}

/**
 * Logical true value.
 */
typealias TRUE = `?`

/**
 * Logical false value.
 */
typealias FALSE = `?`

/**
 * Inverts the logical X.
 */
typealias NOT<X> = `?`

/**
 * Takes the conjunction of the logicals X and Y.
 */
typealias AND<X, Y> = `?`

/**
 * Takes the disjunction of the logicals X and Y.
 */
typealias OR<X, Y> = `?`
