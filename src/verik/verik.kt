package verik

// Copyright (c) 2020 Francis Wang


// Annotations
@Target(AnnotationTarget.CLASS)
annotation class Synthesizable
@Target(AnnotationTarget.CLASS)
annotation class Simulatable
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)

annotation class Module
@Target(AnnotationTarget.CLASS)
annotation class Type
@Target(AnnotationTarget.PROPERTY)

annotation class Input(vararg val sizeArr: Int, val size: String = "")
@Target(AnnotationTarget.PROPERTY)
annotation class Output(vararg val sizeArr: Int, val size: String = "")
@Target(AnnotationTarget.PROPERTY)
annotation class OutputReg(vararg val sizeArr: Int, val size: String = "")

@Target(AnnotationTarget.PROPERTY)
annotation class Reg(vararg val sizeArr: Int, val size: String = "")
@Target(AnnotationTarget.PROPERTY)
annotation class Wire(vararg val sizeArr: Int, val size: String = "")

@Target(AnnotationTarget.FUNCTION)
annotation class Initial
@Target(AnnotationTarget.FUNCTION)
annotation class Always


// Data types
typealias Bool = Boolean
operator fun Boolean.Companion.invoke(vararg size: Int): Bool {return false}
operator fun Bool.get(n: Int): Bool {return false}
operator fun Bool.get(n: Int, m: Int): Bool {return false}
infix fun Bool.set(x: Bool?) {}
infix fun Bool.get(x: Bool) {}
fun Bool.randomize(): Bool {return false}

interface Logic
operator fun <T: Logic> T.get(n: Int): T {return this}
operator fun <T: Logic> T.get(n: Int, m: Int): T {return this}
infix fun <T: Logic> T.set(x: T?) {}
infix fun <T: Logic> T.get(x: T) {}
fun <T: Logic> T.randomize(): T {return this}

class Bit: Logic {
    constructor(vararg size: Int)
    constructor(value: String)
}
class Signed: Logic {
    constructor(vararg size: Int)
    constructor(value: String)
}
class Unsigned: Logic {
    constructor(vararg size: Int)
    constructor(value: String)
}


// Operators
operator fun Unsigned.plus(x: Unsigned):Unsigned {return Unsigned()}
operator fun Unsigned.times(x: Unsigned):Unsigned {return Unsigned()}
infix fun Unsigned.and(x: Unsigned): Unsigned {return Unsigned()}
infix fun Unsigned.xor(x: Unsigned): Unsigned {return Unsigned()}


// Control flow
sealed class Edge
data class PosEdge(val signal: Bool): Edge()
data class NegEdge(val signal: Bool): Edge()

fun on(vararg edge: Edge, block: (Unit) -> Unit) {}
fun forever(block: (Unit) -> Unit) {}


// System commands
fun vkDelay(delay: Int) {}
fun vkError(message: String) {}
fun vkWaitOn(edge: Edge) {}
