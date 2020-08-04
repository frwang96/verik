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

infix fun Int.until(x: _uint) = _range()

infix fun _uint.until(x: Int) = _range()

infix fun _uint.until(x: _uint) = _range()

operator fun Int.rangeTo(x: _uint) = _range()

operator fun _uint.rangeTo(x: Int) = _range()

operator fun _uint.rangeTo(x: _uint) = _range()

operator fun IntRange.contains(x: _uint) = false
