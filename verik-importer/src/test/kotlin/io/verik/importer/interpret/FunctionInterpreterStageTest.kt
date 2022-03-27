/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.interpret

import io.verik.importer.test.BaseTest
import io.verik.importer.test.findDeclaration
import org.junit.jupiter.api.Test

internal class FunctionInterpreterStageTest : BaseTest() {

    @Test
    fun `interpret function`() {
        driveElementTest(
            """
                function int f();
                endfunction
            """.trimIndent(),
            FunctionInterpreterStage::class,
            "KtFunction(f, [], SimpleDescriptor(Int), [], 0, 0)"
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `interpret task`() {
        driveElementTest(
            """
                task t(logic x);
                endtask
            """.trimIndent(),
            FunctionInterpreterStage::class,
            """
                KtFunction(
                    t,
                    [KtValueParameter(x, SimpleDescriptor(Boolean), [], null, 0)],
                    SimpleDescriptor(Unit), [Task], 0, 0
                )
            """.trimIndent()
        ) { it.findDeclaration("t") }
    }

    @Test
    fun `interpret constructor`() {
        driveElementTest(
            """
                class c;
                    function new(logic x);
                    endfunction
                endclass
            """.trimIndent(),
            FunctionInterpreterStage::class,
            "KtConstructor([KtValueParameter(x, SimpleDescriptor(Boolean), [], null, 0)])"
        ) { it.findDeclaration("new") }
    }
}
