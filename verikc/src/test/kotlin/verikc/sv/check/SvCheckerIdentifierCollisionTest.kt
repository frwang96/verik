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

package verikc.sv.check

import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.LineException
import verikc.sv.SvExtractUtil

internal class SvCheckerIdentifierCollisionTest {

    @Test
    fun `identifier reserved`() {
        val string = """
            val always = 0
        """.trimIndent()
        assertThrowsMessage<LineException>("identifier always is reserved in SystemVerilog") {
            SvExtractUtil.extractPrimaryProperty("", string)
        }
    }

    @Test
    fun `identifier collision component`() {
        val moduleContext = """
            val f = 0
        """.trimIndent()
        val string = """
            fun f() {}
        """.trimIndent()
        assertThrowsMessage<LineException>("identifier f has already been defined in the scope") {
            SvExtractUtil.extractModuleMethodBlock("", moduleContext, string)
        }
    }

    @Test
    fun `identifier collision class`() {
        val string = """
            class C: Class() {
                fun f() {}
                fun f(x: Int) {}
            }
        """.trimIndent()
        assertThrowsMessage<LineException>("identifier f has already been defined in the scope") {
            SvExtractUtil.extractCls("", string)
        }
    }
}