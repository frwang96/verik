@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.common

// Copyright (c) 2020 Francis Wang

typealias Bool = Boolean
infix fun Bool.array(size: String) = false
operator fun Bool.get(n: Data) = false
operator fun Bool.get(n: Int, m: Int = 0) = false
infix fun Bool.set(x: Bool?) {}
operator fun Boolean.Companion.invoke() = false
fun Boolean.Companion.of(x: Bits) = false
fun Boolean.Companion.of(x: SNum) = false
fun Boolean.Companion.of(x: UNum) = false

interface Data
infix fun <T:Data> T.array(size: String) = this
operator fun <T: Data> T.get(n: Data) = this
operator fun <T: Data> T.get(n: Int, m: Int = 0) = this
infix fun <T: Data> T.set(x: T?) {}

class Bits(val len: Int): Data {
    companion object {
        fun of(value: Int) = Bits(0)
        fun of(value: String) = Bits(0)
        fun of(value: SNum) = Bits(0)
        fun of(value: UNum) = Bits(0)
    }
}
class SNum(val len: Int): Data {
    companion object {
        fun of(value: Int) = SNum(0)
        fun of(value: String) = SNum(0)
        fun of(value: Bits) = SNum(0)
        fun of(value: UNum) = SNum(0)
    }
}
class UNum(val len: Int): Data {
    companion object {
        fun of(value: Int) = UNum(0)
        fun of(value: String) = UNum(0)
        fun of(value: Bits) = UNum(0)
        fun of(value: SNum) = UNum(0)
    }
}


// Native
operator fun SNum.unaryPlus() = SNum(0)
operator fun UNum.unaryPlus() = UNum(0)
operator fun SNum.unaryMinus() = SNum(0)
operator fun UNum.unaryMinus() = UNum(0)
operator fun Bits.not() = Bits(0)
operator fun SNum.not() = SNum(0)
operator fun UNum.not() = UNum(0)

operator fun Int.plus(x: SNum) = SNum(0)
operator fun Int.plus(x: UNum) = UNum(0)
// These conflict with string concatenation
// operator fun String.plus(x: SNum) = SNum(0)
// operator fun String.plus(x: UNum) = UNum(0)
operator fun SNum.plus(x: Int) = SNum(0)
operator fun SNum.plus(x: String) = SNum(0)
operator fun SNum.plus(x: SNum) = SNum(0)
operator fun SNum.plus(x: UNum) = UNum(0)
operator fun UNum.plus(x: Int) = UNum(0)
operator fun UNum.plus(x: String) = UNum(0)
operator fun UNum.plus(x: SNum) = UNum(0)
operator fun UNum.plus(x: UNum) = UNum(0)

operator fun Int.minus(x: SNum) = SNum(0)
operator fun Int.minus(x: UNum) = UNum(0)
operator fun String.minus(x: SNum) = SNum(0)
operator fun String.minus(x: UNum) = UNum(0)
operator fun SNum.minus(x: Int) = SNum(0)
operator fun SNum.minus(x: String) = SNum(0)
operator fun SNum.minus(x: SNum) = SNum(0)
operator fun SNum.minus(x: UNum) = UNum(0)
operator fun UNum.minus(x: Int) = UNum(0)
operator fun UNum.minus(x: String) = UNum(0)
operator fun UNum.minus(x: SNum) = UNum(0)
operator fun UNum.minus(x: UNum) = UNum(0)

operator fun Int.times(x: SNum) = SNum(0)
operator fun Int.times(x: UNum) = UNum(0)
operator fun String.times(x: SNum) = SNum(0)
operator fun String.times(x: UNum) = UNum(0)
operator fun SNum.times(x: Int) = SNum(0)
operator fun SNum.times(x: String) = SNum(0)
operator fun SNum.times(x: SNum) = SNum(0)
operator fun SNum.times(x: UNum) = UNum(0)
operator fun UNum.times(x: Int) = UNum(0)
operator fun UNum.times(x: String) = UNum(0)
operator fun UNum.times(x: SNum) = UNum(0)
operator fun UNum.times(x: UNum) = UNum(0)

