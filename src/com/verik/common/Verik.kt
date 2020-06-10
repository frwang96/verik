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
