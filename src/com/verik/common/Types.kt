@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.common

// Copyright (c) 2020 Francis Wang

typealias Bool = Boolean
operator fun Boolean.Companion.invoke() = false
infix fun Bool.set(x: Bool?) {}

interface Data
infix fun Data.set(x: Data?) {}

class Bits(val n: Int, val m: Int): Data {
    val len = 0
    constructor(n: Int): this(n, 0)
    operator fun get(n: Int) = false
    operator fun get(n: Data) = false
    operator fun get(n: Int, m: Int) = this
    companion object {
        fun of (len: Int, value: Int) = Bits(0)
        fun of (value: String) = Bits(0)
    }
}
infix fun Bits.set(x: Int) {}
class SNum(val n: Int): Data {
    operator fun get(n: Int) = false
    operator fun get(n: Data) = false
    operator fun get(n: Int, m: Int) = this
    companion object {
        fun of (len: Int, value: Int) = SNum(0)
        fun of (value: String) = SNum(0)
    }
}
infix fun SNum.set(x: Int) {}
class UNum(val n: Int): Data {
    operator fun get(n: Int) = false
    operator fun get(n: Data) = false
    operator fun get(n: Int, m: Int) = this
    companion object {
        fun of (len: Int, value: Int) = UNum(0)
        fun of (value: String) = UNum(0)
    }
}
infix fun UNum.set(x: Int) {}


// Native
operator fun SNum.unaryPlus() = SNum(0)
operator fun UNum.unaryPlus() = UNum(0)
operator fun SNum.unaryMinus() = SNum(0)
operator fun UNum.unaryMinus() = UNum(0)

operator fun Bits.not() = false
operator fun SNum.not() = false
operator fun UNum.not() = false

operator fun Int.plus(x: SNum) = SNum(0)
operator fun Int.plus(x: UNum) = UNum(0)
operator fun SNum.plus(x: Int) = SNum(0)
operator fun SNum.plus(x: SNum) = SNum(0)
operator fun SNum.plus(x: UNum) = UNum(0)
operator fun UNum.plus(x: Int) = UNum(0)
operator fun UNum.plus(x: SNum) = UNum(0)
operator fun UNum.plus(x: UNum) = UNum(0)
operator fun Int.minus(x: SNum) = SNum(0)
operator fun Int.minus(x: UNum) = UNum(0)
operator fun SNum.minus(x: Int) = SNum(0)
operator fun SNum.minus(x: SNum) = SNum(0)
operator fun SNum.minus(x: UNum) = UNum(0)
operator fun UNum.minus(x: Int) = UNum(0)
operator fun UNum.minus(x: SNum) = UNum(0)
operator fun UNum.minus(x: UNum) = UNum(0)

operator fun Int.times(x: SNum) = SNum(0)
operator fun Int.times(x: UNum) = UNum(0)
operator fun SNum.times(x: Int) = SNum(0)
operator fun SNum.times(x: SNum) = SNum(0)
operator fun SNum.times(x: UNum) = UNum(0)
operator fun UNum.times(x: Int) = UNum(0)
operator fun UNum.times(x: SNum) = UNum(0)
operator fun UNum.times(x: UNum) = UNum(0)

operator fun Int.compareTo(x: SNum) = 0
operator fun Int.compareTo(x: UNum) = 0
operator fun SNum.compareTo(x: Int) = 0
operator fun SNum.compareTo(x: SNum) = 0
operator fun UNum.compareTo(x: Int) = 0
operator fun UNum.compareTo(x: UNum) = 0


// Infix
infix fun Int.add(x: SNum) = SNum(0)
infix fun Int.add(x: UNum) = UNum(0)
infix fun SNum.add(x: Int) = SNum(0)
infix fun SNum.add(x: SNum) = SNum(0)
infix fun SNum.add(x: UNum) = UNum(0)
infix fun UNum.add(x: Int) = UNum(0)
infix fun UNum.add(x: SNum) = UNum(0)
infix fun UNum.add(x: UNum) = UNum(0)
infix fun Int.sub(x: SNum) = SNum(0)
infix fun Int.sub(x: UNum) = UNum(0)
infix fun SNum.sub(x: Int) = SNum(0)
infix fun SNum.sub(x: SNum) = SNum(0)
infix fun SNum.sub(x: UNum) = UNum(0)
infix fun UNum.sub(x: Int) = UNum(0)
infix fun UNum.sub(x: SNum) = UNum(0)
infix fun UNum.sub(x: UNum) = UNum(0)

