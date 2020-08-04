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
