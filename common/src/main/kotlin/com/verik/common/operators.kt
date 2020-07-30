/*
 * Copyright 2020 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("UNUSED_PARAMETER", "unused")

package com.verik.common

// range
infix fun Int.until(x: _uint) = _range()
infix fun _uint.until(x: Int) = _range()
infix fun _uint.until(x: _uint) = _range()

operator fun Int.rangeTo(x: _uint) = _range()
operator fun _uint.rangeTo(x: Int) = _range()
operator fun _uint.rangeTo(x: _uint) = _range()

operator fun IntRange.contains(x: _uint) = false

// native
operator fun _sint.unaryPlus() = _sint(0)
operator fun _uint.unaryPlus() = _uint(0)
operator fun _sint.unaryMinus() = _sint(0)
operator fun _uint.unaryMinus() = _uint(0)

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

operator fun Int.rem(x: _sint) = _sint(0)
operator fun Int.rem(x: _uint) = _uint(0)
operator fun _sint.rem(x: Int) = _sint(0)
operator fun _sint.rem(x: _sint) = _sint(0)
operator fun _sint.rem(x: _uint) = _uint(0)
operator fun _uint.rem(x: Int) = _uint(0)
operator fun _uint.rem(x: _sint) = _uint(0)
operator fun _uint.rem(x: _uint) = _uint(0)

operator fun Int.div(x: _sint) = _sint(0)
operator fun Int.div(x: _uint) = _uint(0)
operator fun _sint.div(x: Int) = _sint(0)
operator fun _sint.div(x: _sint) = _sint(0)
operator fun _sint.div(x: _uint) = _uint(0)
operator fun _uint.div(x: Int) = _uint(0)
operator fun _uint.div(x: _sint) = _uint(0)
operator fun _uint.div(x: _uint) = _uint(0)

operator fun Int.compareTo(x: _sint) = 0
operator fun Int.compareTo(x: _uint) = 0
operator fun _sint.compareTo(x: Int) = 0
operator fun _sint.compareTo(x: _sint) = 0
operator fun _uint.compareTo(x: Int) = 0
operator fun _uint.compareTo(x: _uint) = 0

// infix
infix fun Int.eq(x: _sint) = false
infix fun Int.eq(x: _uint) = false
infix fun _sint.eq(x: Int) = false
infix fun _uint.eq(x: Int) = false
infix fun Int.neq(x: _sint) = false
infix fun Int.neq(x: _uint) = false
infix fun _sint.neq(x: Int) = false
infix fun _uint.neq(x: Int) = false

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

infix fun _sint.sl(x: Int) = _sint(0)
infix fun _sint.sl(x: _uint) = _sint(0)
infix fun _uint.sl(x: Int) = _uint(0)
infix fun _uint.sl(x: _uint) = _uint(0)
infix fun _sint.sr(x: Int) = _sint(0)
infix fun _sint.sr(x: _uint) = _sint(0)
infix fun _uint.sr(x: Int) = _uint(0)
infix fun _uint.sr(x: _uint) = _uint(0)

infix fun _sint.rotl(x: Int) = _uint(0)
infix fun _sint.rotl(x: _uint) = _uint(0)
infix fun _uint.rotl(x: Int) = _uint(0)
infix fun _uint.rotl(x: _uint) = _uint(0)
infix fun _sint.rotr(x: Int) = _uint(0)
infix fun _sint.rotr(x: _uint) = _uint(0)
infix fun _uint.rotr(x: Int) = _uint(0)
infix fun _uint.rotr(x: _uint) = _uint(0)

infix fun _sint.sl_ext(x: Int) = _sint(0)
infix fun _uint.sl_ext(x: Int) = _uint(0)

infix fun _sint.sr_tru(x: Int) = _sint(0)
infix fun _uint.sr_tru(x: Int) = _uint(0)

infix fun Int.and(x: _sint) = _uint(0)
infix fun Int.and(x: _uint) = _uint(0)
infix fun _sint.and(x: Int) = _uint(0)
infix fun _sint.and(x: _sint) = _uint(0)
infix fun _sint.and(x: _uint) = _uint(0)
infix fun _uint.and(x: Int) = _uint(0)
infix fun _uint.and(x: _sint) = _uint(0)
infix fun _uint.and(x: _uint) = _uint(0)
infix fun Int.or(x: _sint) = _uint(0)
infix fun Int.or(x: _uint) = _uint(0)
infix fun _sint.or(x: Int) = _uint(0)
infix fun _sint.or(x: _sint) = _uint(0)
infix fun _sint.or(x: _uint) = _uint(0)
infix fun _uint.or(x: Int) = _uint(0)
infix fun _uint.or(x: _sint) = _uint(0)
infix fun _uint.or(x: _uint) = _uint(0)
infix fun Int.xor(x: _sint) = _uint(0)
infix fun Int.xor(x: _uint) = _uint(0)
infix fun _sint.xor(x: Int) = _uint(0)
infix fun _sint.xor(x: _sint) = _uint(0)
infix fun _sint.xor(x: _uint) = _uint(0)
infix fun _uint.xor(x: Int) = _uint(0)
infix fun _uint.xor(x: _sint) = _uint(0)
infix fun _uint.xor(x: _uint) = _uint(0)

infix fun Int.nand(x: _sint) = _uint(0)
infix fun Int.nand(x: _uint) = _uint(0)
infix fun _bool.nand(x: _bool) = false
infix fun _sint.nand(x: Int) = _uint(0)
infix fun _sint.nand(x: _sint) = _uint(0)
infix fun _sint.nand(x: _uint) = _uint(0)
infix fun _uint.nand(x: Int) = _uint(0)
infix fun _uint.nand(x: _sint) = _uint(0)
infix fun _uint.nand(x: _uint) = _uint(0)
infix fun Int.nor(x: _sint) = _uint(0)
infix fun Int.nor(x: _uint) = _uint(0)
infix fun _bool.nor(x: _bool) = false
infix fun _sint.nor(x: Int) = _uint(0)
infix fun _sint.nor(x: _sint) = _uint(0)
infix fun _sint.nor(x: _uint) = _uint(0)
infix fun _uint.nor(x: Int) = _uint(0)
infix fun _uint.nor(x: _sint) = _uint(0)
infix fun _uint.nor(x: _uint) = _uint(0)
infix fun Int.xnor(x: _sint) = _uint(0)
infix fun Int.xnor(x: _uint) = _uint(0)
infix fun _bool.xnor(x: _bool) = false
infix fun _sint.xnor(x: Int) = _uint(0)
infix fun _sint.xnor(x: _sint) = _uint(0)
infix fun _sint.xnor(x: _uint) = _uint(0)
infix fun _uint.xnor(x: Int) = _uint(0)
infix fun _uint.xnor(x: _sint) = _uint(0)
infix fun _uint.xnor(x: _uint) = _uint(0)

infix fun _bool.cat(x: _sint) = _uint(0)
infix fun _bool.cat(x: _uint) = _uint(0)
infix fun _sint.cat(x: _bool) = _uint(0)
infix fun _sint.cat(x: _sint) = _uint(0)
infix fun _sint.cat(x: _uint) = _uint(0)
infix fun _uint.cat(x: _bool) = _uint(0)
infix fun _uint.cat(x: _sint) = _uint(0)
infix fun _uint.cat(x: _uint) = _uint(0)

// assignment
infix fun _sint.put_add(x: Int) {}
infix fun _sint.put_add(x: _sint) {}
infix fun _uint.put_add(x: Int) {}
infix fun _uint.put_add(x: _sint) {}
infix fun _uint.put_add(x: _uint) {}
infix fun _sint.reg_add(x: Int) {}
infix fun _sint.reg_add(x: _sint) {}
infix fun _uint.reg_add(x: Int) {}
infix fun _uint.reg_add(x: _sint) {}
infix fun _uint.reg_add(x: _uint) {}

infix fun _sint.put_sub(x: Int) {}
infix fun _sint.put_sub(x: _sint) {}
infix fun _uint.put_sub(x: Int) {}
infix fun _uint.put_sub(x: _sint) {}
infix fun _uint.put_sub(x: _uint) {}
infix fun _sint.reg_sub(x: Int) {}
infix fun _sint.reg_sub(x: _sint) {}
infix fun _uint.reg_sub(x: Int) {}
infix fun _uint.reg_sub(x: _sint) {}
infix fun _uint.reg_sub(x: _uint) {}

infix fun _sint.put_mul(x: Int) {}
infix fun _sint.put_mul(x: _sint) {}
infix fun _uint.put_mul(x: Int) {}
infix fun _uint.put_mul(x: _sint) {}
infix fun _uint.put_mul(x: _uint) {}
infix fun _sint.reg_mul(x: Int) {}
infix fun _sint.reg_mul(x: _sint) {}
infix fun _uint.reg_mul(x: Int) {}
infix fun _uint.reg_mul(x: _sint) {}
infix fun _uint.reg_mul(x: _uint) {}

infix fun _sint.put_sl(s: Int) {}
infix fun _sint.put_sl(s: _uint) {}
infix fun _uint.put_sl(s: Int) {}
infix fun _uint.put_sl(s: _uint) {}
infix fun _sint.reg_sl(s: Int) {}
infix fun _sint.reg_sl(s: _uint) {}
infix fun _uint.reg_sl(s: Int) {}
infix fun _uint.reg_sl(s: _uint) {}
infix fun _sint.put_sr(s: Int) {}
infix fun _sint.put_sr(s: _uint) {}
infix fun _uint.put_sr(s: Int) {}
infix fun _uint.put_sr(s: _uint) {}
infix fun _sint.reg_sr(s: Int) {}
infix fun _sint.reg_sr(s: _uint) {}
infix fun _uint.reg_sr(s: Int) {}
infix fun _uint.reg_sr(s: _uint) {}

infix fun _uint.put_rotl(s: Int) {}
infix fun _uint.put_rotl(s: _uint) {}
infix fun _uint.reg_rotl(s: Int) {}
infix fun _uint.reg_rotl(s: _uint) {}
infix fun _uint.put_rotr(s: Int) {}
infix fun _uint.put_rotr(s: _uint) {}
infix fun _uint.reg_rotr(s: Int) {}
infix fun _uint.reg_rotr(s: _uint) {}

infix fun _bool.put_and(x: _bool) {}
infix fun _uint.put_and(x: Int) {}
infix fun _uint.put_and(x: _sint) {}
infix fun _uint.put_and(x: _uint) {}
infix fun _bool.reg_and(x: _bool) {}
infix fun _uint.reg_and(x: Int) {}
infix fun _uint.reg_and(x: _sint) {}
infix fun _uint.reg_and(x: _uint) {}
infix fun _bool.put_or(x: _bool) {}
infix fun _uint.put_or(x: Int) {}
infix fun _uint.put_or(x: _sint) {}
infix fun _uint.put_or(x: _uint) {}
infix fun _bool.reg_or(x: _bool) {}
infix fun _uint.reg_or(x: Int) {}
infix fun _uint.reg_or(x: _sint) {}
infix fun _uint.reg_or(x: _uint) {}
infix fun _bool.put_xor(x: _bool) {}
infix fun _uint.put_xor(x: Int) {}
infix fun _uint.put_xor(x: _sint) {}
infix fun _uint.put_xor(x: _uint) {}
infix fun _bool.reg_xor(x: _bool) {}
infix fun _uint.reg_xor(x: Int) {}
infix fun _uint.reg_xor(x: _sint) {}
infix fun _uint.reg_xor(x: _uint) {}

infix fun _bool.put_nand(x: _bool) {}
infix fun _uint.put_nand(x: Int) {}
infix fun _uint.put_nand(x: _sint) {}
infix fun _uint.put_nand(x: _uint) {}
infix fun _bool.reg_nand(x: _bool) {}
infix fun _uint.reg_nand(x: Int) {}
infix fun _uint.reg_nand(x: _sint) {}
infix fun _uint.reg_nand(x: _uint) {}
infix fun _bool.put_nor(x: _bool) {}
infix fun _uint.put_nor(x: Int) {}
infix fun _uint.put_nor(x: _sint) {}
infix fun _uint.put_nor(x: _uint) {}
infix fun _bool.reg_nor(x: _bool) {}
infix fun _uint.reg_nor(x: Int) {}
infix fun _uint.reg_nor(x: _sint) {}
infix fun _uint.reg_nor(x: _uint) {}
infix fun _bool.put_xnor(x: _bool) {}
infix fun _uint.put_xnor(x: Int) {}
infix fun _uint.put_xnor(x: _sint) {}
infix fun _uint.put_xnor(x: _uint) {}
infix fun _bool.reg_xnor(x: _bool) {}
infix fun _uint.reg_xnor(x: Int) {}
infix fun _uint.reg_xnor(x: _sint) {}
infix fun _uint.reg_xnor(x: _uint) {}

// function
fun rep(n: Int, x: _bool) = _uint(0)
fun rep(n: Int, x: _sint) = _uint(0)
fun rep(n: Int, x: _uint) = _uint(0)

fun inv(x: _sint) = _uint(0)
fun inv(x: _uint) = _uint(0)

fun red_and(x: _sint) = false
fun red_and(x: _uint) = false
fun red_or(x: _sint) = false
fun red_or(x: _uint) = false
fun red_nand(x: _sint) = false
fun red_nand(x: _uint) = false
fun red_nor(x: _sint) = false
fun red_nor(x: _uint) = false
fun red_xor(x: _sint) = false
fun red_xor(x: _uint) = false
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

fun signed(x: _uint) = _sint(0)
fun unsigned(x: _sint) = _uint(0)

// other
fun len(x: _bool) = 0
fun len(x: _data) = 0
fun <_T: _instance> len(x: _array<_T>) = 0

fun ext(len: Int, x: _sint) = _sint(0)
fun ext(len: Int, x: _uint) = _uint(0)
fun tru(len: Int, x: _sint) = _sint(0)
fun tru(len: Int, x: _uint) = _uint(0)
