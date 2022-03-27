/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.preprocess

import io.verik.importer.test.BaseTest
import org.junit.jupiter.api.Test

internal class BasePreprocessorTest : BaseTest() {

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
    fun `directive else`() {
        drivePreprocessorTest(
            """
                `ifdef X
                `else
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
            true,
            "Unmatched directive"
        )
    }
}
