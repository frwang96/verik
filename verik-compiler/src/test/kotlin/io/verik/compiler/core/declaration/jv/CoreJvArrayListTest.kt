/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.jv

import io.verik.compiler.core.common.Core
import io.verik.compiler.test.CoreDeclarationTest
import org.junit.jupiter.api.Test

internal class CoreJvArrayListTest : CoreDeclarationTest() {

    @Test
    fun `serialize add get set size`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Jv.Util.ArrayList.F_add_E,
                Core.Jv.Util.ArrayList.F_get_Int,
                Core.Jv.Util.ArrayList.F_set_Int_E,
                Core.Jv.Util.ArrayList.P_size
            ),
            """
                val a = ArrayList<Int>()
                var x = 0
                fun f() {
                    a.add(0)
                    x = a[0]
                    a[0] = x
                    x = a.size
                }
            """.trimIndent(),
            """
                function automatic void f();
                    a.add(0);
                    x = a.get(0);
                    a.set(0, x);
                    x = a.size();
                endfunction : f
            """.trimIndent()
        )
    }
}
