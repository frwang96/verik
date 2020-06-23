@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.common

// Copyright (c) 2020 Francis Wang

interface _data
infix fun <T: _data> T.set(x: T?) = this
infix fun <T: _data> T.put(x: T?) {}
infix fun <T: _data> T.con(x: T?) {}
fun _data.pack() = _bits(0)

typealias _bool = Boolean
operator fun Boolean.Companion.invoke() = false
infix fun _bool.set(x: _bool?) = this
infix fun _bool.put(x: _bool?) {}
infix fun _bool.con(x: _bool?) {}
fun _bool.pack() = _bits(0)

typealias _int = Int
operator fun Int.Companion.invoke() = 0
fun _int.pack() = 0

open class _bits(val range: IntRange): _data {
    constructor(len: Int): this(0..0)
    operator fun get(n: Int) = false
    operator fun get(n: _bits) = false
    operator fun get(n: _uint) = false
    operator fun get(range: IntRange) = this
    companion object {
        fun of (len: Int, value: Int) = _bits(0)
        fun of (value: String) = _bits(0)
        fun of (value: Int) = _bits(0)
    }
}
fun _bits.unpack(x: _bool) = false
fun <T> _bits.unpack(x: _array<T>) = x
fun <T: _data> _bits.unpack(x: T) = x
infix fun _bits.set(x: Int) = this
infix fun _bits.put(x: Int) {}
infix fun _bits.con(x: Int) {}
class _byte: _bits(8)

open class _sint(val len: Int): _data {
    operator fun get(n: Int) = false
    operator fun get(n: _bits) = false
    operator fun get(n: _uint) = false
    operator fun get(range: IntRange) = _bits(0)
    companion object {
        fun of (len: Int, value: Int) = _sint(0)
        fun of (value: String) = _sint(0)
        fun of (value: Int) = _sint(0)
    }
}
infix fun _sint.set(x: Int) = this
infix fun _sint.put(x: Int) {}
infix fun _sint.con(x: Int) {}
class _sint8: _sint(8)
class _sint16: _sint(16)
class _sint32: _sint(32)
class _sint64: _sint(64)

open class _uint(val len: Int): _data {
    operator fun get(n: Int) = false
    operator fun get(n: _bits) = false
    operator fun get(n: _uint) = false
    operator fun get(range: IntRange) = _bits(0)
    companion object {
        fun of (len: Int, value: Int) = _uint(0)
        fun of (value: String) = _uint(0)
        fun of (value: Int) = _uint(0)
    }
}
infix fun _uint.set(x: Int) = this
infix fun _uint.put(x: Int) {}
infix fun _uint.con(x: Int) {}
class _uint8: _uint(8)
class _uint16: _uint(16)
class _uint32: _uint(32)
class _uint64: _uint(64)

interface _enum: _data
interface _struct: _data
infix fun <T: _struct> T.apply(block: T.() -> Unit) = this


// Native
operator fun _sint.unaryPlus() = _sint(0)
operator fun _uint.unaryPlus() = _uint(0)
operator fun _sint.unaryMinus() = _sint(0)
operator fun _uint.unaryMinus() = _uint(0)

operator fun _bits.not() = false
operator fun _sint.not() = false
operator fun _uint.not() = false

operator fun Int.plus(x: _sint) = _sint(0)
operator fun Int.plus(x: _uint) = _uint(0)
operator fun _sint.plus(x: Int) = _sint(0)
operator fun _sint.plus(x: _sint) = _sint(0)
operator fun _sint.plus(x: _uint) = _uint(0)
operator fun _uint.plus(x: Int) = _uint(0)
operator fun _uint.plus(x: _sint) = _uint(0)
operator fun _uint.plus(x: _uint) = _uint(0)
operator fun Int.minus(x: _sint) = _sint(0)
operator fun Int.minus(x: _uint) = _uint(0)
operator fun _sint.minus(x: Int) = _sint(0)
operator fun _sint.minus(x: _sint) = _sint(0)
operator fun _sint.minus(x: _uint) = _uint(0)
operator fun _uint.minus(x: Int) = _uint(0)
operator fun _uint.minus(x: _sint) = _uint(0)
operator fun _uint.minus(x: _uint) = _uint(0)

operator fun Int.times(x: _sint) = _sint(0)
operator fun Int.times(x: _uint) = _uint(0)
operator fun _sint.times(x: Int) = _sint(0)
operator fun _sint.times(x: _sint) = _sint(0)
operator fun _sint.times(x: _uint) = _uint(0)
operator fun _uint.times(x: Int) = _uint(0)
operator fun _uint.times(x: _sint) = _uint(0)
operator fun _uint.times(x: _uint) = _uint(0)

operator fun Int.compareTo(x: _sint) = 0
operator fun Int.compareTo(x: _uint) = 0
operator fun _sint.compareTo(x: Int) = 0
operator fun _sint.compareTo(x: _sint) = 0
operator fun _uint.compareTo(x: Int) = 0
operator fun _uint.compareTo(x: _uint) = 0


