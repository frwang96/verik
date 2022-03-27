/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.interpret

import io.verik.importer.test.BaseTest
import io.verik.importer.test.findDeclaration
import org.junit.jupiter.api.Test

internal class CompanionObjectInterpreterStageTest : BaseTest() {

    @Test
    fun `interpret companion object function`() {
        driveElementTest(
            """
                class c;
                    static function void f();
                    endfunction
                endclass
            """.trimIndent(),
            CompanionObjectInterpreterStage::class,
            "KtClass(c, [CompanionObject([SvFunction(*)])], [], [], SimpleDescriptor(Class), 1)"
        ) { it.findDeclaration("c") }
    }

    @Test
    fun `interpret companion object property`() {
        driveElementTest(
            """
                class c;
                    static logic x;
                endclass
            """.trimIndent(),
            CompanionObjectInterpreterStage::class,
            "KtClass(c, [CompanionObject([Property(*)])], [], [], SimpleDescriptor(Class), 1)"
        ) { it.findDeclaration("c") }
    }

    @Test
    fun `interpret companion object property not supported`() {
        driveMessageTest(
            """
                class c #(type T);
                    static logic x;
                endclass
            """.trimIndent(),
            false,
            "Parameterized static property is not supported: x"
        )
    }
}
