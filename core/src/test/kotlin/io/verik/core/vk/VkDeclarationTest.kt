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

package io.verik.core.vk

import io.verik.core.FileLine
import io.verik.core.FileLineException
import io.verik.core.assert.assertThrowsMessage
import io.verik.core.kt.KtRuleParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VkDeclarationTest {

    @Test
    fun `class declaration`() {
        val rule = KtRuleParser.parseDeclaration("class _m: _module")
        val declaration = VkDeclaration(rule)
        assert(declaration is VkClassDeclaration)
        val classDeclaration = declaration as VkClassDeclaration
        assertEquals(VkClassDeclaration("_m", FileLine(1), listOf(), listOf(), "_module", null), classDeclaration)
    }

    @Test
    fun `class declaration with annotation`() {
        val rule = KtRuleParser.parseDeclaration("@top class _m: _module")
        val declaration = VkDeclaration(rule)
        assert(declaration is VkClassDeclaration)
        val classDeclaration = declaration as VkClassDeclaration
        assertEquals(VkClassDeclaration("_m", FileLine(1), listOf(VkClassAnnotation.TOP), listOf(), "_module", null), classDeclaration)
    }

    @Test
    fun `class declaration with modifier`() {
        val rule = KtRuleParser.parseDeclaration("open class _m: _module")
        val declaration = VkDeclaration(rule)
        assert(declaration is VkClassDeclaration)
        val classDeclaration = declaration as VkClassDeclaration
        assertEquals(VkClassDeclaration("_m", FileLine(1), listOf(), listOf(VkClassModifier.OPEN), "_module", null), classDeclaration)
    }

    @Test
    fun `class declaration illegal annotation`() {
        val rule = KtRuleParser.parseDeclaration("@wire class _m: _module")
        assertThrowsMessage<FileLineException>("illegal class annotation") {
            VkDeclaration(rule)
        }
    }

    @Test
    fun `class declaration illegal modifier`() {
        val rule = KtRuleParser.parseDeclaration("override class _m: _module")
        assertThrowsMessage<FileLineException>("illegal class modifier") {
            VkDeclaration(rule)
        }
    }

    @Test
    fun `class declaration reserved name`() {
        val rule = KtRuleParser.parseDeclaration("class _config_mirror: _struct")
        assertThrowsMessage<FileLineException>("identifier _config_mirror is reserved") {
            VkDeclaration(rule)
        }
    }

    @Test
    fun `function declaration`() {
        val rule = KtRuleParser.parseDeclaration("fun f()")
        val declaration = VkDeclaration(rule)
        assert(declaration is VkFunctionDeclaration)
        val functionDeclaration = declaration as VkFunctionDeclaration
        assertEquals(VkFunctionDeclaration("f", FileLine(1), listOf(), listOf(), null), functionDeclaration)
    }

    @Test
    fun `function declaration with annotation`() {
        val rule = KtRuleParser.parseDeclaration("@initial fun f()")
        val declaration = VkDeclaration(rule)
        assert(declaration is VkFunctionDeclaration)
        val functionDeclaration = declaration as VkFunctionDeclaration
        assertEquals(VkFunctionDeclaration("f", FileLine(1), listOf(VkFunctionAnnotation.INITIAL), listOf(), null), functionDeclaration)
    }

    @Test
    fun `function declaration with modifier`() {
        val rule = KtRuleParser.parseDeclaration("override fun f()")
        val declaration = VkDeclaration(rule)
        assert(declaration is VkFunctionDeclaration)
        val functionDeclaration = declaration as VkFunctionDeclaration
        assertEquals(VkFunctionDeclaration("f", FileLine(1), listOf(), listOf(VkFunctionModifier.OVERRIDE), null), functionDeclaration)
    }

    @Test
    fun `function declaration illegal annotation`() {
        val rule = KtRuleParser.parseDeclaration("@wire fun f()")
        assertThrowsMessage<FileLineException>("illegal function annotation") {
            VkDeclaration(rule)
        }
    }

    @Test
    fun `function declaration illegal modifier`() {
        val rule = KtRuleParser.parseDeclaration("enum fun f()")
        assertThrowsMessage<FileLineException>("illegal function modifier") {
            VkDeclaration(rule)
        }
    }

    @Test
    fun `property declaration`() {
        val rule = KtRuleParser.parseDeclaration("val a = _bool()")
        val declaration = VkDeclaration(rule)
        assert (declaration is VkPropertyDeclaration)
        val propertyDeclaration = declaration as VkPropertyDeclaration
        val expected = VkPropertyDeclaration("a", FileLine(1), listOf(),
                VkCallableExpression(FileLine(1), VkIdentifierExpression(FileLine(1), "_bool"), listOf()))
        assertEquals(expected, propertyDeclaration)
    }

    @Test
    fun `property declaration with annotation`() {
        val rule = KtRuleParser.parseDeclaration("@input val a = _bool()")
        val declaration = VkDeclaration(rule)
        assert (declaration is VkPropertyDeclaration)
        val propertyDeclaration = declaration as VkPropertyDeclaration
        val expected = VkPropertyDeclaration("a", FileLine(1), listOf(VkPropertyAnnotation.INPUT),
                VkCallableExpression(FileLine(1), VkIdentifierExpression(FileLine(1), "_bool"), listOf()))
        assertEquals(expected, propertyDeclaration)
    }

    @Test
    fun `property declaration illegal annotation`() {
        val rule = KtRuleParser.parseDeclaration("@annotation val a = _bool()")
        assertThrowsMessage<FileLineException>("illegal property annotation") {
            VkDeclaration(rule)
        }
    }

    @Test
    fun `property declaration illegal modifier`() {
        val rule = KtRuleParser.parseDeclaration("enum val a = _bool()")
        assertThrowsMessage<FileLineException>("illegal property modifier") {
            VkDeclaration(rule)
        }
    }
}