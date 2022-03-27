/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.transform.post

import io.verik.importer.test.BaseTest
import io.verik.importer.test.findDeclaration
import org.junit.jupiter.api.Test

internal class SuperDescriptorTransformerStageTest : BaseTest() {

    @Test
    fun `replace super descriptor`() {
        driveElementTest(
            """
                class c #(type T) extends T;
                endclass
            """.trimIndent(),
            SuperDescriptorTransformerStage::class,
            "KtClass(c, [], [TypeParameter(T, null, 0)], [], SimpleDescriptor(Class), 1)"
        ) { it.findDeclaration("c") }
    }
}