operator fun Int.compareTo(x: SNum) = 0
operator fun Int.compareTo(x: UNum) = 0
operator fun String.compareTo(x: SNum) = 0
operator fun String.compareTo(x: UNum) = 0
operator fun SNum.compareTo(x: Int) = 0
operator fun SNum.compareTo(x: String) = 0
operator fun SNum.compareTo(x: SNum) = 0
operator fun UNum.compareTo(x: Int) = 0
operator fun UNum.compareTo(x: String) = 0
operator fun UNum.compareTo(x: UNum) = 0

// Infix
infix fun Int.addFull(x: SNum) = SNum(0)
infix fun Int.addFull(x: UNum) = UNum(0)
infix fun String.addFull(x: SNum) = SNum(0)
infix fun String.addFull(x: UNum) = UNum(0)
infix fun SNum.addFull(x: Int) = SNum(0)
infix fun SNum.addFull(x: String) = SNum(0)
infix fun SNum.addFull(x: SNum) = SNum(0)
infix fun SNum.addFull(x: UNum) = UNum(0)
infix fun UNum.addFull(x: Int) = UNum(0)
infix fun UNum.addFull(x: String) = UNum(0)
infix fun UNum.addFull(x: SNum) = UNum(0)
infix fun UNum.addFull(x: UNum) = UNum(0)

infix fun Int.mulFull(x: SNum) = SNum(0)
infix fun Int.mulFull(x: UNum) = UNum(0)
infix fun String.mulFull(x: SNum) = SNum(0)
infix fun String.mulFull(x: UNum) = UNum(0)
infix fun SNum.mulFull(x: Int) = SNum(0)
infix fun SNum.mulFull(x: String) = SNum(0)
infix fun SNum.mulFull(x: SNum) = SNum(0)
infix fun SNum.mulFull(x: UNum) = UNum(0)
infix fun UNum.mulFull(x: Int) = UNum(0)
infix fun UNum.mulFull(x: String) = UNum(0)
infix fun UNum.mulFull(x: SNum) = UNum(0)
infix fun UNum.mulFull(x: UNum) = UNum(0)

infix fun Bits.sl(x: Int) = Bits(0)
infix fun Bits.sl(x: String) = Bits(0)
infix fun Bits.sl(x: UNum) = Bits(0)
infix fun SNum.sl(x: Int) = SNum(0)
infix fun SNum.sl(x: String) = SNum(0)
infix fun SNum.sl(x: UNum) = SNum(0)
infix fun UNum.sl(x: Int) = UNum(0)
infix fun UNum.sl(x: String) = UNum(0)
infix fun UNum.sl(x: UNum) = UNum(0)

infix fun Bits.sr(x: Int) = Bits(0)
infix fun Bits.sr(x: String) = Bits(0)
infix fun Bits.sr(x: UNum) = Bits(0)
infix fun SNum.sr(x: Int) = SNum(0)
infix fun SNum.sr(x: String) = SNum(0)
infix fun SNum.sr(x: UNum) = SNum(0)
infix fun UNum.sr(x: Int) = UNum(0)
infix fun UNum.sr(x: String) = UNum(0)
infix fun UNum.sr(x: UNum) = UNum(0)

infix fun Bits.rot(x: Int) = Bits(0)
infix fun Bits.rot(x: String) = Bits(0)
infix fun Bits.rot(x: UNum) = Bits(0)
infix fun SNum.rot(x: Int) = SNum(0)
infix fun SNum.rot(x: String) = SNum(0)
infix fun SNum.rot(x: UNum) = SNum(0)
infix fun UNum.rot(x: Int) = UNum(0)
infix fun UNum.rot(x: String) = UNum(0)
infix fun UNum.rot(x: UNum) = UNum(0)