// Infix
infix fun Int.add(x: _sint) = _sint(0)
infix fun Int.add(x: _uint) = _uint(0)
infix fun _sint.add(x: Int) = _sint(0)
infix fun _sint.add(x: _sint) = _sint(0)
infix fun _sint.add(x: _uint) = _uint(0)
infix fun _uint.add(x: Int) = _uint(0)
infix fun _uint.add(x: _sint) = _uint(0)
infix fun _uint.add(x: _uint) = _uint(0)
infix fun Int.sub(x: _sint) = _sint(0)
infix fun Int.sub(x: _uint) = _uint(0)
infix fun _sint.sub(x: Int) = _sint(0)
infix fun _sint.sub(x: _sint) = _sint(0)
infix fun _sint.sub(x: _uint) = _uint(0)
infix fun _uint.sub(x: Int) = _uint(0)
infix fun _uint.sub(x: _sint) = _uint(0)
infix fun _uint.sub(x: _uint) = _uint(0)

infix fun Int.mul(x: _sint) = _sint(0)
infix fun Int.mul(x: _uint) = _uint(0)
infix fun _sint.mul(x: Int) = _sint(0)
infix fun _sint.mul(x: _sint) = _sint(0)
infix fun _sint.mul(x: _uint) = _uint(0)
infix fun _uint.mul(x: Int) = _uint(0)
infix fun _uint.mul(x: _sint) = _uint(0)
infix fun _uint.mul(x: _uint) = _uint(0)

infix fun _bits.sl(x: Int)  = _bits(0)
infix fun _bits.sl(x: _uint) = _bits(0)
infix fun _sint.sl(x: Int) = _sint(0)
infix fun _sint.sl(x: _uint) = _sint(0)
infix fun _uint.sl(x: Int) = _uint(0)
infix fun _uint.sl(x: _uint) = _uint(0)
infix fun _bits.sr(x: Int)  = _bits(0)
infix fun _bits.sr(x: _uint) = _bits(0)
infix fun _sint.sr(x: Int) = _sint(0)
infix fun _sint.sr(x: _uint) = _sint(0)
infix fun _uint.sr(x: Int) = _uint(0)
infix fun _uint.sr(x: _uint) = _uint(0)
infix fun _bits.rotl(x: Int)  = _bits(0)
infix fun _bits.rotl(x: _uint) = _bits(0)
infix fun _sint.rotl(x: Int) = _sint(0)
infix fun _sint.rotl(x: _uint) = _sint(0)
infix fun _uint.rotl(x: Int) = _uint(0)
infix fun _uint.rotl(x: _uint) = _uint(0)
infix fun _bits.rotr(x: Int)  = _bits(0)
infix fun _bits.rotr(x: _uint) = _bits(0)
infix fun _sint.rotr(x: Int) = _sint(0)
infix fun _sint.rotr(x: _uint) = _sint(0)
infix fun _uint.rotr(x: Int) = _uint(0)
infix fun _uint.rotr(x: _uint) = _uint(0)

infix fun _bits.sl_ext(x: Int) = _bits(0)
infix fun _sint.sl_ext(x: Int) = _sint(0)
infix fun _uint.sl_ext(x: Int) = _uint(0)

infix fun _bits.sr_tru(x: Int) = _bits(0)
infix fun _sint.sr_tru(x: Int) = _sint(0)
infix fun _uint.sr_tru(x: Int) = _uint(0)

infix fun Int.and(x: _bits) = _bits(0)
infix fun Int.and(x: _sint) = _sint(0)
infix fun Int.and(x: _uint) = _uint(0)
infix fun _bits.and(x: _bits) = _bits(0)
infix fun _bits.and(x: Int) = _bits(0)
infix fun _sint.and(x: _sint) = _sint(0)
infix fun _sint.and(x: Int) = _sint(0)
infix fun _uint.and(x: _uint) = _uint(0)
infix fun _uint.and(x: Int) = _uint(0)
infix fun Int.or(x: _bits) = _bits(0)
infix fun Int.or(x: _sint) = _sint(0)
infix fun Int.or(x: _uint) = _uint(0)
infix fun _bits.or(x: _bits) = _bits(0)
infix fun _bits.or(x: Int) = _bits(0)
infix fun _sint.or(x: _sint) = _sint(0)
infix fun _sint.or(x: Int) = _sint(0)
infix fun _uint.or(x: _uint) = _uint(0)
infix fun _uint.or(x: Int) = _uint(0)
infix fun Int.xor(x: _bits) = _bits(0)
infix fun Int.xor(x: _sint) = _sint(0)
infix fun Int.xor(x: _uint) = _uint(0)
infix fun _bits.xor(x: _bits) = _bits(0)
infix fun _bits.xor(x: Int) = _bits(0)
infix fun _sint.xor(x: _sint) = _sint(0)
infix fun _sint.xor(x: Int) = _sint(0)
infix fun _uint.xor(x: _uint) = _uint(0)
infix fun _uint.xor(x: Int) = _uint(0)

