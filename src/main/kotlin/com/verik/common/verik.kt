@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.common

// Copyright (c) 2020 Francis Wang

// annotations
@Target(AnnotationTarget.CLASS)
annotation class main
@Target(AnnotationTarget.CLASS)
annotation class test
@Target(AnnotationTarget.CLASS)
annotation class extern

@Target(AnnotationTarget.PROPERTY)
annotation class input
@Target(AnnotationTarget.PROPERTY)
annotation class output
@Target(AnnotationTarget.PROPERTY)
annotation class intf
@Target(AnnotationTarget.PROPERTY)
annotation class port

@Target(AnnotationTarget.PROPERTY)
annotation class def

@Target(AnnotationTarget.PROPERTY)
annotation class rand

@Target(AnnotationTarget.FUNCTION)
annotation class comb
@Target(AnnotationTarget.FUNCTION)
annotation class seq
@Target(AnnotationTarget.FUNCTION)
annotation class initial

@Target(AnnotationTarget.FUNCTION)
annotation class task


// components
interface _component
infix fun <T: _component> T.with(block: (T) -> Unit) = this

interface _module: _component
interface _circuit: _module

interface _intf: _component
infix fun <T: _intf> T.con(x: T?) {}
infix fun <T: _intf> T.set(x: T?) {}

interface _port
infix fun <T: _port> T.con(x: T?) {}

class _group<T: _component>(val range: IntRange, val type: T): Iterable<T> {
    constructor(len: Int, type: T): this(0..0, type)
    operator fun get(n: Int) = type
    operator fun get(n: _uint) = type
    operator fun get(range: IntRange) = this
    operator fun get(range: _range) = this

    override fun iterator() = _iterator()
    inner class _iterator: Iterator<T> {
        override fun hasNext() = false
        override fun next() = type
    }
}
infix fun <T: _component> _group<T>.with(block: (_group<T>) -> Unit) = this


// instances
interface _instance
infix fun <T: _instance> T.set(x: T?) = this
infix fun <T: _instance> T.put(x: T?) {}
infix fun <T: _instance> T.apply(block: T.() -> Unit) = this

interface _object: _instance
fun _object.is_null() = false
fun _object.new() {}
fun _object.randomize() {}
fun <T: _object> T.randomize(block: T.() -> Unit){}

class _array<T: _instance>(val range: IntRange, val type: T): _instance, Iterable<T> {
    constructor(len: Int, type: T): this(0..0, type)
    operator fun get(n: Int) = type
    operator fun get(n: _uint) = type
    operator fun get(range: IntRange) = this
    operator fun get(range: _range) = this

    override fun iterator() = _iterator()
    inner class _iterator: Iterator<T> {
        override fun hasNext() = false
        override fun next() = type
    }
}
fun <T: _instance> array(x: T, vararg y: T) = _array(0, x)
fun <T: _instance> array(n: Int, x: T) = _array(0, x)
operator fun <T: _instance> _array<T>.plus(x: _array<T>) = x
infix fun <T: _instance> _array<T>.for_each(block: (T) -> Unit) {}
infix fun <T: _instance> _array<T>.con(x: _array<T>?) {}
fun <T: _instance> _array<T>.pack() = _uint(0)


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
fun vk_wait(n: Int) {}
fun vk_wait_on(edge: _edge, n: Int = 1) {}
fun vk_literal(string: String) {}
fun vk_display(message: String) {}
fun vk_write(message: String) {}
fun vk_error(message: String) {}
fun vk_finish() {}
