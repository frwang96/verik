/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.cast

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class CasterStageTest : BaseTest() {

    @Test
    fun `project empty`() {
        driveElementTest(
            "",
            CasterStage::class,
            """
                Project(
                    [Package(test, [File([])], [], REGULAR_NON_ROOT)],
                    Package(<root>, [], [], REGULAR_ROOT),
                    [],
                    Package(imported, [], [], IMPORTED_ROOT)
                )
            """.trimIndent()
        ) { it }
    }

    @Test
    fun `file class`() {
        driveElementTest(
            """
                class C
            """.trimIndent(),
            CasterStage::class,
            "File([KtClass(C, C, Any, [], [], PrimaryConstructor(C, C, [], null), 0, 0)])"
        ) { it.files().first() }
    }

    @Test
    fun `file classes`() {
        driveElementTest(
            """
                class C0
                class C1
            """.trimIndent(),
            CasterStage::class,
            """
                File([
                    KtClass(C0, C0, Any, [], [], PrimaryConstructor(C0, C0, [], null), 0, 0),
                    KtClass(C1, C1, Any, [], [], PrimaryConstructor(C1, C1, [], null), 0, 0)
                ])
            """.trimIndent()
        ) { it.files().first() }
    }
}
