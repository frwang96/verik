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

package io.verik.stubs

import io.verik.assert.assertThrowsMessage
import io.verik.common.collections.*
import io.verik.common.data.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class TypeCheckerTest {

    @Test
    fun `unsupported type`() {
        assertThrowsMessage<IllegalArgumentException>("data type array not supported") {
            TypeChecker.check(_array(1, _uint(8)), StubEntry("x", array(1, uint(8, 0))))
        }
    }

    @Test
    fun `type match`() {
        assertDoesNotThrow {
            TypeChecker.check(_uint(8), StubEntry("x", uint(8, 0)))
        }
    }

    @Test
    fun `type width mismatch`() {
        assertThrowsMessage<IllegalArgumentException>("width mismatch for x expected 8 but was 16") {
            TypeChecker.check(_uint(8), StubEntry("x", uint(16, 0)))
        }
    }
}