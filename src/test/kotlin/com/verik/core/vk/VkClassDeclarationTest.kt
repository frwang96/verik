package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.KtTree
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Copyright (c) 2020 Francis Wang

internal class VkClassDeclarationTest {

    @Test
    fun `bare declaration`() {
        val tree = KtTree.parseTopLevelObject("class _m: _module")
        assert(VkClassDeclaration.isClassDeclaration(tree))
        val classDeclaration = VkClassDeclaration(tree)
        assertEquals(VkClassDeclaration(listOf(), listOf(), "_m", "_module", null, LinePos(0, 0)), classDeclaration)
    }

    @Test
    fun `declaration with annotation`() {
        val tree = KtTree.parseTopLevelObject("@top class _m: _module")
        assert(VkClassDeclaration.isClassDeclaration(tree))
        val classDeclaration = VkClassDeclaration(tree)
        assertEquals(VkClassDeclaration(listOf(VkClassAnnotation.TOP), listOf(), "_m", "_module", null, LinePos(0, 0)), classDeclaration)
    }

    @Test
    fun `declaration with modifier`() {
        val tree = KtTree.parseTopLevelObject("open class _m: _module")
        assert(VkClassDeclaration.isClassDeclaration(tree))
        val classDeclaration = VkClassDeclaration(tree)
        assertEquals(VkClassDeclaration(listOf(), listOf(VkClassModifier.OPEN), "_m", "_module", null, LinePos(0, 0)), classDeclaration)
    }

    @Test
    fun `illegal annotation`() {
        val tree = KtTree.parseTopLevelObject("@wire class _m: _module")
        assert(VkClassDeclaration.isClassDeclaration(tree))
        assertThrows<VkParseException> {
            VkClassDeclaration(tree)
        }
    }

    @Test
    fun `illegal modifier`() {
        val tree = KtTree.parseTopLevelObject("const class _m: _module")
        assert(VkClassDeclaration.isClassDeclaration(tree))
        assertThrows<VkParseException> {
            VkClassDeclaration(tree)
        }
    }
}