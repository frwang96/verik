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
 * Adds the cardinals N and M.
 */
typealias ADD<N, M> = `*`

/**
 * Subtracts the cardinals N and M.
 */
typealias SUB<N, M> = `*`

/**
 * Multiplies the cardinals N and M.
 */
typealias MUL<N, M> = `*`

/**
 * Divides the cardinals N and M
 */
typealias DIV<N, M> = `*`

/**
 * Takes the maximum of the cardinals N and M.
 */
typealias MAX<N, M> = `*`

/**
 * Takes the minimum of the cardinals N and M.
 */
typealias MIN<N, M> = `*`

/**
 * The cardinal N. Used for shielding the type parameter from the Kotlin type system.
 */
typealias OF<N> = `*`

/**
 * Increments the cardinal N.
 */
typealias INC<N> = `*`

/**
 * Decrements the cardinal N.
 */
typealias DEC<N> = `*`

/**
 * Ceiling logarithm base two of the cardinal N.
 */
typealias LOG<N> = `*`

/**
 * Width of the binary representation of the cardinal N. Equivalent to `LOG<INC<N>>`.
 */
typealias WIDTH<N> = `*`

/**
 * Exponential base two of the cardinal N.
 */
typealias EXP<N> = `*`
