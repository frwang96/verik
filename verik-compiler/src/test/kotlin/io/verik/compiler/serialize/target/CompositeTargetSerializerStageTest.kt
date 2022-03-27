/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.serialize.target

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class CompositeTargetSerializerStageTest : BaseTest() {

    @Test
    fun `target class`() {
        driveTextFileTest(
            """
                val a: ArrayList<Boolean> = nc()
            """.trimIndent(),
            """
                package verik_pkg;
                
                    class ArrayList #(type E = int);
                
                        E queue [${'$'}];
                
                    endclass : ArrayList
                
                endpackage : verik_pkg
            """.trimIndent()
        ) { it.targetPackageTextFile!! }
    }

    @Test
    fun `target function`() {
        driveTextFileTest(
            """
                val a: ArrayList<Boolean> = nc()
                fun f() {
                    a.add(false)
                }
            """.trimIndent(),
            """
                package verik_pkg;
                
                    class ArrayList #(type E = int);
                
                        E queue [${'$'}];
                
                        function automatic void add(E e);
                            queue.push_back(e);
                        endfunction : add
                
                    endclass : ArrayList
                
                endpackage : verik_pkg
            """.trimIndent()
        ) { it.targetPackageTextFile!! }
    }
}
