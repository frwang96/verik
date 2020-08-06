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

class _list<TYPE: _instance>(override val _TYPE: TYPE): _class, _iterable<TYPE>(_TYPE) {

    fun size() = 0
}

fun <TYPE: _instance> list(_TYPE: TYPE, size: Int) = _list(_TYPE)

fun <TYPE: _instance> list(_TYPE: TYPE, size: Int, x: TYPE) = _list(_TYPE)

fun <TYPE: _instance> list(_TYPE: TYPE, vararg x: TYPE) = _list(_TYPE)
