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
