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

typealias _bool = Boolean

operator fun Boolean.Companion.invoke() = false

fun _bool.is_unknown() = false

fun _bool.is_floating() = false

fun _bool.pack() = _uint(0)

infix fun _bool.put(x: _bool?) {}

infix fun _bool.reg(x: _bool?) {}

infix fun _bool.drive(x: _bool?) {}

infix fun _bool.con(x: _bool?) {}
