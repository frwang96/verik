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
    fun connect(vararg nets: Any){}
}
infix fun <T:Circuit> T.array(size: String):T {return this}
operator fun <T: Circuit> T.get(n: Data): T {return this}
operator fun <T: Circuit> T.get(n: Int, m: Int = 0): T {return this}

interface Module {
    fun connect(vararg nets: Any){}
}
infix fun <T:Module> T.array(size: String):T {return this}
operator fun <T: Module> T.get(n: Data): T {return this}
operator fun <T: Module> T.get(n: Int, m: Int = 0): T {return this}

interface Interface
infix fun <T:Interface> T.array(size: String):T {return this}
operator fun <T: Interface> T.get(n: Data): T {return this}
operator fun <T: Interface> T.get(n: Int, m: Int = 0): T {return this}
infix fun <T: Interface> T.set(x: T?) {}
interface Port
infix fun <T: Port> T.set(x: T?) {}

interface Class
infix fun <T:Class> T.array(size: String):T {return this}
operator fun <T: Class> T.get(n: Data): T {return this}
operator fun <T: Class> T.get(n: Int, m: Int = 0): T {return this}


// Data types
typealias Bool = Boolean
operator fun Boolean.Companion.invoke(): Bool {return false}
infix fun Bool.array(size: String):Bool {return false}
operator fun Bool.get(n: Data): Bool {return false}
operator fun Bool.get(n: Int, m: Int = 0): Bool {return false}
infix fun Bool.set(x: Bool?) {}
fun Bool.randomize(): Bool {return false}

interface Data
infix fun <T:Data> T.array(size: String):T {return this}
operator fun <T: Data> T.get(n: Data): T {return this}
operator fun <T: Data> T.get(n: Int, m: Int = 0): T {return this}
infix fun <T: Data> T.set(x: T?) {}
fun <T: Data> T.randomize(): T {return this}

open class Bit(val size: Int): Data {
    constructor(value: String): this(0)
}
class UNum(size: Int): Bit(size) {
    constructor(value: String): this(0)
}
class SNum(size: Int): Bit(size) {
    constructor(value: String): this(0)
}

interface Struct: Data
interface Enu: Data


// Operators
operator fun UNum.not(): Bool {return false}
operator fun UNum.plus(x: UNum): UNum {return UNum(0)}
operator fun UNum.times(x: UNum): UNum {return UNum(0)}
infix fun UNum.and(x: UNum): UNum {return UNum(0)}
infix fun UNum.xor(x: UNum): UNum {return UNum(0)}


// Control flow
sealed class Edge
data class PosEdge(val signal: Bool): Edge()
data class NegEdge(val signal: Bool): Edge()

fun on(vararg edge: Edge, block: (Unit) -> Unit) {}
fun forever(block: (Unit) -> Unit) {}


// Verik commands
fun vkDelay(delay: Int) {}
fun vkWaitOn(edge: Edge) {}
fun vkLiteral(string: String) {}
fun vkDisplay(message: String) {}
fun vkWrite(message: String) {}
fun vkError(message: String) {}
fun vkFinish() {}
