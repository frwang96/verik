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

package io.verik.importer.preprocess

import io.verik.importer.test.BaseTest
import org.junit.jupiter.api.Test

internal class MacroEvaluatorTest : BaseTest() {

    @Test
    fun `macro simple`() {
        drivePreprocessorTest(
            """
                `define X(x) x
                `X(abc)
            """.trimIndent(),
            "abc"
        )
    }

    @Test
    fun `macro brackets`() {
        drivePreprocessorTest(
            """
                `define X(x) x
                `X(())
            """.trimIndent(),
            "()"
        )
    }

    @Test
    fun `macro incorrect arguments`() {
        driveMessageTest(
            """
                `define X(x) x
                `X
            """.trimIndent(),
            false,
            "Incorrect number of macro arguments: Expected 1 actual 0"
        )
    }
}
