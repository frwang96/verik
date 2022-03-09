/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
