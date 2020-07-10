package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.KtRuleParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Copyright (c) 2020 Francis Wang

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
        assertThrows<VkParseException> {
            VkDeclaration(rule)
        }
    }

    @Test
    fun `class declaration illegal modifier`() {
        val rule = KtRuleParser.parseDeclaration("override class _m: _module")
        assertThrows<VkParseException> {
            VkDeclaration(rule)
        }
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
        assertThrows<VkParseException> {
            VkDeclaration(rule)
        }
    }

    @Test
    fun `function declaration illegal modifier`() {
        val rule = KtRuleParser.parseDeclaration("enum fun f()")
        assertThrows<VkParseException> {
            VkDeclaration(rule)
        }
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
        assertThrows<VkParseException> {
            VkDeclaration(rule)
        }
    }

    @Test
    fun `property declaration illegal modifier`() {
        val rule = KtRuleParser.parseDeclaration("enum val a = _bool()")
        assertThrows<VkParseException> {
            VkDeclaration(rule)
        }
    }
}