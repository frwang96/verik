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

package io.verik.common.collections

import io.verik.common.*
import io.verik.common.data.*

class _array<TYPE: _instance>(override val _TYPE: TYPE, val SIZE: Int): _class, _data, _iterable<TYPE>(_TYPE) {

    operator fun get(n: Int) = _TYPE

    operator fun get(n: _uint) = _TYPE
}

fun <TYPE: _instance> array(_TYPE: TYPE, SIZE: Int) = _array(_TYPE, 0)

fun <TYPE: _instance> array(_TYPE: TYPE, SIZE: Int, x: TYPE) = _array(_TYPE, 0)

fun <TYPE: _instance> array(_TYPE: TYPE, vararg  x: TYPE) = _array(_TYPE, 0)

infix fun <TYPE: _instance> _array<TYPE>.put(x: _array<TYPE>?) {}

infix fun <TYPE: _instance> _array<TYPE>.reg(x: _array<TYPE>?) {}

infix fun <TYPE: _instance> _array<TYPE>.drive(x: _array<TYPE>?) {}

infix fun <TYPE: _instance> _array<TYPE>.con(x: _array<TYPE>?) {}