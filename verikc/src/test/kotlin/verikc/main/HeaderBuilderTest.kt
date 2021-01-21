/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.main

import org.junit.jupiter.api.Test
import verikc.assertStringEquals
import verikc.kt.KtParseUtil

internal class HeaderBuilderTest {

    private val header = """
        @file:Suppress("FunctionName", "unused", "UNUSED_PARAMETER", "UnusedImport")
        
        package test
        
        import verik.base.*
        import verik.data.*
    """.trimIndent()

    @Test
    fun `header enum`() {
        val string = """
            package test
            enum class E(val value: Ubit) {}
        """.trimIndent()
        val expected = """
            fun t_E() = E.values()[0]

            infix fun E.set(x: E) {}
        """.trimIndent()
        assertStringEquals(
            header + "\n\n" + expected,
            HeaderBuilder.build(KtParseUtil.parsePkg(string))!!
        )
    }

    @Test
    fun `header class`() {
        val string = """
            package test
            class C: _class()
        """.trimIndent()
        val expected = """
            fun t_C() = C()
            
            infix fun C.set(x: C) {}
            
            fun i_C() = C()
        """.trimIndent()
        assertStringEquals(
            header + "\n\n" + expected,
            HeaderBuilder.build(KtParseUtil.parsePkg(string))!!
        )
    }
}