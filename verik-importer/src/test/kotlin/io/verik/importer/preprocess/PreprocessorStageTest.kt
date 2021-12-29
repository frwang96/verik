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

internal class PreprocessorStageTest : BaseTest() {

    @Test
    fun `directive timescale`() {
        drivePreprocessorTest(
            "`timescale 1ns / 1ns",
            ""
        )
    }

    @Test
    fun `directive ifndef`() {
        drivePreprocessorTest(
            """
                `ifndef X
                    abc
                `endif
            """.trimIndent(),
            "abc"
        )
    }

    @Test
    fun `directive ifdef`() {
        drivePreprocessorTest(
            """
                `define X
                `ifdef X
                    abc
                `endif
            """.trimIndent(),
            "abc"
        )
    }

    @Test
    fun `directive endif unmatched`() {
        driveMessageTest(
            "`endif",
            false,
            "Unmatched endif directive"
        )
    }

    @Test
    fun `directive symbol`() {
        drivePreprocessorTest(
            """
                `define X abc
                `X
            """.trimIndent(),
            "abc"
        )
    }

    @Test
    fun `directive symbol undefined`() {
        driveMessageTest(
            "`X",
            false,
            "Undefined macro: X"
        )
    }
}
