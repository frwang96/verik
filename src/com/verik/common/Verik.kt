@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.common

// Copyright (c) 2020 Francis Wang

// Annotations
@Target(AnnotationTarget.CLASS)
annotation class Virtual

@Target(AnnotationTarget.PROPERTY)
annotation class In
@Target(AnnotationTarget.PROPERTY)
annotation class Out
@Target(AnnotationTarget.PROPERTY)
annotation class Bundle

@Target(AnnotationTarget.PROPERTY)
annotation class Logic

@Target(AnnotationTarget.FUNCTION)
annotation class Initial
@Target(AnnotationTarget.FUNCTION)
annotation class Always

@Target(AnnotationTarget.FUNCTION)
annotation class Task
@Target(AnnotationTarget.FUNCTION)
annotation class Fun


// Components
interface Circuit {
    fun connect(vararg nets: Any) {}
}

interface Module {
    fun connect(vararg nets: Any) {}
}

interface Interface
infix fun <T: Interface> T.set(x: T?) {}
interface Port
infix fun <T: Port> T.set(x: T?) {}

class Vector<T>(val n: Int, val m: Int, val type: T): Iterable<T> {
    val len = 0
    constructor(n: Int, type: T): this(n, 0, type)
    operator fun get(n: Int) = type
    operator fun get(n: Data) = type
    operator fun get(n: Int, m: Int) = this
    override fun iterator() = VectorIterator(type)

    class VectorIterator<T>(val type: T): Iterator<T> {
        override fun hasNext() = false
        override fun next() = type
    }
}

interface Class


// Control flow
sealed class Edge
data class PosEdge(val signal: Bool): Edge()
data class NegEdge(val signal: Bool): Edge()

fun on(vararg edge: Edge, block: (Unit) -> Unit) {}
fun forever(block: (Unit) -> Unit) {}


// Verik commands
fun vkRandom(): Int {return 0}
fun vkDelay(delay: Int) {}
fun vkWaitOn(edge: Edge) {}
fun vkLiteral(string: String) {}
fun vkDisplay(message: String) {}
fun vkWrite(message: String) {}
fun vkError(message: String) {}
fun vkFinish() {}
