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

package verik.core.vk

import verik.core.main.LineException
import verik.core.al.AlRuleParser
import verik.core.assertThrowsMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VkDeclarationTest {

    @Test
    fun `class declaration`() {
        val rule = AlRuleParser.parseDeclaration("class _m: _module")
        val declaration = VkDeclaration(rule)
        assert(declaration is VkClassDeclaration)
        val classDeclaration = declaration as VkClassDeclaration
        assertEquals(VkClassDeclaration(1, "_m", listOf(), listOf(), "_module", null), classDeclaration)
    }

    @Test
    fun `class declaration with annotation`() {
        val rule = AlRuleParser.parseDeclaration("@top class _m: _module")
        val declaration = VkDeclaration(rule)
        assert(declaration is VkClassDeclaration)
        val classDeclaration = declaration as VkClassDeclaration
        assertEquals(VkClassDeclaration(1, "_m", listOf(VkClassAnnotation.TOP), listOf(), "_module", null), classDeclaration)
    }

    @Test
    fun `class declaration with modifier`() {
        val rule = AlRuleParser.parseDeclaration("open class _m: _module")
        val declaration = VkDeclaration(rule)
        assert(declaration is VkClassDeclaration)
        val classDeclaration = declaration as VkClassDeclaration
        assertEquals(VkClassDeclaration(1, "_m", listOf(), listOf(VkClassModifier.OPEN), "_module", null), classDeclaration)
    }

    @Test
    fun `class declaration illegal annotation`() {
        val rule = AlRuleParser.parseDeclaration("@wire class _m: _module")
        assertThrowsMessage<LineException>("illegal class annotation") {
            VkDeclaration(rule)
        }
    }

    @Test
    fun `class declaration illegal modifier`() {
        val rule = AlRuleParser.parseDeclaration("override class _m: _module")
        assertThrowsMessage<LineException>("illegal class modifier") {
            VkDeclaration(rule)
        }
    }

    @Test
    fun `function declaration`() {
        val rule = AlRuleParser.parseDeclaration("fun f()")
        val declaration = VkDeclaration(rule)
        assert(declaration is VkFunctionDeclaration)
        val functionDeclaration = declaration as VkFunctionDeclaration
        assertEquals(VkFunctionDeclaration(1, "f", listOf(), listOf(), null), functionDeclaration)
    }

    @Test
    fun `function declaration with annotation`() {
        val rule = AlRuleParser.parseDeclaration("@initial fun f()")
        val declaration = VkDeclaration(rule)
        assert(declaration is VkFunctionDeclaration)
        val functionDeclaration = declaration as VkFunctionDeclaration
        assertEquals(VkFunctionDeclaration(1, "f", listOf(VkFunctionAnnotation.INITIAL), listOf(), null), functionDeclaration)
    }

    @Test
    fun `function declaration with modifier`() {
        val rule = AlRuleParser.parseDeclaration("override fun f()")
        val declaration = VkDeclaration(rule)
        assert(declaration is VkFunctionDeclaration)
        val functionDeclaration = declaration as VkFunctionDeclaration
        assertEquals(VkFunctionDeclaration(1, "f", listOf(), listOf(VkFunctionModifier.OVERRIDE), null), functionDeclaration)
    }

    @Test
    fun `function declaration illegal annotation`() {
        val rule = AlRuleParser.parseDeclaration("@wire fun f()")
        assertThrowsMessage<LineException>("illegal function annotation") {
            VkDeclaration(rule)
        }
    }

    @Test
    fun `function declaration illegal modifier`() {
        val rule = AlRuleParser.parseDeclaration("enum fun f()")
        assertThrowsMessage<LineException>("illegal function modifier") {
            VkDeclaration(rule)
        }
    }

    @Test
    fun `property declaration`() {
        val rule = AlRuleParser.parseDeclaration("val a = _bool()")
        val declaration = VkDeclaration(rule)
        assert (declaration is VkPropertyDeclaration)
        val propertyDeclaration = declaration as VkPropertyDeclaration
        val expected = VkPropertyDeclaration(1, "a", listOf(),
                VkExpressionCallable(1, VkExpressionIdentifier(1, "_bool"), listOf()))
        assertEquals(expected, propertyDeclaration)
    }

    @Test
    fun `property declaration with annotation`() {
        val rule = AlRuleParser.parseDeclaration("@input val a = _bool()")
        val declaration = VkDeclaration(rule)
        assert (declaration is VkPropertyDeclaration)
        val propertyDeclaration = declaration as VkPropertyDeclaration
        val expected = VkPropertyDeclaration(1, "a", listOf(VkPropertyAnnotation.INPUT),
                VkExpressionCallable(1, VkExpressionIdentifier(1, "_bool"), listOf()))
        assertEquals(expected, propertyDeclaration)
    }

    @Test
    fun `property declaration illegal annotation`() {
        val rule = AlRuleParser.parseDeclaration("@annotation val a = _bool()")
        assertThrowsMessage<LineException>("illegal property annotation") {
            VkDeclaration(rule)
        }
    }

    @Test
    fun `property declaration illegal modifier`() {
        val rule = AlRuleParser.parseDeclaration("enum val a = _bool()")
        assertThrowsMessage<LineException>("illegal property modifier") {
            VkDeclaration(rule)
        }
    }
}