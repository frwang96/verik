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

internal class MacroPreprocessorTest : BaseTest() {

    @Test
    fun `directive undef`() {
        driveMessageTest(
            """
                `define X abc
                `undef X
                `X
            """.trimIndent(),
            true,
            "Undefined macro: X"
        )
    }

    @Test
    fun `directive undef all`() {
        driveMessageTest(
            """
                `define X abc
                `undefineall
                `X
            """.trimIndent(),
            true,
            "Undefined macro: X"
        )
    }

    @Test
    fun `directive macro`() {
        drivePreprocessorTest(
            """
                `define X abc
                `X
            """.trimIndent(),
            "abc"
        )
    }

    @Test
    fun `directive macro multiline`() {
        drivePreprocessorTest(
            """
                `define X abc\
                def
                `X
            """.trimIndent(),
            """
                abc
                def
            """.trimIndent()
        )
    }

    @Test
    fun `directive macro nested`() {
        drivePreprocessorTest(
            """
                `define X abc
                `define Y `X
                `Y
            """.trimIndent(),
            "abc"
        )
    }

    @Test
    fun `directive macro parenthesis`() {
        drivePreprocessorTest(
            """
                `define X (1)
                `X
            """.trimIndent(),
            "(1)"
        )
    }

    @Test
    fun `directive macro undefined`() {
        driveMessageTest(
            "`X",
            true,
            "Undefined macro: X"
        )
    }

    @Test
    fun `directive macro args`() {
        drivePreprocessorTest(
            """
                `define X(x) x
                `X(abc)
            """.trimIndent(),
            "abc"
        )
    }

    @Test
    fun `directive macro args brackets`() {
        drivePreprocessorTest(
            """
                `define X(x) x
                `X(())
            """.trimIndent(),
            "()"
        )
    }

    @Test
    fun `directive macro string`() {
        drivePreprocessorTest(
            """
                `define X(x) x
                `X("abc")
            """.trimIndent(),
            "\"abc\""
        )
    }

    @Test
    fun `directive macro args escape quotes`() {
        drivePreprocessorTest(
            """
                `define X(x) `"x`"
                `X(abc)
            """.trimIndent(),
            "\"abc\""
        )
    }

    @Test
    fun `directive macro args incorrect arguments`() {
        driveMessageTest(
            """
                `define X(x) x
                `X
            """.trimIndent(),
            true,
            "Incorrect number of macro arguments: Expected 1 actual 0"
        )
    }
}
