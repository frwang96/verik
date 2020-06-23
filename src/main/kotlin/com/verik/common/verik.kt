@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.common

// Copyright (c) 2020 Francis Wang

// Annotations
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


// Components
interface _module
interface _circuit: _module
infix fun <T: _module> T.with(block: (T) -> Unit) = this

interface _intf
infix fun <T: _intf> T.with(block: (T) -> Unit) = this
infix fun <T: _intf> T.con(x: T?) {}
infix fun <T: _intf> T.set(x: T?) = this

interface _port
infix fun <T: _port> T.con(x: T?) {}

interface _class
fun _class.new() {}
fun _class.is_null() = false
fun _class.randomize() {}
infix fun <T: _class> T.apply(block: T.() -> Unit) = this
infix fun <T: _class> T.set(x: T?) = this
fun <T: _class> T.randomize(block: T.() -> Unit){}


// Collections
class _array<T>(val range: IntRange, val type: T) {
    constructor(len: Int, type: T): this(0..0, type)
    operator fun get(n: Int) = type
    operator fun get(n: _bits) = type
    operator fun get(n: _uint) = type
    operator fun get(range: IntRange) = this
    companion object {
        fun <T> of(x: T, vararg y: T) = _array(0, x)
        fun <T> rep(x: T) = _array(0, x)
    }
}
infix fun <T> _array<T>.for_each(block: (T) -> Unit) {}
infix fun <T> _array<T>.for_indexed(block: (Int, T) -> Unit) {}
infix fun <T> _array<T>.set(x: _array<T>?) = this
infix fun <T> _array<T>.put(x: _array<T>?) {}
infix fun <T> _array<T>.con(x: _array<T>?) {}
fun <T> _array<T>.pack() = _bits(0)


// Utilities
fun log(x: Int) = 0
fun exp(x: Int) = 0
fun min(x: Int, vararg y: Int) = 0
fun max(x: Int, vararg y: Int) = 0

// Control flow
interface _edge
fun posedge(x: _bool) = object: _edge {}
fun negedge(x: _bool) = object: _edge {}

fun on(x: _edge, vararg y: _edge, block: (Unit) -> Unit) {}
fun forever(block: (Unit) -> Unit) {}


// Verik commands
fun vk_random() = 0
fun vk_wait(n: Int) {}
fun vk_wait_on(edge: _edge, n: Int = 1) {}
fun vk_literal(string: String) {}
fun vk_display(message: String) {}
fun vk_write(message: String) {}
fun vk_error(message: String) {}
fun vk_finish() {}
