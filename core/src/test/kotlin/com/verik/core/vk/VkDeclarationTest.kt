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

package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.LinePosException
import com.verik.core.kt.KtRuleParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class VkDeclarationTest {

    @Test
    fun `class declaration`() {
        val rule = KtRuleParser.parseDeclaration("class _m: _module")
        val declaration = VkDeclaration(rule)
        assert(declaration is VkClassDeclaration)
        val classDeclaration = declaration as VkClassDeclaration
        assertEquals(VkClassDeclaration("_m", LinePos(1, 1), listOf(), listOf(), "_module", null), classDeclaration)
    }

    @Test
    fun `class declaration with annotation`() {
        val rule = KtRuleParser.parseDeclaration("@top class _m: _module")
        val declaration = VkDeclaration(rule)
        assert(declaration is VkClassDeclaration)
        val classDeclaration = declaration as VkClassDeclaration
        assertEquals(VkClassDeclaration("_m", LinePos(1, 6), listOf(VkClassAnnotation.TOP), listOf(), "_module", null), classDeclaration)
    }

    @Test
    fun `class declaration with modifier`() {
        val rule = KtRuleParser.parseDeclaration("open class _m: _module")
        val declaration = VkDeclaration(rule)
        assert(declaration is VkClassDeclaration)
        val classDeclaration = declaration as VkClassDeclaration
        assertEquals(VkClassDeclaration("_m", LinePos(1, 6), listOf(), listOf(VkClassModifier.OPEN), "_module", null), classDeclaration)
    }

    @Test
    fun `class declaration illegal annotation`() {
        val rule = KtRuleParser.parseDeclaration("@wire class _m: _module")
        val exception = assertThrows<LinePosException> {
            VkDeclaration(rule)
        }
        assertEquals("(1, 1) illegal class annotation", exception.message)
    }

    @Test
    fun `class declaration illegal modifier`() {
        val rule = KtRuleParser.parseDeclaration("override class _m: _module")
        val exception = assertThrows<LinePosException> {
            VkDeclaration(rule)
        }
        assertEquals("(1, 1) illegal class modifier", exception.message)
    }

    @Test
    fun `function declaration`() {
        val rule = KtRuleParser.parseDeclaration("fun f()")
        val declaration = VkDeclaration(rule)
        assert(declaration is VkFunctionDeclaration)
        val functionDeclaration = declaration as VkFunctionDeclaration
        assertEquals(VkFunctionDeclaration("f", LinePos(1, 1), listOf(), listOf(), null), functionDeclaration)
    }

    @Test
    fun `function declaration with annotation`() {
        val rule = KtRuleParser.parseDeclaration("@initial fun f()")
        val declaration = VkDeclaration(rule)
        assert(declaration is VkFunctionDeclaration)
        val functionDeclaration = declaration as VkFunctionDeclaration
        assertEquals(VkFunctionDeclaration("f", LinePos(1, 10), listOf(VkFunctionAnnotation.INITIAL), listOf(), null), functionDeclaration)
    }

    @Test
    fun `function declaration with modifier`() {
        val rule = KtRuleParser.parseDeclaration("override fun f()")
        val declaration = VkDeclaration(rule)
        assert(declaration is VkFunctionDeclaration)
        val functionDeclaration = declaration as VkFunctionDeclaration
        assertEquals(VkFunctionDeclaration("f", LinePos(1, 10), listOf(), listOf(VkFunctionModifier.OVERRIDE), null), functionDeclaration)
    }

    @Test
    fun `function declaration illegal annotation`() {
        val rule = KtRuleParser.parseDeclaration("@wire fun f()")
        val exception = assertThrows<LinePosException> {
            VkDeclaration(rule)
        }
        assertEquals("(1, 1) illegal function annotation", exception.message)
    }

    @Test
    fun `function declaration illegal modifier`() {
        val rule = KtRuleParser.parseDeclaration("enum fun f()")
        val exception = assertThrows<LinePosException> {
            VkDeclaration(rule)
        }
        assertEquals("(1, 1) illegal function modifier", exception.message)
    }

    @Test
    fun `property declaration`() {
        val rule = KtRuleParser.parseDeclaration("val a = _bool()")
        val declaration = VkDeclaration(rule)
        assert (declaration is VkPropertyDeclaration)
        val propertyDeclaration = declaration as VkPropertyDeclaration
        val expected = VkPropertyDeclaration("a", LinePos(1, 1), listOf(),
                VkCallableExpression(LinePos(1, 9), VkIdentifierExpression(LinePos(1, 9), "_bool"), listOf()))
        assertEquals(expected, propertyDeclaration)
    }

    @Test
    fun `property declaration with annotation`() {
        val rule = KtRuleParser.parseDeclaration("@input val a = _bool()")
        val declaration = VkDeclaration(rule)
        assert (declaration is VkPropertyDeclaration)
        val propertyDeclaration = declaration as VkPropertyDeclaration
        val expected = VkPropertyDeclaration("a", LinePos(1, 8), listOf(VkPropertyAnnotation.INPUT),
                VkCallableExpression(LinePos(1, 16), VkIdentifierExpression(LinePos(1, 16), "_bool"), listOf()))
        assertEquals(expected, propertyDeclaration)
    }

    @Test
    fun `property declaration illegal annotation`() {
        val rule = KtRuleParser.parseDeclaration("@annotation val a = _bool()")
        val exception = assertThrows<LinePosException> {
            VkDeclaration(rule)
        }
        assertEquals("(1, 1) illegal property annotation", exception.message)
    }

    @Test
    fun `property declaration illegal modifier`() {
        val rule = KtRuleParser.parseDeclaration("enum val a = _bool()")
        val exception = assertThrows<LinePosException> {
            VkDeclaration(rule)
        }
        assertEquals("(1, 1) illegal property modifier", exception.message)
    }
}