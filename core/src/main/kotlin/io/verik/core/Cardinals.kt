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

/**
 * Base type representing the cardinals. Cardinals may be specified with numbers enclosed in backticks, such as
 * ``1`` or ``256``.
 */
sealed class Cardinal

/**
 * Returns the value of cardinal [N] as an integer.
 */
fun <N : Cardinal> i(): Int {
    throw VerikException()
}

/**
 * Adds the cardinals N and M.
 */
typealias ADD<N, M> = Cardinal

/**
 * Subtracts the cardinals N and M.
 */
typealias SUB<N, M> = Cardinal

/**
 * Multiplies the cardinals N and M.
 */
typealias MUL<N, M> = Cardinal

/**
 * Takes the maximum of the cardinals N and M.
 */
typealias MAX<N, M> = Cardinal

/**
 * Takes the minimum of the cardinals N and M.
 */
typealias MIN<N, M> = Cardinal

/**
 * Increments the cardinal N.
 */
typealias INC<N> = Cardinal

/**
 * Decrements the cardinal N.
 */
typealias DEC<N> = Cardinal

/**
 * Takes the ceiling logarithm base two of the cardinal N.
 */
typealias LOG<N> = Cardinal

/**
 * Takes the exponential base two of the cardinal N.
 */
typealias EXP<N> = Cardinal
