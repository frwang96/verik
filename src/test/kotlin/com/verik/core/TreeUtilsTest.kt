package com.verik.core

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

class TreeUtilsTest {

    @Test
    fun `parse`() {
        val tree = parseKotlinFile("val x = 1")
    }
}
