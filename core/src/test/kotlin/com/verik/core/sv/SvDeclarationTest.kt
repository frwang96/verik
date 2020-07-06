package com.verik.core.sv

import com.verik.core.SourceBuilder
import com.verik.core.assertStringEquals
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class SvDeclarationTest {

    @Test
    fun `continuous assignment`() {
        val declaration = SvContinuousAssignment(SvFunctionExpression("bassign", SvFunctionType.OPERATOR, listOf(
                SvLiteralExpression("x"),
                SvLiteralExpression("y")
        )), 0)
        val builder = SourceBuilder()
        declaration.build(builder)
        assertStringEquals("assign x = y;", builder)
    }
}