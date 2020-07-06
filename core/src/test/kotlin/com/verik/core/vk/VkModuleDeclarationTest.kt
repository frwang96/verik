package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class VkModuleDeclarationTest {

    @Test
    fun `empty declaration`() {
        val rule = KtRuleParser.parseDeclaration("@comp val m = _m()")
        val propertyDeclaration = VkDeclaration(rule) as VkPropertyDeclaration
        val moduleDeclaration = VkModuleDeclaration(propertyDeclaration)
        val expected = VkModuleDeclaration("m", VkNamedType("_m"), listOf(), LinePos(1, 7))
        assertEquals(expected, moduleDeclaration)
    }
}