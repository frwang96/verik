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

@file:Suppress("UNUSED_PARAMETER")

package io.verik.common.data

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
