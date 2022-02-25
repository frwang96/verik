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
 * Represents a value which is either true or false.
 */
class Boolean private constructor() {

    /**
     * Returns the inverse of this boolean.
     */
    operator fun not(): Boolean {
        throw VerikException()
    }

    /**
     * Performs a logical and operation between this Boolean and the [other] one. Unlike the `&&` operator, this
     * function does not perform short-circuit evaluation. Both this and [other] will always be evaluated.
     */
    infix fun and(other: Boolean): Boolean {
        throw VerikException()
    }

    /**
     * Performs a logical or operation between this Boolean and the [other] one. Unlike the `||` operator, this function
     * does not perform short-circuit evaluation. Both this and [other] will always be evaluated.
     */
    infix fun or(other: Boolean): Boolean {
        throw VerikException()
    }

    /**
     * Performs a logical xor operation between this Boolean and the [other] one.
     */
    infix fun xor(other: Boolean): Boolean {
        throw VerikException()
    }
}
