package com.verik.core.vk

import com.verik.core.kt.KtTree
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Copyright (c) 2020 Francis Wang

internal class VkPortTest {

    @Test
    fun `input bool`() {
        val tree = KtTree.parsePropertyDeclaration("@input val a = _bool()")
        val propertyDeclaration = VkPropertyDeclaration(tree)
        assert(VkPortType.isPortType(propertyDeclaration))
        val port = VkPort(propertyDeclaration)
        assertEquals(port, VkPort(VkPortType.INPUT, "a", VkBoolType))
    }
    @Test
    fun `output uint`() {
        val tree = KtTree.parsePropertyDeclaration("@output val a = _uint(1)")
        val propertyDeclaration = VkPropertyDeclaration(tree)
        assert(VkPortType.isPortType(propertyDeclaration))
        val port = VkPort(propertyDeclaration)
        assertEquals(port, VkPort(VkPortType.OUTPUT, "a", VkUintType(1)))
    }
    @Test
    fun `not a port`() {
        val tree = KtTree.parsePropertyDeclaration("val a = _uint(1)")
        val propertyDeclaration = VkPropertyDeclaration(tree)
        assert(!VkPortType.isPortType(propertyDeclaration))
    }

    @Test
    fun `illegal annotations`() {
        val tree = KtTree.parsePropertyDeclaration("@input @rand val a = _uint(1)")
        val propertyDeclaration = VkPropertyDeclaration(tree)
        assert(VkPortType.isPortType(propertyDeclaration))
        assertThrows<VkParseException> {
            VkPort(propertyDeclaration)
        }
    }
    @Test
    fun `illegal modifiers`() {
        val tree = KtTree.parsePropertyDeclaration("@input const val a = _uint(1)")
        val propertyDeclaration = VkPropertyDeclaration(tree)
        assert(VkPortType.isPortType(propertyDeclaration))
        assertThrows<VkParseException> {
            VkPort(propertyDeclaration)
        }
    }
}