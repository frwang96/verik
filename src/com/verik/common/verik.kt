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
annotation class InOut

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.LOCAL_VARIABLE)
annotation class Reg
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.LOCAL_VARIABLE)
annotation class Wire

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
operator fun <T: Circuit> T.get(n: Int): T {return this}
operator fun <T: Circuit> T.get(n: Int, m: Int): T {return this}

interface Module {
    fun connect(vararg nets: Any){}
}
infix fun <T:Module> T.array(size: String):T {return this}
operator fun <T: Module> T.get(n: Int): T {return this}
operator fun <T: Module> T.get(n: Int, m: Int): T {return this}

interface Interface {
    fun connect(vararg nets: Any){}
}
infix fun <T:Interface> T.array(size: String):T {return this}
operator fun <T: Interface> T.get(n: Int): T {return this}
operator fun <T: Interface> T.get(n: Int, m: Int): T {return this}
interface Port {
    fun connect(vararg nets: Any){}
}

interface Class
infix fun <T:Class> T.array(size: String):T {return this}
operator fun <T: Class> T.get(n: Int): T {return this}
operator fun <T: Class> T.get(n: Int, m: Int): T {return this}


// Data types
typealias Bool = Boolean
operator fun Boolean.Companion.invoke(): Bool {return false}
infix fun Bool.array(size: String):Bool {return false}
operator fun Bool.get(n: Int): Bool {return false}
operator fun Bool.get(n: Int, m: Int): Bool {return false}
infix fun Bool.con(x: Bool?) {}
infix fun Bool.set(x: Bool?) {}
fun Bool.randomize(): Bool {return false}

interface Data
infix fun <T:Data> T.array(size: String):T {return this}
operator fun <T: Data> T.get(n: Int): T {return this}
operator fun <T: Data> T.get(n: Int, m: Int): T {return this}
infix fun <T: Data> T.con(x: T?) {}
infix fun <T: Data> T.set(x: T?) {}
fun <T: Data> T.randomize(): T {return this}

class Bit(var size: Int): Data {
    constructor(value: String): this(0)
}
class Signed(var size: Int): Data {
    constructor(value: String): this(0)
}
class Unsigned(var size: Int): Data {
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
fun vkWaitOn(edge: Edge) {}
fun vkLiteral(string: String) {}
fun vkDisplay(message: String) {}
fun vkWrite(message: String) {}
fun vkError(message: String) {}
