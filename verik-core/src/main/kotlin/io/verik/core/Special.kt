/*
 * Copyright (c) 2021 Francis Wang
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

@file:Suppress("unused", "UNUSED_PARAMETER")

package io.verik.core

/**
 * Returns a value with all bits set to X.
 * @param T the type of the value
 */
fun <T> x(): T {
    throw VerikException()
}

/**
 * Returns a value with all bits set to Z.
 * @param T the type of the value
 */
fun <T> z(): T {
    throw VerikException()
}

/**
 * Inject [content] as SystemVerilog to use unsupported language constructs.
 */
fun sv(content: String) {
    throw VerikException()
}
