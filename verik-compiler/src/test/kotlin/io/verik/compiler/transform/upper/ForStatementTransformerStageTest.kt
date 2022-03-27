/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.upper

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class ForStatementTransformerStageTest : BaseTest() {

    @Test
    fun `transform forEach rangeTo`() {
        driveTextFileTest(
            """
                fun f() {
                    @Suppress("ForEachParameterNotUsed")
                    (0 .. 7).forEach { }
                }
            """.trimIndent(),
            """
                function automatic void f();
                    for (int __0 = 0; __0 <= 7; __0++) begin
                        int it;
                        it = __0;
                    end
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `transform forEach until`() {
        driveTextFileTest(
            """
                fun f() {
                    @Suppress("ForEachParameterNotUsed")
                    (0 until 8).forEach { }
                }
            """.trimIndent(),
            """
                function automatic void f();
                    for (int __0 = 0; __0 < 8; __0++) begin
                        int it;
                        it = __0;
                    end
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `transform forEach Queue`() {
        driveTextFileTest(
            """
                val a: Queue<Boolean> = nc()
                fun f() {
                    @Suppress("ForEachParameterNotUsed")
                    a.forEach { }
                }
            """.trimIndent(),
            """
                logic a [$];

                function automatic void f();
                    for (int __0 = 0; __0 < a.size(); __0++) begin
                        logic it;
                        it = a[__0];
                    end
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }

    @Test
    fun `transform forEach ArrayList`() {
        driveTextFileTest(
            """
                val a = ArrayList<Boolean>()
                fun f() {
                    @Suppress("ForEachParameterNotUsed")
                    a.forEach { }
                }
            """.trimIndent(),
            """
                verik_pkg::ArrayList#(.E(logic)) a = verik_pkg::ArrayList#(.E(logic))::__new();

                function automatic void f();
                    for (int __0 = 0; __0 < a.size(); __0++) begin
                        logic it;
                        it = a.get(__0);
                    end
                endfunction : f
            """.trimIndent()
        ) { it.nonRootPackageTextFiles[0] }
    }
}
