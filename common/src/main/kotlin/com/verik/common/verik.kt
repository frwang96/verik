@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.common

// Copyright (c) 2020 Francis Wang

// annotations
@Target(AnnotationTarget.CLASS)
annotation class top
@Target(AnnotationTarget.CLASS)
annotation class extern

@Target(AnnotationTarget.PROPERTY)
annotation class input
@Target(AnnotationTarget.PROPERTY)
annotation class output
@Target(AnnotationTarget.PROPERTY)
annotation class inoutput
@Target(AnnotationTarget.PROPERTY)
annotation class intf
@Target(AnnotationTarget.PROPERTY)
annotation class port

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
interface _circuit: _module

interface _intf: _component
infix fun <_T: _intf> _T.con(x: _T?) {}
infix fun <_T: _intf> _T.put(x: _T?) {}

interface _port
infix fun <_T: _port> _T.con(x: _T?) {}

class _group<_T: _component>(val RANGE: IntRange, val T: _T): Iterable<_T> {
    constructor(LEN: Int, type: _T): this(0..0, type)
    operator fun get(n: Int) = T
    operator fun get(n: _uint) = T
    operator fun get(range: IntRange) = this
    operator fun get(range: _range) = this

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

class _array<_T: _instance>(val RANGE: IntRange, val T: _T): _instance, Iterable<_T> {
    constructor(LEN: Int, type: _T): this(0..0, type)
    operator fun get(n: Int) = T
    operator fun get(n: _uint) = T
    operator fun get(range: IntRange) = this
    operator fun get(range: _range) = this

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


// verik commands
fun vk_random() = 0
fun vk_wait(n: Int = 1) {}
fun vk_wait(edge: _edge, n: Int = 1) {}
fun vk_print(message: String) {}
fun vk_println(message: String) {}
fun vk_error(message: String) {}
fun vk_finish() {}