infix fun Int.and(x: Bits) = Bits(0)
infix fun Int.and(x: SNum) = SNum(0)
infix fun Int.and(x: UNum) = UNum(0)
infix fun String.and(x: Bits) = Bits(0)
infix fun String.and(x: SNum) = SNum(0)
infix fun String.and(x: UNum) = UNum(0)
infix fun SNum.and(x: SNum) = SNum(0)
infix fun UNum.and(x: UNum) = UNum(0)

infix fun Int.or(x: Bits) = Bits(0)
infix fun Int.or(x: SNum) = SNum(0)
infix fun Int.or(x: UNum) = UNum(0)
infix fun String.or(x: Bits) = Bits(0)
infix fun String.or(x: SNum) = SNum(0)
infix fun String.or(x: UNum) = UNum(0)
infix fun SNum.or(x: SNum) = SNum(0)
infix fun UNum.or(x: UNum) = UNum(0)

infix fun Int.xor(x: Bits) = Bits(0)
infix fun Int.xor(x: SNum) = SNum(0)
infix fun Int.xor(x: UNum) = UNum(0)
infix fun String.xor(x: Bits) = Bits(0)
infix fun String.xor(x: SNum) = SNum(0)
infix fun String.xor(x: UNum) = UNum(0)
infix fun SNum.xor(x: SNum) = SNum(0)
infix fun UNum.xor(x: UNum) = UNum(0)

infix fun String.cat(x: Bits) = Bits(0)
infix fun String.cat(x: SNum) = SNum(0)
infix fun String.cat(x: UNum) = UNum(0)
infix fun Bool.cat(x: Bits) = Bits(0)
infix fun Bool.cat(x: SNum) = SNum(0)
infix fun Bool.cat(x: UNum) = UNum(0)
infix fun Bits.cat(x: Bool) = Bits(0)
infix fun Bits.cat(x: String) = Bits(0)
infix fun Bits.cat(x: Bits) = Bits(0)
infix fun Bits.cat(x: SNum) = SNum(0)
infix fun Bits.cat(x: UNum) = UNum(0)
infix fun SNum.cat(x: Bool) = SNum(0)
infix fun SNum.cat(x: String) = SNum(0)
infix fun SNum.cat(x: Bits) = SNum(0)
infix fun SNum.cat(x: SNum) = SNum(0)
infix fun SNum.cat(x: UNum) = UNum(0)
infix fun UNum.cat(x: Bool) = UNum(0)
infix fun UNum.cat(x: String) = UNum(0)
infix fun UNum.cat(x: Bits) = UNum(0)
infix fun UNum.cat(x: SNum) = UNum(0)
infix fun UNum.cat(x: UNum) = UNum(0)


// Function
fun inv(x: Bits) = Bits(0)
fun inv(x: SNum) = SNum(0)
fun inv(x: UNum) = UNum(0)
fun redAnd(x: Bits) = false
fun redAnd(x: SNum) = false
fun redAnd(x: UNum) = false
fun redOr(x: Bits) = false
fun redOr(x: SNum) = false
fun redOr(x: UNum) = false
fun redNand(x: Bits) = false
fun redNand(x: SNum) = false
fun redNand(x: UNum) = false
fun redNor(x: Bits) = false
fun redNor(x: SNum) = false
fun redNor(x: UNum) = false
fun redXor(x: Bits) = false
fun redXor(x: SNum) = false
fun redXor(x: UNum) = false
fun redXnor(x: Bits) = false
fun redXnor(x: SNum) = false
fun redXnor(x: UNum) = false

// Cast
fun len(x: Bool) = 0
fun len(x: Data) = 0
fun pack(x: Bool) = Bits(0)
fun pack(x: Data) = Bits(0)
fun unpack(x: Bool, y: Bits) = false
fun unpack(x: Data, y: Bits) = x
fun ext(n: Int, x: Bits) = Bits(0)
fun ext(n: Int, x: SNum) = SNum(0)
fun ext(n: Int, x: UNum) = UNum(0)
fun tru(n: Int, x: Bits) = Bits(0)
fun tru(n: Int, x: SNum) = SNum(0)
fun tru(n: Int, x: UNum) = UNum(0)
