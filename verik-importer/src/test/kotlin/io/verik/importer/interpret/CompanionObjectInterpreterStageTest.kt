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
