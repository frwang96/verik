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

class _array<_T: _instance>(override val T: _T, val LEN: Int): _class, _data, _iterable<_T>(T) {

    operator fun get(n: Int) = T

    operator fun get(n: _uint) = T
}

fun <_T: _instance> array(T: _T, LEN: Int) = _array(T, 0)

fun <_T: _instance> array(T: _T, LEN: Int, x: _T) = _array(T, 0)

fun <_T: _instance> array(T: _T, vararg  x: _T) = _array(T, 0)

infix fun <_T: _instance> _array<_T>.put(x: _array<_T>?) {}

infix fun <_T: _instance> _array<_T>.reg(x: _array<_T>?) {}

infix fun <_T: _instance> _array<_T>.drive(x: _array<_T>?) {}

infix fun <_T: _instance> _array<_T>.con(x: _array<_T>?) {}
