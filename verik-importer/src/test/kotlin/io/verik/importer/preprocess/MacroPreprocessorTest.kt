/*
 * SPDX-License-Identifier: Apache-2.0
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
    fun `directive macro file`() {
        drivePreprocessorTest(
            "`__FILE__",
            "\"/src/test.sv\""
        )
    }

    @Test
    fun `directive macro line`() {
        drivePreprocessorTest(
            "`__LINE__",
            "1"
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
