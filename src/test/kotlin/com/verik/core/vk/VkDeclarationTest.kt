package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.KtTree
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
            val tree = KtTree.parseDeclaration("class _m: _module")
            val declaration = VkDeclaration(tree)
            assert(declaration is VkClassDeclaration)
            val classDeclaration = declaration as VkClassDeclaration
            Assertions.assertEquals(VkClassDeclaration(listOf(), listOf(), "_m", "_module", null, LinePos(0, 0)), classDeclaration)
        }

        @Test
        fun `declaration with annotation`() {
            val tree = KtTree.parseDeclaration("@top class _m: _module")
            val declaration = VkDeclaration(tree)
            assert(declaration is VkClassDeclaration)
            val classDeclaration = declaration as VkClassDeclaration
            Assertions.assertEquals(VkClassDeclaration(listOf(VkClassAnnotation.TOP), listOf(), "_m", "_module", null, LinePos(0, 0)), classDeclaration)
        }

        @Test
        fun `declaration with modifier`() {
            val tree = KtTree.parseDeclaration("open class _m: _module")
            val declaration = VkDeclaration(tree)
            assert(declaration is VkClassDeclaration)
            val classDeclaration = declaration as VkClassDeclaration
            Assertions.assertEquals(VkClassDeclaration(listOf(), listOf(VkClassModifier.OPEN), "_m", "_module", null, LinePos(0, 0)), classDeclaration)
        }

        @Test
        fun `illegal annotation`() {
            val tree = KtTree.parseDeclaration("@wire class _m: _module")
            assertThrows<VkParseException> {
                VkDeclaration(tree)
            }
        }

        @Test
        fun `illegal modifier`() {
            val tree = KtTree.parseDeclaration("const class _m: _module")
            assertThrows<VkParseException> {
                VkDeclaration(tree)
            }
        }
    }

    @Nested
    inner class Function {

        @Test
        fun `bare declaration`() {
            val tree = KtTree.parseDeclaration("fun f()")
            val declaration = VkDeclaration(tree)
            assert(declaration is VkFunctionDeclaration)
            val functionDeclaration = declaration as VkFunctionDeclaration
            Assertions.assertEquals(VkFunctionDeclaration(listOf(), listOf(), "f", null, LinePos(0, 0)), functionDeclaration)
        }

        @Test
        fun `declaration with annotation`() {
            val tree = KtTree.parseDeclaration("@initial fun f()")
            val declaration = VkDeclaration(tree)
            assert(declaration is VkFunctionDeclaration)
            val functionDeclaration = declaration as VkFunctionDeclaration
            Assertions.assertEquals(VkFunctionDeclaration(listOf(VkFunctionAnnotation.INITIAL), listOf(), "f", null, LinePos(0, 0)), functionDeclaration)
        }

        @Test
        fun `declaration with modifier`() {
            val tree = KtTree.parseDeclaration("override fun f()")
            val declaration = VkDeclaration(tree)
            assert(declaration is VkFunctionDeclaration)
            val functionDeclaration = declaration as VkFunctionDeclaration
            Assertions.assertEquals(VkFunctionDeclaration(listOf(), listOf(VkFunctionModifier.OVERRIDE), "f", null, LinePos(0, 0)), functionDeclaration)
        }

        @Test
        fun `illegal annotation`() {
            val tree = KtTree.parseDeclaration("@wire fun f()")
            assertThrows<VkParseException> {
                VkDeclaration(tree)
            }
        }

        @Test
        fun `illegal modifier`() {
            val tree = KtTree.parseDeclaration("const fun f()")
            assertThrows<VkParseException> {
                VkDeclaration(tree)
            }
        }
    }

    @Nested
    inner class Property {

        @Test
        fun `bare declaration`() {
            val tree = KtTree.parseDeclaration("val a = _bool()")
            val declaration = VkDeclaration(tree)
            assert (declaration is VkPropertyDeclaration)
            val propertyDeclaration = declaration as VkPropertyDeclaration
            Assertions.assertEquals(VkPropertyDeclaration(listOf(), listOf(), "a", VkBoolType, LinePos(0, 0)), propertyDeclaration)
        }

        @Test
        fun `declaration with annotation`() {
            val tree = KtTree.parseDeclaration("@input val a = _bool()")
            val declaration = VkDeclaration(tree)
            assert (declaration is VkPropertyDeclaration)
            val propertyDeclaration = declaration as VkPropertyDeclaration
            Assertions.assertEquals(VkPropertyDeclaration(listOf(VkPropertyAnnotation.INPUT), listOf(), "a", VkBoolType, LinePos(0, 0)), propertyDeclaration)
        }

        @Test
        fun `declaration with modifier`() {
            val tree = KtTree.parseDeclaration("const val a = _bool()")
            val declaration = VkDeclaration(tree)
            assert (declaration is VkPropertyDeclaration)
            val propertyDeclaration = declaration as VkPropertyDeclaration
            Assertions.assertEquals(VkPropertyDeclaration(listOf(), listOf(VkPropertyModifier.CONST), "a", VkBoolType, LinePos(0, 0)), propertyDeclaration)
        }

        @Test
        fun `illegal annotation`() {
            val tree = KtTree.parseDeclaration("@annotation val a = _bool()")
            assertThrows<VkParseException> {
                VkDeclaration(tree)
            }
        }

        @Test
        fun `illegal modifier`() {
            val tree = KtTree.parseDeclaration("enum val a = _bool()")
            assertThrows<VkParseException> {
                VkDeclaration(tree)
            }
        }
    }
}