infix fun Int.mul(x: SNum) = SNum(0)
infix fun Int.mul(x: UNum) = UNum(0)
infix fun SNum.mul(x: Int) = SNum(0)
infix fun SNum.mul(x: SNum) = SNum(0)
infix fun SNum.mul(x: UNum) = UNum(0)
infix fun UNum.mul(x: Int) = UNum(0)
infix fun UNum.mul(x: SNum) = UNum(0)
infix fun UNum.mul(x: UNum) = UNum(0)

infix fun Bits.sl(x: Int)  = Bits(0)
infix fun Bits.sl(x: UNum) = Bits(0)
infix fun SNum.sl(x: Int) = SNum(0)
infix fun SNum.sl(x: UNum) = SNum(0)
infix fun UNum.sl(x: Int) = UNum(0)
infix fun UNum.sl(x: UNum) = UNum(0)
infix fun Bits.sr(x: Int)  = Bits(0)
infix fun Bits.sr(x: UNum) = Bits(0)
infix fun SNum.sr(x: Int) = SNum(0)
infix fun SNum.sr(x: UNum) = SNum(0)
infix fun UNum.sr(x: Int) = UNum(0)
infix fun UNum.sr(x: UNum) = UNum(0)
infix fun Bits.rotl(x: Int)  = Bits(0)
infix fun Bits.rotl(x: UNum) = Bits(0)
infix fun SNum.rotl(x: Int) = SNum(0)
infix fun SNum.rotl(x: UNum) = SNum(0)
infix fun UNum.rotl(x: Int) = UNum(0)
infix fun UNum.rotl(x: UNum) = UNum(0)
infix fun Bits.rotr(x: Int)  = Bits(0)
infix fun Bits.rotr(x: UNum) = Bits(0)
infix fun SNum.rotr(x: Int) = SNum(0)
infix fun SNum.rotr(x: UNum) = SNum(0)
infix fun UNum.rotr(x: Int) = UNum(0)
infix fun UNum.rotr(x: UNum) = UNum(0)

infix fun Bits.slExt(x: Int) = Bits(0)
infix fun SNum.slExt(x: Int) = SNum(0)
infix fun UNum.slExt(x: Int) = UNum(0)

infix fun Bits.srTru(x: Int) = Bits(0)
infix fun SNum.srTru(x: Int) = SNum(0)
infix fun UNum.srTru(x: Int) = UNum(0)

infix fun Int.and(x: Bits) = Bits(0)
infix fun Int.and(x: SNum) = SNum(0)
infix fun Int.and(x: UNum) = UNum(0)
infix fun Bits.and(x: Bits) = Bits(0)
infix fun Bits.and(x: Int) = Bits(0)
infix fun SNum.and(x: SNum) = SNum(0)
infix fun SNum.and(x: Int) = SNum(0)
infix fun UNum.and(x: UNum) = UNum(0)
infix fun UNum.and(x: Int) = UNum(0)
infix fun Int.or(x: Bits) = Bits(0)
infix fun Int.or(x: SNum) = SNum(0)
infix fun Int.or(x: UNum) = UNum(0)
infix fun Bits.or(x: Bits) = Bits(0)
infix fun Bits.or(x: Int) = Bits(0)
infix fun SNum.or(x: SNum) = SNum(0)
infix fun SNum.or(x: Int) = SNum(0)
infix fun UNum.or(x: UNum) = UNum(0)
infix fun UNum.or(x: Int) = UNum(0)
infix fun Int.xor(x: Bits) = Bits(0)
infix fun Int.xor(x: SNum) = SNum(0)
infix fun Int.xor(x: UNum) = UNum(0)
infix fun Bits.xor(x: Bits) = Bits(0)
infix fun Bits.xor(x: Int) = Bits(0)
infix fun SNum.xor(x: SNum) = SNum(0)
infix fun SNum.xor(x: Int) = SNum(0)
infix fun UNum.xor(x: UNum) = UNum(0)
infix fun UNum.xor(x: Int) = UNum(0)

