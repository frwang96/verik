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

package io.verik.core.assert

import org.junit.jupiter.api.Assertions.assertEquals

fun assertStringEquals(expected: Any, actual: Any) {
    assertEquals(expected.toString().trim(), actual.toString().trim())
}

inline fun <reified T: Exception> assertThrowsMessage(message: String, block:() -> Unit) {
    try {
        block()
    } catch (exception: Exception) {
        if (exception is T) {
            assertEquals(message, exception.message)
            return
        } else throw AssertionError("expected ${T::class.simpleName} but got ${exception::class.simpleName}")
    }
    throw AssertionError("expected exception but none was thrown")
}