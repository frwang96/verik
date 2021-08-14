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

package io.verik.compiler.cast

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.TestDriver
import io.verik.compiler.util.TestErrorException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ImportDirectiveCheckerTest : BaseTest() {

    @Test
    fun `import not found`() {
        assertThrows<TestErrorException> {
            TestDriver.cast(
                """
                    import java.time.LocalDateTime
                """.trimIndent()
            )
        }.apply {
            assertEquals("Import package not found: java.time", message)
        }
    }

    @Test
    fun `import not found all under`() {
        assertThrows<TestErrorException> {
            TestDriver.cast(
                """
                    import java.time.*
                """.trimIndent()
            )
        }.apply {
            assertEquals("Import package not found: java.time", message)
        }
    }
}