package com.verik.core.vk

import com.verik.core.kt.KtTree
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Copyright (c) 2020 Francis Wang

internal class VkPortTest {

    @Test
    fun `input bool`() {
        val tree = KtTree.parseDeclaration("@input val a = _bool()")
        assert(VkPropertyDeclaration.isPropertyDeclaration(tree))
        val propertyDeclaration = VkPropertyDeclaration(tree)
        assert(VkPort.isPort(propertyDeclaration))
        val port = VkPort(propertyDeclaration)
        assertEquals(VkPort(VkPortType.INPUT, "a", VkBoolType), port)
    }
    @Test
    fun `output uint`() {
        val tree = KtTree.parseDeclaration("@output val a = _uint(1)")
        assert(VkPropertyDeclaration.isPropertyDeclaration(tree))
        val propertyDeclaration = VkPropertyDeclaration(tree)
        assert(VkPort.isPort(propertyDeclaration))
        val port = VkPort(propertyDeclaration)
        assertEquals(VkPort(VkPortType.OUTPUT, "a", VkUintType(1)), port)
    }
    @Test
    fun `not a port`() {
        val tree = KtTree.parseDeclaration("val a = _uint(1)")
        assert(VkPropertyDeclaration.isPropertyDeclaration(tree))
        val propertyDeclaration = VkPropertyDeclaration(tree)
        assert(!VkPort.isPort(propertyDeclaration))
    }

    @Test
    fun `illegal annotations`() {
        val tree = KtTree.parseDeclaration("@input @rand val a = _uint(1)")
        assert(VkPropertyDeclaration.isPropertyDeclaration(tree))
        val propertyDeclaration = VkPropertyDeclaration(tree)
        assert(VkPort.isPort(propertyDeclaration))
        assertThrows<VkParseException> {
            VkPort(propertyDeclaration)
        }
    }
    @Test
    fun `illegal modifiers`() {
        val tree = KtTree.parseDeclaration("@input const val a = _uint(1)")
        assert(VkPropertyDeclaration.isPropertyDeclaration(tree))
        val propertyDeclaration = VkPropertyDeclaration(tree)
        assert(VkPort.isPort(propertyDeclaration))
        assertThrows<VkParseException> {
            VkPort(propertyDeclaration)
        }
    }
}