@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.common

// Copyright (c) 2020 Francis Wang

// Annotations
@Target(AnnotationTarget.CLASS)
annotation class main
@Target(AnnotationTarget.CLASS)
annotation class virtual

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

@Target(AnnotationTarget.FUNCTION)
annotation class comb
@Target(AnnotationTarget.FUNCTION)
annotation class seq
@Target(AnnotationTarget.FUNCTION)
annotation class initial

@Target(AnnotationTarget.FUNCTION)
annotation class task
@Target(AnnotationTarget.FUNCTION)
annotation class function


// Components
interface _module
interface _circuit: _module
interface _intf
interface _port
interface _class


// Collections
class _group<T>(val len: Int, val type: T) {
    operator fun get(n: Int) = type
}
infix fun <T> _group<T>.for_each(block: (T) -> Unit) {}
infix fun <T> _group<T>.for_indexed(block: (Int, T) -> Unit) {}


// Utilities
fun log(x: Int) = 0
fun exp(x: Int) = 0
fun min(x: Int, vararg y: Int) = 0
fun max(x: Int, vararg y: Int) = 0

// Control flow
interface _edge
fun posedge(signal: _bool) = object: _edge {}
fun negedge(signal: _bool) = object: _edge {}

fun on(x: _edge, vararg y: _edge, block: (Unit) -> Unit) {}
fun forever(block: (Unit) -> Unit) {}


// Verik commands
fun vk_random(): Int {return 0}
fun vk_delay(delay: Int) {}
fun vk_wait_on(edge: _edge) {}
fun vk_literal(string: String) {}
fun vk_display(message: String) {}
fun vk_write(message: String) {}
fun vk_error(message: String) {}
fun vk_finish() {}
