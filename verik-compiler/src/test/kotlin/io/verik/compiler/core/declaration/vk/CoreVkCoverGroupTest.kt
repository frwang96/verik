/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.core.common.Core
import io.verik.compiler.test.CoreDeclarationTest
import org.junit.jupiter.api.Test

internal class CoreVkCoverGroupTest : CoreDeclarationTest() {

    @Test
    fun `sample getCoverage`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.CoverGroup.F_sample,
                Core.Vk.CoverGroup.F_coverage
            ),
            """
                class CG : CoverGroup()
                val cg = CG()
                var x = 0.0
                fun f() { 
                    cg.sample()
                    x = cg.coverage()
                }
            """.trimIndent(),
            """
                covergroup CG();

                endgroup : CG

                function automatic void f();
                    cg.sample();
                    x = cg.get_coverage();
                endfunction : f
            """.trimIndent()
        )
    }
}
