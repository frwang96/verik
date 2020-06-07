package com.verik.common

// Copyright (c) 2020 Francis Wang

// Annotations
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class Synthesizable
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class Simulatable
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class Virtual

@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class Module
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class Interface
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class Port
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class Object
@Target(AnnotationTarget.CLASS)
annotation class Type

@Target(AnnotationTarget.PROPERTY)
annotation class In
@Target(AnnotationTarget.PROPERTY)
annotation class Out

@Target(AnnotationTarget.PROPERTY)
annotation class Reg
@Target(AnnotationTarget.PROPERTY)
annotation class Wire

@Target(AnnotationTarget.FUNCTION)
annotation class Initial
@Target(AnnotationTarget.FUNCTION)
annotation class Always


// Data types
typealias Bool = Boolean
operator fun Boolean.Companion.invoke(): Bool {return false}
infix fun Bool.array(size: String):Bool {return false}
operator fun Bool.get(n: Int): Bool {return false}
operator fun Bool.get(n: Int, m: Int): Bool {return false}
infix fun Bool.con(x: Bool?) {}
infix fun Bool.set(x: Bool?) {}
fun Bool.randomize(): Bool {return false}

interface Logic
infix fun <T:Logic> T.array(size: String):T {return this}
operator fun <T: Logic> T.get(n: Int): T {return this}
operator fun <T: Logic> T.get(n: Int, m: Int): T {return this}
infix fun <T: Logic> T.con(x: T?) {}
infix fun <T: Logic> T.set(x: T?) {}
fun <T: Logic> T.randomize(): T {return this}

class Bit(var size: Int): Logic {
    constructor(value: String): this(0)
}
class Signed(var size: Int): Logic {
    constructor(value: String): this(0)
}
class Unsigned(var size: Int): Logic {
    constructor(value: String): this(0)
}


// Operators
operator fun Unsigned.plus(x: Unsigned): Unsigned {return Unsigned(0)}
operator fun Unsigned.times(x: Unsigned): Unsigned {return Unsigned(0)}
infix fun Unsigned.and(x: Unsigned): Unsigned {return Unsigned(0)}
infix fun Unsigned.xor(x: Unsigned): Unsigned {return Unsigned(0)}


// Control flow
sealed class Edge
data class PosEdge(val signal: Bool): Edge()
data class NegEdge(val signal: Bool): Edge()

fun on(vararg edge: Edge, block: (Unit) -> Unit) {}
fun forever(block: (Unit) -> Unit) {}


// Verik commands
fun vkDelay(delay: Int) {}
fun vkError(message: String) {}
fun vkWaitOn(edge: Edge) {}
fun vkDisplay(message: String) {}
fun vkWrite(message: String) {}
fun vkLiteral(string: String) {}