infix fun Int.nand(x: Bits) = Bits(0)
infix fun Int.nand(x: SNum) = SNum(0)
infix fun Int.nand(x: UNum) = UNum(0)
infix fun Bool.nand(x: Bool) = false
infix fun Bits.nand(x: Bits) = Bits(0)
infix fun Bits.nand(x: Int) = Bits(0)
infix fun SNum.nand(x: SNum) = SNum(0)
infix fun SNum.nand(x: Int) = SNum(0)
infix fun UNum.nand(x: UNum) = UNum(0)
infix fun UNum.nand(x: Int) = UNum(0)
infix fun Int.nor(x: Bits) = Bits(0)
infix fun Int.nor(x: SNum) = SNum(0)
infix fun Int.nor(x: UNum) = UNum(0)
infix fun Bool.nor(x: Bool) = false
infix fun Bits.nor(x: Bits) = Bits(0)
infix fun Bits.nor(x: Int) = Bits(0)
infix fun SNum.nor(x: SNum) = SNum(0)
infix fun SNum.nor(x: Int) = SNum(0)
infix fun UNum.nor(x: UNum) = UNum(0)
infix fun UNum.nor(x: Int) = UNum(0)
infix fun Int.xnor(x: Bits) = Bits(0)
infix fun Int.xnor(x: SNum) = SNum(0)
infix fun Int.xnor(x: UNum) = UNum(0)
infix fun Bool.xnor(x: Bool) = false
infix fun Bits.xnor(x: Bits) = Bits(0)
infix fun Bits.xnor(x: Int) = Bits(0)
infix fun SNum.xnor(x: SNum) = SNum(0)
infix fun SNum.xnor(x: Int) = SNum(0)
infix fun UNum.xnor(x: UNum) = UNum(0)
infix fun UNum.xnor(x: Int) = UNum(0)


infix fun Bool.cat(x: Bool) = Bits(0)
infix fun Bool.cat(x: Bits) = Bits(0)
infix fun Bool.cat(x: SNum) = SNum(0)
infix fun Bool.cat(x: UNum) = UNum(0)
infix fun Bits.cat(x: Bool) = Bits(0)
infix fun Bits.cat(x: Bits) = Bits(0)
infix fun Bits.cat(x: SNum) = SNum(0)
infix fun Bits.cat(x: UNum) = UNum(0)
infix fun SNum.cat(x: Bool) = SNum(0)
infix fun SNum.cat(x: Bits) = SNum(0)
infix fun SNum.cat(x: SNum) = SNum(0)
infix fun SNum.cat(x: UNum) = UNum(0)
infix fun UNum.cat(x: Bool) = UNum(0)
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

fun max(x: SNum, y: SNum) = SNum(0)
fun max(x: UNum, y: UNum) = UNum(0)
fun max(x: Int, y: SNum) = SNum(0)
fun max(x: Int, y: UNum) = UNum(0)
fun max(x: SNum, y: Int) = SNum(0)
fun max(x: UNum, y: Int) = UNum(0)
fun min(x: SNum, y: SNum) = SNum(0)
fun min(x: UNum, y: UNum) = UNum(0)
fun min(x: Int, y: SNum) = SNum(0)
fun min(x: Int, y: UNum) = UNum(0)
fun min(x: SNum, y: Int) = SNum(0)
fun min(x: UNum, y: Int) = UNum(0)

fun signed(x: Bits) = SNum(0)
fun signed(x: UNum) = SNum(0)

fun unsigned(x: Bits) = UNum(0)
fun unsigned(x: SNum) = UNum(0)

// Other
fun len(x: Bool) = 0
fun len(x: Data) = 0

fun ext(n: Int, x: Bits) = Bits(0)
fun ext(n: Int, x: SNum) = SNum(0)
fun ext(n: Int, x: UNum) = UNum(0)
fun tru(n: Int, x: Bits) = Bits(0)
fun tru(n: Int, x: SNum) = SNum(0)
fun tru(n: Int, x: UNum) = UNum(0)

fun pack(x: Bool) = Bits(0)
fun pack(x: Data) = Bits(0)

fun unpack(x: Bool, y: Bits) = false
fun unpack(x: Bits, y: Bits) = Bits(0)
fun unpack(x: SNum, y: Bits) = SNum(0)
fun unpack(x: UNum, y: Bits) = UNum(0)
fun unpack(x: Data, y: Bits) = x
