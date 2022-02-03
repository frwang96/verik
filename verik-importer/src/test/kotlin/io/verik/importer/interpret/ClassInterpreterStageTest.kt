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

internal class ClassInterpreterStageTest : BaseTest() {

    @Test
    fun `interpret class`() {
        driveElementTest(
            """
                class c;
                endclass
                class d extends c;
                endclass
            """.trimIndent(),
            ClassInterpreterStage::class,
            "KtClass(d, [], [], [], ReferenceDescriptor(c, c, c, []), 1)"
        ) { it.findDeclaration("d") }
    }

    @Test
    fun `interpret module`() {
        driveElementTest(
            """
                module m(input x);
                endmodule
            """.trimIndent(),
            ClassInterpreterStage::class,
            """
                KtClass(
                    m, [], [],
                    [KtValueParameter(x, SimpleDescriptor(Boolean), [In], 1, 0)], SimpleDescriptor(Module), 0
                )
            """.trimIndent()
        ) { it.findDeclaration("m") }
    }

    @Test
    fun `interpret struct`() {
        driveElementTest(
            """
                typedef struct {
                    logic x;
                } s;
            """.trimIndent(),
            ClassInterpreterStage::class,
            """
                KtClass(
                    s, [], [],
                    [KtValueParameter(x, SimpleDescriptor(Boolean), [], 1, 0)], SimpleDescriptor(Struct), 0
                )
            """.trimIndent()
        ) { it.findDeclaration("s") }
    }
}
