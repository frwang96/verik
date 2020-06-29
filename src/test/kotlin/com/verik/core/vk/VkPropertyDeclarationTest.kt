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
        val tree = KtTree.parsePropertyDeclaration("val a = _bool()")
        val propertyDeclaration = VkPropertyDeclaration(tree)
        assertEquals(propertyDeclaration, VkPropertyDeclaration(listOf(), listOf(), "a", VkBoolType, LinePos(0, 0)))
    }
    @Test
    fun `declaration with annotation`() {
        val tree = KtTree.parsePropertyDeclaration("@input val a = _bool()")
        val propertyDeclaration = VkPropertyDeclaration(tree)
        assertEquals(propertyDeclaration, VkPropertyDeclaration(listOf(VkPropertyAnnotation.INPUT), listOf(), "a", VkBoolType, LinePos(0, 0)))
    }
    @Test
    fun `declaration with modifier`() {
        val tree = KtTree.parsePropertyDeclaration("const val a = _bool()")
        val propertyDeclaration = VkPropertyDeclaration(tree)
        assertEquals(propertyDeclaration, VkPropertyDeclaration(listOf(), listOf(VkPropertyModifier.CONST), "a", VkBoolType, LinePos(0, 0)))
    }

    @Test
    fun `illegal annotation`() {
        assertThrows<VkParseException> {
            val tree = KtTree.parsePropertyDeclaration("@annotation val a = _bool()")
            VkPropertyDeclaration(tree)
        }
    }
    @Test
    fun `illegal modifier`() {
        assertThrows<VkParseException> {
            val tree = KtTree.parsePropertyDeclaration("enum val a = _bool()")
            VkPropertyDeclaration(tree)
        }
    }
}