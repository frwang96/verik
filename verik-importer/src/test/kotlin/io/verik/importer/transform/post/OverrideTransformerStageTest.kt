/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.transform.post

import io.verik.importer.test.BaseTest
import io.verik.importer.test.findDeclaration
import org.junit.jupiter.api.Test

internal class OverrideTransformerStageTest : BaseTest() {

    @Test
    fun `override property`() {
        driveElementTest(
            """
                class c;
                    logic x;
                endclass

                class d extends c;
                    logic x;
                endclass
            """.trimIndent(),
            OverrideTransformerStage::class,
            "KtClass(d, [], [], [], ReferenceDescriptor(*), 1)"
        ) { it.findDeclaration("d") }
    }

    @Test
    fun `override function`() {
        driveElementTest(
            """
                class c;
                    function void f();
                    endfunction
                endclass

                class d extends c;
                    function void f();
                    endfunction
                endclass
            """.trimIndent(),
            OverrideTransformerStage::class,
            "KtClass(d, [KtFunction(f, [], SimpleDescriptor(Unit), [], 1, 1)], [], [], ReferenceDescriptor(*), 1)"
        ) { it.findDeclaration("d") }
    }

    @Test
    fun `override function type alias`() {
        driveElementTest(
            """
                class c;
                    function void f();
                    endfunction
                endclass
                
                typedef c t;

                class d extends t;
                    function void f();
                    endfunction
                endclass
            """.trimIndent(),
            OverrideTransformerStage::class,
            "KtClass(d, [KtFunction(f, [], SimpleDescriptor(Unit), [], 1, 1)], [], [], ReferenceDescriptor(*), 1)"
        ) { it.findDeclaration("d") }
    }

    @Test
    fun `override function remove default`() {
        driveElementTest(
            """
                class c;
                    function void f(int x = 0);
                    endfunction
                endclass

                class d extends c;
                    function void f(int x = 0);
                    endfunction
                endclass
            """.trimIndent(),
            OverrideTransformerStage::class,
            """
                KtClass(
                    d, [
                        KtFunction(f, [KtValueParameter(x, SimpleDescriptor(Int), [], null, 0)],
                        SimpleDescriptor(Unit), [], 1, 1)
                    ],
                    [], [], ReferenceDescriptor(c, c, c, []), 1
                )
            """.trimIndent()
        ) { it.findDeclaration("d") }
    }
}
