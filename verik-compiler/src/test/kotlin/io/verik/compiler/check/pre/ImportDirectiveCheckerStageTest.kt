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

package io.verik.compiler.check.pre

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.TestErrorException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ImportDirectiveCheckerStageTest : BaseTest() {

    @Test
    fun `import not found`() {
        assertThrows<TestErrorException> {
            driveTest(
                ImportDirectiveCheckerStage::class,
                """
                    import java.time.LocalDateTime
                """.trimIndent()
            )
        }.apply {
            assertEquals("Package not found: java.time", message)
        }
    }

    @Test
    fun `import not found all under`() {
        assertThrows<TestErrorException> {
            driveTest(
                ImportDirectiveCheckerStage::class,
                """
                    import java.time.*
                """.trimIndent()
            )
        }.apply {
            assertEquals("Package not found: java.time", message)
        }
    }
}
