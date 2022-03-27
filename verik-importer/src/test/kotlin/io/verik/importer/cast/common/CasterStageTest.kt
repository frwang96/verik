/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.common

import io.verik.importer.test.BaseTest
import org.junit.jupiter.api.Test

internal class CasterStageTest : BaseTest() {

    @Test
    fun `cast project `() {
        driveElementTest(
            "",
            CasterStage::class,
            "Project([])"
        ) { it }
    }

    @Test
    fun `cast project with module`() {
        driveElementTest(
            """
                module m;
                endmodule
            """.trimIndent(),
            CasterStage::class,
            "Project([Module(m, [], [], [])])"
        ) { it }
    }
}
