/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.interpret

import io.verik.importer.test.BaseTest
import io.verik.importer.test.findDeclaration
import org.junit.jupiter.api.Test

internal class ClassInterpreterStageTest : BaseTest() {

    @Test
    fun `interpret class`() {
        driveElementTest(
            """
                class c;
                endclass
                class d extends c;
                endclass
            """.trimIndent(),
            ClassInterpreterStage::class,
            "KtClass(d, [], [], [], ReferenceDescriptor(c, c, c, []), 1)"
        ) { it.findDeclaration("d") }
    }

    @Test
    fun `interpret module`() {
        driveElementTest(
            """
                module m(input x);
                endmodule
            """.trimIndent(),
            ClassInterpreterStage::class,
            """
                KtClass(
                    m, [], [],
                    [KtValueParameter(x, SimpleDescriptor(Boolean), [In], 1, 0)], SimpleDescriptor(Module), 0
                )
            """.trimIndent()
        ) { it.findDeclaration("m") }
    }

    @Test
    fun `interpret struct`() {
        driveElementTest(
            """
                typedef struct {
                    logic x;
                } s;
            """.trimIndent(),
            ClassInterpreterStage::class,
            """
                KtClass(
                    s, [], [],
                    [KtValueParameter(x, SimpleDescriptor(Boolean), [], 1, 0)], SimpleDescriptor(Struct), 0
                )
            """.trimIndent()
        ) { it.findDeclaration("s") }
    }
}
