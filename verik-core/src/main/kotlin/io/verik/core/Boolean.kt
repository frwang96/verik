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

@file:Suppress("unused")

package io.verik.core

import kotlin.properties.Delegates

/**
 * (UNIMPLEMENTED) Returns true if the boolean is unknown.
 */
fun Boolean.isx(): Boolean {
    throw VerikException()
}

/**
 * (UNIMPLEMENTED) Returns true if the boolean is floating.
 */
fun Boolean.isz(): Boolean {
    throw VerikException()
}

/**
 * (UNIMPLEMENTED) Returns true if the boolean is unknown or floating.
 */
fun Boolean.isxz(): Boolean {
    throw VerikException()
}

/**
 * Converts this value to a [Ubit] of width [N]. The value is extended with zero extension.
 */
fun <N : `*`> Boolean.toUbit(): Ubit<N> {
    throw VerikException()
}

/**
 * Converts this value to an [Sbit] of width [N]. The value is extended with sign extension.
 */
fun <N : `*`> Boolean.toSbit(): Sbit<N> {
    throw VerikException()
}

/**
 * Unknown boolean value.
 */
val unknown: Boolean by Delegates.observable(false) { _, _, _ ->
    throw VerikException()
}

/**
 * Floating boolean value.
 */
val floating: Boolean by Delegates.observable(false) { _, _, _ ->
    throw VerikException()
}
