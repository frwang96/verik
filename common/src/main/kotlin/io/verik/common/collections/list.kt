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

class _list<_T: _instance>(override val T: _T): _class, _iterable<_T>(T) {

    fun size() = 0

    operator fun get(n: Int) = T

    operator fun get(n: _uint) = T
}

fun <_T: _instance> list(T: _T, size: Int) = _list(T)

fun <_T: _instance> list(T: _T, size: Int, x: _T) = _list(T)

fun <_T: _instance> list(T: _T, vararg x: _T) = _list(T)
