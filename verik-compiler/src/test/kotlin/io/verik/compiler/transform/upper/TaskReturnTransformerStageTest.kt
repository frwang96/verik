/*
 * Copyright (c) 2022 Francis Wang
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

internal class TaskReturnTransformerStageTest : BaseTest() {

    @Test
    fun `internal return`() {
        driveTextFileTest(
            """
                @Task
                fun f(): Boolean { return false }
            """.trimIndent(),
            """
                task automatic f(
                    output logic __0
                );
                    __0 = 1'b0;
                    return;
                endtask : f
            """.trimIndent()
        ) { it.regularPackageTextFiles[0] }
    }

    @Test
    fun `external return`() {
        driveTextFileTest(
            """
                var x = false
                @Task
                fun f(): Boolean { return false }
                @Task
                fun g() { x = f() }
            """.trimIndent(),
            """
                logic x = 1'b0;

                task automatic f(
                    output logic __0
                );
                    __0 = 1'b0;
                    return;
                endtask : f

                task automatic g();
                    logic __1;
                    f(.__0(__1));
                    x = __1;
                endtask : g
            """.trimIndent()
        ) { it.regularPackageTextFiles[0] }
    }
}
