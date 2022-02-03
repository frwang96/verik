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
