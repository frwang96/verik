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
