package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.KtTree
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Copyright (c) 2020 Francis Wang

internal class VkPropertyDeclarationTest {

    @Test
    fun `bare declaration`() {
        val tree = KtTree.parseDeclaration("val a = _bool()")
        assert(VkPropertyDeclaration.isPropertyDeclaration(tree))
        val propertyDeclaration = VkPropertyDeclaration(tree)
        assertEquals(VkPropertyDeclaration(listOf(), listOf(), "a", VkBoolType, LinePos(0, 0)), propertyDeclaration)
    }
    @Test
    fun `declaration with annotation`() {
        val tree = KtTree.parseDeclaration("@input val a = _bool()")
        assert(VkPropertyDeclaration.isPropertyDeclaration(tree))
        val propertyDeclaration = VkPropertyDeclaration(tree)
        assertEquals(VkPropertyDeclaration(listOf(VkPropertyAnnotation.INPUT), listOf(), "a", VkBoolType, LinePos(0, 0)), propertyDeclaration)
    }
    @Test
    fun `declaration with modifier`() {
        val tree = KtTree.parseDeclaration("const val a = _bool()")
        assert(VkPropertyDeclaration.isPropertyDeclaration(tree))
        val propertyDeclaration = VkPropertyDeclaration(tree)
        assertEquals(VkPropertyDeclaration(listOf(), listOf(VkPropertyModifier.CONST), "a", VkBoolType, LinePos(0, 0)), propertyDeclaration)
    }

    @Test
    fun `illegal annotation`() {
        val tree = KtTree.parseDeclaration("@annotation val a = _bool()")
        assert(VkPropertyDeclaration.isPropertyDeclaration(tree))
        assertThrows<VkParseException> {
            VkPropertyDeclaration(tree)
        }
    }
    @Test
    fun `illegal modifier`() {
        val tree = KtTree.parseDeclaration("enum val a = _bool()")
        assert(VkPropertyDeclaration.isPropertyDeclaration(tree))
        assertThrows<VkParseException> {
            VkPropertyDeclaration(tree)
        }
    }
}