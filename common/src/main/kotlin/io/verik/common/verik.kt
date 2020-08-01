/*
 * Copyright 2020 Francis Wang
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

package io.verik.common

import io.verik.common.types.*

// annotations
@Target(AnnotationTarget.CLASS)
annotation class top

@Target(AnnotationTarget.PROPERTY)
annotation class input
@Target(AnnotationTarget.PROPERTY)
annotation class output
@Target(AnnotationTarget.PROPERTY)
annotation class inout
@Target(AnnotationTarget.PROPERTY)
annotation class intf
@Target(AnnotationTarget.PROPERTY)
annotation class iport

@Target(AnnotationTarget.PROPERTY)
annotation class comp

@Target(AnnotationTarget.PROPERTY)
annotation class wire
@Target(AnnotationTarget.PROPERTY)
annotation class rand

@Target(AnnotationTarget.FUNCTION)
annotation class put
@Target(AnnotationTarget.FUNCTION)
annotation class reg
@Target(AnnotationTarget.FUNCTION)
annotation class drive
@Target(AnnotationTarget.FUNCTION)
annotation class initial

@Target(AnnotationTarget.FUNCTION)
annotation class task


// components
interface _component
infix fun <_T: _component> _T.with(block: (_T) -> Unit) = this

interface _module: _component

interface _intf: _component
infix fun <_T: _intf> _T.con(x: _T?) {}
infix fun <_T: _intf> _T.put(x: _T?) {}

interface _iport
infix fun <_T: _iport> _T.con(x: _T?) {}

class _group<_T: _component>(val LEN: Int, val T: _T): Iterable<_T> {
    operator fun get(n: Int) = T
    operator fun get(n: _uint) = T

    override fun iterator() = _iterator()
    inner class _iterator: Iterator<_T> {
        override fun hasNext() = false
        override fun next() = T
    }
}
infix fun <_T: _component> _group<_T>.with(block: (_group<_T>) -> Unit) = this


// instances
interface _instance
infix fun <_T: _instance> _T.put(x: _T?) {}
infix fun <_T: _instance> _T.reg(x: _T?) {}
infix fun <_T: _instance> _T.drive(x: _T?) {}
infix fun <_T: _instance> _T.with(block: (_T) -> Unit) = this

interface _object: _instance
fun _object.is_null() = false
fun _object.randomize() {}
fun <_T: _object> _T.randomize(block: _T.() -> Unit) {}

class _array<_T: _instance>(val LEN: Int, val T: _T): _instance, Iterable<_T> {
    operator fun get(n: Int) = T
    operator fun get(n: _uint) = T

    override fun iterator() = _iterator()
    inner class _iterator: Iterator<_T> {
        override fun hasNext() = false
        override fun next() = T
    }
}
fun <_T: _instance> array(x: _T, vararg y: _T) = _array(0, x)
fun <_T: _instance> array(LEN: Int, x: _T) = _array(0, x)
fun <_T: _instance> _array<_T>.for_each(block: (_T) -> Unit) {}
fun <_T: _instance> _array<_T>.is_unknown() = false
fun <_T: _instance> _array<_T>.is_floating() = false
infix fun <_T: _instance> _array<_T>.con(x: _array<_T>?) {}
fun <_T: _instance> _array<_T>.pack() = _uint(0)


// utilities
fun log(x: Int) = 0
fun exp(x: Int) = 0
fun min(x: Int, vararg y: Int) = 0
fun max(x: Int, vararg y: Int) = 0


// control flow
interface _edge
fun posedge(x: _bool) = object: _edge {}
fun negedge(x: _bool) = object: _edge {}

fun on(x: _edge, vararg y: _edge, block: (Unit) -> Unit) {}
fun forever(block: (Unit) -> Unit) {}
fun repeat(times: _uint, action: (Unit) -> Unit) {}

fun wait(n: Int = 1) {}
fun wait(edge: _edge, n: Int = 1) {}


// system functions
fun random() = 0
fun finish() {}

enum class _severity {
    INFO,
    WARNING,
    ERROR,
    FATAL
}
fun log(severity: _severity, message: String) {}
