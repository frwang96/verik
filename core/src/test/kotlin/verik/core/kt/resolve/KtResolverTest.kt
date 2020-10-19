/*
 * Copyright 2020 Francis Wang
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

package verik.core.kt.resolve

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.kt.KtPrimaryProperty
import verik.core.kt.KtUtil
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_UNIT

internal class KtResolverTest {

    @Test
    fun `property with constructor function`() {
        val declarations = """
            class _m: _module
        """.trimIndent()
        val string = """
            val m = _m()
        """.trimIndent()
        val declaration = KtUtil.resolveDeclaration(string, declarations) as KtPrimaryProperty
        assert(declaration.type != null)
    }

    @Test
    fun `property with primary function`() {
        val declarations = """
            fun f() {}
        """.trimIndent()
        val string = """
            val y = f()
        """.trimIndent()
        val declaration = KtUtil.resolveDeclaration(string, declarations) as KtPrimaryProperty
        assertEquals(TYPE_UNIT, declaration.type)
    }

    @Test
    fun `property with primary property`() {
        val declarations = """
            val x = 0
        """.trimIndent()
        val string = """
            val y = x
        """.trimIndent()
        val declaration = KtUtil.resolveDeclaration(string, declarations) as KtPrimaryProperty
        assertEquals(TYPE_INT, declaration.type)
    }

    @Test
    fun `property with primary property in type`() {
        val declarations = """
            class _m: _module {
                val x = 0
            }
        """.trimIndent()
        val string = """
            val y = _m().x
        """.trimIndent()
        val declaration = KtUtil.resolveDeclaration(string, declarations) as KtPrimaryProperty
        assertEquals(TYPE_INT, declaration.type)
    }

    @Test
    fun `property with enum entry`() {
        val declarations = """
            enum class _op(override val value: _int): _enum {
                ADD(0), SUB(1)
            }
        """.trimIndent()
        val string = """
            val op = _op.ADD
        """.trimIndent()
        val declaration = KtUtil.resolveDeclaration(string, declarations) as KtPrimaryProperty
        assert(declaration.type != null)
    }
}