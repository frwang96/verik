/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.preprocess

import io.verik.importer.test.BaseTest
import org.junit.jupiter.api.Test

internal class PreprocessorStageTest : BaseTest() {

    @Test
    fun `lexer unrecognized token`() {
        driveMessageTest(
            "`0",
            true,
            "Preprocessor lexer error: Unable to recognize token"
        )
    }

    @Test
    fun `directive timescale`() {
        drivePreprocessorTest(
            "`timescale 1ns / 1ns",
            ""
        )
    }

    @Test
    fun `directive line`() {
        drivePreprocessorTest(
            "`line 1",
            ""
        )
    }
}