infix fun Int.nand(x: _bits) = _bits(0)
infix fun Int.nand(x: _sint) = _sint(0)
infix fun Int.nand(x: _uint) = _uint(0)
infix fun _bool.nand(x: _bool) = false
infix fun _bits.nand(x: _bits) = _bits(0)
infix fun _bits.nand(x: Int) = _bits(0)
infix fun _sint.nand(x: _sint) = _sint(0)
infix fun _sint.nand(x: Int) = _sint(0)
infix fun _uint.nand(x: _uint) = _uint(0)
infix fun _uint.nand(x: Int) = _uint(0)
infix fun Int.nor(x: _bits) = _bits(0)
infix fun Int.nor(x: _sint) = _sint(0)
infix fun Int.nor(x: _uint) = _uint(0)
infix fun _bool.nor(x: _bool) = false
infix fun _bits.nor(x: _bits) = _bits(0)
infix fun _bits.nor(x: Int) = _bits(0)
infix fun _sint.nor(x: _sint) = _sint(0)
infix fun _sint.nor(x: Int) = _sint(0)
infix fun _uint.nor(x: _uint) = _uint(0)
infix fun _uint.nor(x: Int) = _uint(0)
infix fun Int.xnor(x: _bits) = _bits(0)
infix fun Int.xnor(x: _sint) = _sint(0)
infix fun Int.xnor(x: _uint) = _uint(0)
infix fun _bool.xnor(x: _bool) = false
infix fun _bits.xnor(x: _bits) = _bits(0)
infix fun _bits.xnor(x: Int) = _bits(0)
infix fun _sint.xnor(x: _sint) = _sint(0)
infix fun _sint.xnor(x: Int) = _sint(0)
infix fun _uint.xnor(x: _uint) = _uint(0)
infix fun _uint.xnor(x: Int) = _uint(0)


infix fun _bool.cat(x: _bool) = _bits(0)
infix fun _bool.cat(x: _bits) = _bits(0)
infix fun _bool.cat(x: _sint) = _sint(0)
infix fun _bool.cat(x: _uint) = _uint(0)
infix fun _bits.cat(x: _bool) = _bits(0)
infix fun _bits.cat(x: _bits) = _bits(0)
infix fun _bits.cat(x: _sint) = _sint(0)
infix fun _bits.cat(x: _uint) = _uint(0)
infix fun _sint.cat(x: _bool) = _sint(0)
infix fun _sint.cat(x: _bits) = _sint(0)
infix fun _sint.cat(x: _sint) = _sint(0)
infix fun _sint.cat(x: _uint) = _uint(0)
infix fun _uint.cat(x: _bool) = _uint(0)
infix fun _uint.cat(x: _bits) = _uint(0)
infix fun _uint.cat(x: _sint) = _uint(0)
infix fun _uint.cat(x: _uint) = _uint(0)


// Function
fun rep(n: Int, x: _bits) = _bits(0)

fun inv(x: _bits) = _bits(0)
fun inv(x: _sint) = _sint(0)
fun inv(x: _uint) = _uint(0)

fun red_and(x: _bits) = false
fun red_and(x: _sint) = false
fun red_and(x: _uint) = false
fun red_or(x: _bits) = false
fun red_or(x: _sint) = false
fun red_or(x: _uint) = false
fun red_nand(x: _bits) = false
fun red_nand(x: _sint) = false
fun red_nand(x: _uint) = false
fun red_nor(x: _bits) = false
fun red_nor(x: _sint) = false
fun red_nor(x: _uint) = false
fun red_xor(x: _bits) = false
fun red_xor(x: _sint) = false
fun red_xor(x: _uint) = false
fun red_xnor(x: _bits) = false
fun red_xnor(x: _sint) = false
fun red_xnor(x: _uint) = false

fun max(x: _sint, y: _sint) = _sint(0)
fun max(x: _uint, y: _uint) = _uint(0)
fun max(x: Int, y: _sint) = _sint(0)
fun max(x: Int, y: _uint) = _uint(0)
fun max(x: _sint, y: Int) = _sint(0)
fun max(x: _uint, y: Int) = _uint(0)
fun min(x: _sint, y: _sint) = _sint(0)
fun min(x: _uint, y: _uint) = _uint(0)
fun min(x: Int, y: _sint) = _sint(0)
fun min(x: Int, y: _uint) = _uint(0)
fun min(x: _sint, y: Int) = _sint(0)
fun min(x: _uint, y: Int) = _uint(0)

fun signed(x: _bits) = _sint(0)
fun signed(x: _uint) = _sint(0)

fun unsigned(x: _bits) = _uint(0)
fun unsigned(x: _sint) = _uint(0)

// Other
fun len(x: _bool) = 0
fun len(x: _data) = 0

fun ext(len: Int, x: _bits) = _bits(0)
fun ext(len: Int, x: _sint) = _sint(0)
fun ext(len: Int, x: _uint) = _uint(0)
fun tru(len: Int, x: _bits) = _bits(0)
fun tru(len: Int, x: _sint) = _sint(0)
fun tru(len: Int, x: _uint) = _uint(0)
