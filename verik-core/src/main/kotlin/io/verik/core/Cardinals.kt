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

@file:Suppress("UNUSED_TYPEALIAS_PARAMETER", "unused")

package io.verik.core

sealed class Cardinal

typealias `*` = Cardinal

/**
 * True cardinal with value one.
 */
typealias TRUE = `*`

/**
 * False cardinal with value zero.
 */
typealias FALSE = `*`

/**
 * Logical not of the cardinal X.
 */
typealias NOT<X> = `*`

/**
 * Logical and of the cardinals X and Y.
 */
typealias AND<X, Y> = `*`

/**
 * Logical or of the cardinals X and Y.
 */
typealias OR<X, Y> = `*`

/**
 * Evaluate as the cardinal Y if the cardinal X is one and the cardinal Z otherwise. X must take the value of either
 * zero or one.
 */
typealias IF<X, Y, Z> = `*`

/**
 * Adds the cardinals X and Y.
 */
typealias ADD<X, Y> = `*`

/**
 * Subtracts the cardinals X and Y.
 */
typealias SUB<X, Y> = `*`

/**
 * Multiplies the cardinals X and Y.
 */
typealias MUL<X, Y> = `*`

/**
 * Divides the cardinals X and Y
 */
typealias DIV<X, Y> = `*`

/**
 * Takes the maximum of the cardinals X and Y.
 */
typealias MAX<X, Y> = `*`

/**
 * Takes the minimum of the cardinals X and Y.
 */
typealias MIN<X, Y> = `*`

/**
 * The cardinal X. Used for shielding the type parameter from the Kotlin type system.
 */
typealias OF<X> = `*`

/**
 * Increments the cardinal X.
 */
typealias INC<X> = `*`

/**
 * Decrements the cardinal X.
 */
typealias DEC<X> = `*`

/**
 * Ceiling logarithm base two of the cardinal X.
 */
typealias LOG<X> = `*`

/**
 * Width of the binary representation of the cardinal X. Equivalent to `LOG<INC<X>>`.
 */
typealias WIDTH<X> = `*`

/**
 * Exponential base two of the cardinal X.
 */
typealias EXP<X> = `*`
