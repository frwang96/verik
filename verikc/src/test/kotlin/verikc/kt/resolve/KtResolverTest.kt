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

package verikc.kt.resolve

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.base.symbol.Symbol
import verikc.kt.KtResolveUtil
import verikc.kt.ast.KtPrimaryProperty
import verikc.lang.LangSymbol.TYPE_INT

internal class KtResolverTest {

    @Test
    fun `property with type constructor function`() {
        val fileContext = """
            class _m: _module
        """.trimIndent()
        val string = """
            val m = _m()
        """.trimIndent()
        val declaration = KtResolveUtil.resolveDeclaration(fileContext, string) as KtPrimaryProperty
        assertEquals(Symbol(3), declaration.typeSymbol)
    }

    @Test
    fun `property with function`() {
        val fileContext = """
            fun f(): _int {}
        """.trimIndent()
        val string = """
            val y = f()
        """.trimIndent()
        val declaration = KtResolveUtil.resolveDeclaration(fileContext, string) as KtPrimaryProperty
        assertEquals(TYPE_INT, declaration.typeSymbol)
    }

    @Test
    fun `property with primary property`() {
        val fileContext = """
            val x = 0
        """.trimIndent()
        val string = """
            val y = x
        """.trimIndent()
        val declaration = KtResolveUtil.resolveDeclaration(fileContext, string) as KtPrimaryProperty
        assertEquals(TYPE_INT, declaration.typeSymbol)
    }

    @Test
    fun `property with primary property in type`() {
        val fileContext = """
            class _m: _module {
                val x = 0
            }
        """.trimIndent()
        val string = """
            val y = _m().x
        """.trimIndent()
        val declaration = KtResolveUtil.resolveDeclaration(fileContext, string) as KtPrimaryProperty
        assertEquals(TYPE_INT, declaration.typeSymbol)
    }

    @Test
    fun `property with enum entry`() {
        val fileContext = """
            enum class _op(override val value: _int): _enum {
                ADD(0), SUB(1)
            }
        """.trimIndent()
        val string = """
            val op = _op.ADD
        """.trimIndent()
        val declaration = KtResolveUtil.resolveDeclaration(fileContext, string) as KtPrimaryProperty
        assertEquals(Symbol(3), declaration.typeSymbol)
    }
}
