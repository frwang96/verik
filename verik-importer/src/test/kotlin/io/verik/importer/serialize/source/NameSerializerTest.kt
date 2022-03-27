/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.serialize.source

import io.verik.importer.test.BaseTest
import org.junit.jupiter.api.Test

internal class NameSerializerTest : BaseTest() {

    @Test
    fun `serialize name keyword`() {
        driveTextFileTest(
            """
                logic val;
            """.trimIndent(),
            """
                var `val`: Boolean = imp()
            """.trimIndent()
        )
    }
}
