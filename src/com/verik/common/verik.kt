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
infix fun <T:Circuit> T.array(size: String) = this
operator fun <T: Circuit> T.get(n: Data) = this
operator fun <T: Circuit> T.get(n: Int, m: Int = 0) = this

interface Module {
    fun connect(vararg nets: Any) {}
}
infix fun <T:Module> T.array(size: String) = this
operator fun <T: Module> T.get(n: Data) = this
operator fun <T: Module> T.get(n: Int, m: Int = 0) = this

interface Interface
infix fun <T:Interface> T.array(size: String) = this
operator fun <T: Interface> T.get(n: Data) = this
operator fun <T: Interface> T.get(n: Int, m: Int = 0) = this
infix fun <T: Interface> T.set(x: T?) {}
interface Port
infix fun <T: Port> T.set(x: T?) {}

interface Class
infix fun <T:Class> T.array(size: String) = this
operator fun <T: Class> T.get(n: Data) = this
operator fun <T: Class> T.get(n: Int, m: Int = 0) = this


// Data types
typealias Bool = Boolean
operator fun Boolean.Companion.invoke() = false
infix fun Bool.array(size: String) = false
operator fun Bool.get(n: Data) = false
operator fun Bool.get(n: Int, m: Int = 0) = false
infix fun Bool.set(x: Bool?) {}

interface Data
infix fun <T:Data> T.array(size: String) = this
operator fun <T: Data> T.get(n: Data) = this
operator fun <T: Data> T.get(n: Int, m: Int = 0) = this
infix fun <T: Data> T.set(x: T?) {}

class Bits(val len: Int): Data {
    operator fun not() = false
    companion object {
        fun of(value: Int) = Bits(0)
        fun of(value: String) = Bits(0)
    }
}
class UNum(val len: Int): Data {
    operator fun not() = false
    operator fun plus(x: UNum) = UNum(0)
    operator fun times(x: UNum) = UNum(0)
    infix fun and(x: UNum) = UNum(0)
    infix fun xor(x: UNum) = UNum(0)
    companion object {
        fun of(value: Int) = UNum(0)
        fun of(value: String) = UNum(0)
    }
}
class SNum(val len: Int): Data {
    operator fun not() = false
    operator fun plus(x: SNum) = SNum(0)
    operator fun times(x: SNum) = SNum(0)
    companion object {
        fun of(value: Int) = SNum(0)
        fun of(value: String) = SNum(0)
    }
}


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
