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

package verik.stubs

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import verik.common.data.*

internal class TypeCheckerTest {

    @Test
    fun `unsupported type`() {
        assertThrowsMessage<IllegalArgumentException>("type _sint not supported") {
            TypeChecker.check(_sint(8), StubEntry("x", sint(8, 0)))
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
        assertThrowsMessage<IllegalArgumentException>("size mismatch for x expected 8 but was 16") {
            TypeChecker.check(_uint(8), StubEntry("x", uint(16, 0)))
        }
    }
}