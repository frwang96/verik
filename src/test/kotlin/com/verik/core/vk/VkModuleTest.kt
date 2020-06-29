package com.verik.core.vk

import com.verik.core.kt.KtTree
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Copyright (c) 2020 Francis Wang

internal class VkModuleTest {

    @Test
    fun `simple module`() {
        val tree = KtTree.parseTopLevelObject("class _m: _module")
        assert(VkClassDeclaration.isClassDeclaration(tree))
        val classDeclaration = VkClassDeclaration(tree)
        assert(VkModule.isModule(classDeclaration))
        val module = VkModule(classDeclaration)
        assertEquals(VkModule(VkModuleElabType.REGULAR, false, "_m", listOf()), module)
    }
    @Test
    fun `module with port`() {
        val tree = KtTree.parseTopLevelObject("""
            class _m: _module {
                @input val a = _bool()
            }
        """.trimIndent())
        assert(VkClassDeclaration.isClassDeclaration(tree))
        val classDeclaration = VkClassDeclaration(tree)
        assert(VkModule.isModule(classDeclaration))
        val module = VkModule(classDeclaration)
        assertEquals(VkModule(VkModuleElabType.REGULAR, false, "_m",
                listOf(VkPort(VkPortType.INPUT, "a", VkBoolType))), module)
    }

    @Test
    fun `illegal elaboration type`() {
        val tree = KtTree.parseTopLevelObject("@top @extern class _m: _module")
        assert(VkClassDeclaration.isClassDeclaration(tree))
        val classDeclaration = VkClassDeclaration(tree)
        assert(VkModule.isModule(classDeclaration))
        assertThrows<VkParseException> {
            VkModule(classDeclaration)
        }
    }

    @Test
    fun `illegal modifier type`() {
        val tree = KtTree.parseTopLevelObject("enum class _m: _module")
        assert(VkClassDeclaration.isClassDeclaration(tree))
        val classDeclaration = VkClassDeclaration(tree)
        assert(VkModule.isModule(classDeclaration))
        assertThrows<VkParseException> {
            VkModule(classDeclaration)
        }
    }
}