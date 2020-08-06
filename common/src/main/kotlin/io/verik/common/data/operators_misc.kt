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

fun rep(n: Int, x: _bool) = _uint(0)

fun rep(n: Int, x: _sint) = _uint(0)

fun rep(n: Int, x: _uint) = _uint(0)

fun max(x: _sint, y: _sint) = _sint(0)

fun max(x: _uint, y: _uint) = _uint(0)

fun max(x: Int, y: _sint) = _sint(0)

fun max(x: Int, y: _uint) = _uint(0)

fun max(x: _sint, y: Int) = _sint(0)

fun max(x: _uint, y: Int) = _uint(0)

fun min(x: _sint, y: _sint) = _sint(0)

fun min(x: _uint, y: _uint) = _uint(0)

fun min(x: Int, y: _sint) = _sint(0)

fun min(x: Int, y: _uint) = _uint(0)

fun min(x: _sint, y: Int) = _sint(0)

fun min(x: _uint, y: Int) = _uint(0)

fun signed(x: _uint) = _sint(0)

fun unsigned(x: _sint) = _uint(0)

fun ext(len: Int, x: _sint) = _sint(0)

fun ext(len: Int, x: _uint) = _uint(0)

fun tru(len: Int, x: _sint) = _sint(0)

fun tru(len: Int, x: _uint) = _uint(0)
