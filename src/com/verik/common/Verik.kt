@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.common

// Copyright (c) 2020 Francis Wang

// Annotations
@Target(AnnotationTarget.CLASS)
annotation class Main
@Target(AnnotationTarget.CLASS)
annotation class Virtual

@Target(AnnotationTarget.PROPERTY)
annotation class In
@Target(AnnotationTarget.PROPERTY)
annotation class Out
@Target(AnnotationTarget.PROPERTY)
annotation class Port

@Target(AnnotationTarget.FUNCTION)
annotation class Connect
@Target(AnnotationTarget.FUNCTION)
annotation class Comb
@Target(AnnotationTarget.FUNCTION)
annotation class Seq
@Target(AnnotationTarget.FUNCTION)
annotation class Initial

@Target(AnnotationTarget.FUNCTION)
annotation class Task
@Target(AnnotationTarget.FUNCTION)
annotation class Fun


// Components
interface Module
infix fun Module.con(x: Any) {}
infix fun Module.con(x: List<Any>) {}

interface Circuit: Module

interface Interface
infix fun Interface.con(x: Interface?) {}
interface Interport
infix fun Interport.con(x: Interport?) {}

interface Class

class Group<T>(val n: Int, val type: T): Iterable<T> {
    operator fun get(n: Int) = type
    override fun iterator() = GroupIterator(type)

    class GroupIterator<T>(val type: T): Iterator<T> {
        override fun hasNext() = false
        override fun next() = type
    }
}


// Utilities
fun log(x: Int) = 0
fun exp(x: Int) = 0
fun min(x: Int, vararg y: Int) = 0
fun max(x: Int, vararg y: Int) = 0

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
