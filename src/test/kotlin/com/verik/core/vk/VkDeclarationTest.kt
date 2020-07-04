package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.KtRuleParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Copyright (c) 2020 Francis Wang

internal class VkDeclarationTest {

    @Nested
    inner class Class {

        @Test
        fun `bare declaration`() {
            val rule = KtRuleParser.parseDeclaration("class _m: _module")
            val declaration = VkDeclaration(rule)
            assert(declaration is VkClassDeclaration)
            val classDeclaration = declaration as VkClassDeclaration
            Assertions.assertEquals(VkClassDeclaration("_m", LinePos(0, 0), listOf(), listOf(), "_module", null), classDeclaration)
        }

        @Test
        fun `declaration with annotation`() {
            val rule = KtRuleParser.parseDeclaration("@top class _m: _module")
            val declaration = VkDeclaration(rule)
            assert(declaration is VkClassDeclaration)
            val classDeclaration = declaration as VkClassDeclaration
            Assertions.assertEquals(VkClassDeclaration("_m", LinePos(0, 0), listOf(VkClassAnnotation.TOP), listOf(), "_module", null), classDeclaration)
        }

        @Test
        fun `declaration with modifier`() {
            val rule = KtRuleParser.parseDeclaration("open class _m: _module")
            val declaration = VkDeclaration(rule)
            assert(declaration is VkClassDeclaration)
            val classDeclaration = declaration as VkClassDeclaration
            Assertions.assertEquals(VkClassDeclaration("_m", LinePos(0, 0), listOf(), listOf(VkClassModifier.OPEN), "_module", null), classDeclaration)
        }

        @Test
        fun `illegal annotation`() {
            val rule = KtRuleParser.parseDeclaration("@wire class _m: _module")
            assertThrows<VkParseException> {
                VkDeclaration(rule)
            }
        }

        @Test
        fun `illegal modifier`() {
            val rule = KtRuleParser.parseDeclaration("const class _m: _module")
            assertThrows<VkParseException> {
                VkDeclaration(rule)
            }
        }
    }

    @Nested
    inner class Function {

        @Test
        fun `bare declaration`() {
            val rule = KtRuleParser.parseDeclaration("fun f()")
            val declaration = VkDeclaration(rule)
            assert(declaration is VkFunctionDeclaration)
            val functionDeclaration = declaration as VkFunctionDeclaration
            Assertions.assertEquals(VkFunctionDeclaration("f", LinePos(0, 0), listOf(), listOf(), null), functionDeclaration)
        }

        @Test
        fun `declaration with annotation`() {
            val rule = KtRuleParser.parseDeclaration("@initial fun f()")
            val declaration = VkDeclaration(rule)
            assert(declaration is VkFunctionDeclaration)
            val functionDeclaration = declaration as VkFunctionDeclaration
            Assertions.assertEquals(VkFunctionDeclaration("f", LinePos(0, 0), listOf(VkFunctionAnnotation.INITIAL), listOf(), null), functionDeclaration)
        }

        @Test
        fun `declaration with modifier`() {
            val rule = KtRuleParser.parseDeclaration("override fun f()")
            val declaration = VkDeclaration(rule)
            assert(declaration is VkFunctionDeclaration)
            val functionDeclaration = declaration as VkFunctionDeclaration
            Assertions.assertEquals(VkFunctionDeclaration("f", LinePos(0, 0), listOf(), listOf(VkFunctionModifier.OVERRIDE), null), functionDeclaration)
        }

        @Test
        fun `illegal annotation`() {
            val rule = KtRuleParser.parseDeclaration("@wire fun f()")
            assertThrows<VkParseException> {
                VkDeclaration(rule)
            }
        }

        @Test
        fun `illegal modifier`() {
            val rule = KtRuleParser.parseDeclaration("const fun f()")
            assertThrows<VkParseException> {
                VkDeclaration(rule)
            }
        }
    }

    @Nested
    inner class Property {

        @Test
        fun `bare declaration`() {
            val rule = KtRuleParser.parseDeclaration("val a = _bool()")
            val declaration = VkDeclaration(rule)
            assert (declaration is VkPropertyDeclaration)
            val propertyDeclaration = declaration as VkPropertyDeclaration
            Assertions.assertEquals(VkPropertyDeclaration("a", LinePos(0, 0), listOf(), listOf(), VkBoolType), propertyDeclaration)
        }

        @Test
        fun `declaration with annotation`() {
            val rule = KtRuleParser.parseDeclaration("@input val a = _bool()")
            val declaration = VkDeclaration(rule)
            assert (declaration is VkPropertyDeclaration)
            val propertyDeclaration = declaration as VkPropertyDeclaration
            Assertions.assertEquals(VkPropertyDeclaration("a", LinePos(0, 0), listOf(VkPropertyAnnotation.INPUT), listOf(), VkBoolType), propertyDeclaration)
        }

        @Test
        fun `declaration with modifier`() {
            val rule = KtRuleParser.parseDeclaration("const val a = _bool()")
            val declaration = VkDeclaration(rule)
            assert (declaration is VkPropertyDeclaration)
            val propertyDeclaration = declaration as VkPropertyDeclaration
            Assertions.assertEquals(VkPropertyDeclaration("a", LinePos(0, 0), listOf(), listOf(VkPropertyModifier.CONST), VkBoolType), propertyDeclaration)
        }

        @Test
        fun `illegal annotation`() {
            val rule = KtRuleParser.parseDeclaration("@annotation val a = _bool()")
            assertThrows<VkParseException> {
                VkDeclaration(rule)
            }
        }

        @Test
        fun `illegal modifier`() {
            val rule = KtRuleParser.parseDeclaration("enum val a = _bool()")
            assertThrows<VkParseException> {
                VkDeclaration(rule)
            }
        }
    }
}