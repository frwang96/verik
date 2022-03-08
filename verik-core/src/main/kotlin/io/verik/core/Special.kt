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
 * Represents a value that is imported from SystemVerilog. It is generated by the importer and should not be used
 * directly.
 */
fun <T> imp(): T {
    throw VerikException()
}

/**
 * Inject [content] directly as a SystemVerilog statement. To inject an expression inline use [inji].
 */
fun inj(content: String) {
    throw VerikException()
}

/**
 * Inject [content] directly as an inline SystemVerilog expression. The injected expression has type [T].
 */
fun <T> inji(content: String): T {
    throw VerikException()
}

/**
 * Returns the SystemVerilog representation of type [T]. This should only be used in injected string literals.
 */
fun <T> t(): String {
    throw VerikException()
}

/**
 * Represents a value that is not connected in output port connections or property declarations.
 */
fun <T> nc(): T {
    throw VerikException()
}

/**
 * Returns the value of the cardinal [X] as a [Boolean]. [X] must take the value of either zero or one.
 */
fun <X : `*`> b(): Boolean {
    throw VerikException()
}

/**
 * Returns the value of cardinal [N] as an integer.
 */
fun <N : `*`> i(): Int {
    throw VerikException()
}

/**
 * Construct a [Ubit] out of the cardinal [N].
 */
fun <N : `*`> u(): Ubit<`*`> {
    throw VerikException()
}

/**
 * Construct a [Ubit] out of the constant [value].
 */
fun u(value: Int): Ubit<`*`> {
    throw VerikException()
}

/**
 * Construct a [Ubit] out of the constant [value].
 */
fun u(value: String): Ubit<`*`> {
    throw VerikException()
}

/**
 * Construct a [Ubit] with all bits set to false.
 */
fun <N : `*`> u0(): Ubit<N> {
    throw VerikException()
}

/**
 * Construct a [Ubit] with all bits set to true.
 */
fun <N : `*`> u1(): Ubit<N> {
    throw VerikException()
}

/**
 * Construct a [Ubit] with all bits set to unknown.
 */
fun <N : `*`> ux(): Ubit<N> {
    throw VerikException()
}

/**
 * Construct a [Ubit] with all bits set to floating.
 */
fun <N : `*`> uz(): Ubit<N> {
    throw VerikException()
}

/**
 * Construct a [Sbit] out of the constant [value].
 */
fun s(value: Int): Sbit<`*`> {
    throw VerikException()
}

/**
 * Construct a [Sbit] out of the constant [value].
 */
fun s(value: String): Sbit<`*`> {
    throw VerikException()
}

/**
 * Construct a [Sbit] with all bits set to false.
 */
fun <N : `*`> s0(): Sbit<N> {
    throw VerikException()
}

/**
 * Construct a [Sbit] with all bits set to true.
 */
fun <N : `*`> s1(): Sbit<N> {
    throw VerikException()
}

/**
 * (UNIMPLEMENTED) Construct a [Sbit] with all bits set to unknown.
 */
fun <N : `*`> sx(): Sbit<N> {
    throw VerikException()
}

/**
 * (UNIMPLEMENTED) Construct a [Sbit] with all bits set to floating.
 */
fun <N : `*`> sz(): Sbit<N> {
    throw VerikException()
}

/**
 * Construct a value of type [T] with all bits set to false.
 */
fun <T> fill0(): T {
    throw VerikException()
}

/**
 * Construct a value of type [T] with all bits set to true.
 */
fun <T> fill1(): T {
    throw VerikException()
}

/**
 * Construct a value of type [T] with all bits set to unknown.
 */
fun <T> fillx(): T {
    throw VerikException()
}

/**
 * Construct a value of type [T] with all bits set to floating.
 */
fun <T> fillz(): T {
    throw VerikException()
}

/**
 * (UNIMPLEMENTED) Pack [value] as a [Ubit].
 */
fun pack(value: Any): Ubit<`*`> {
    throw VerikException()
}

/**
 * (UNIMPLEMENTED) Unpack [value] to type [T].
 */
fun <T> unpack(value: Ubit<`*`>): T {
    throw VerikException()
}

/**
 * Construct a [Sequence] that evaluates [value] on the current cycle.
 */
fun q(value: Boolean): Sequence {
    throw VerikException()
}

/**
 * Construct a randomization constraint from [conditions].
 */
fun c(vararg conditions: Boolean): Constraint {
    throw VerikException()
}

/**
 * Construct a [CoverPoint] for [value].
 */
fun cp(value: Any): CoverPoint {
    throw VerikException()
}

/**
 * Construct a [CoverPoint] for [value] with configuration [config].
 */
fun cp(value: Any, config: CoverPoint.(CoverPoint) -> Unit): CoverPoint {
    throw VerikException()
}

/**
 * Construct a [CoverCross] for the cover points.
 */
fun cc(vararg cover_point: CoverPoint): CoverCross {
    throw VerikException()
}

/**
 * Construct a [CoverCross] for the cover points with configuration [config].
 */
fun cc(vararg cover_point: CoverPoint, config: CoverPoint.(CoverPoint) -> Unit): CoverCross {
    throw VerikException()
}
