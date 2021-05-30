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

package io.verik.compiler.serialize

import io.verik.compiler.util.BaseTest
import io.verik.compiler.util.TestDriver
import io.verik.compiler.util.assertOutputTextEquals
import org.junit.jupiter.api.Test

internal class ProjectSerializerTest: BaseTest() {

    @Test
    fun `serialize module`() {
        val projectContext = TestDriver.serialize("""
            class M: Module()
        """.trimIndent())
        val expected = """
            module M;
            
            endmodule: M
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `serialize basic class`() {
        val projectContext = TestDriver.serialize("""
            class C
        """.trimIndent())
        val expected = """
            class C;
            
            endclass: C
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }

    @Test
    fun `serialize basic function`() {
        val projectContext = TestDriver.serialize("""
            class C {
                fun f() {}
            }
        """.trimIndent())
        val expected = """
            class C;
            
                function void f();
                endfunction: f
            
            endclass: C
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputTextFiles.last()
        )
    }
}