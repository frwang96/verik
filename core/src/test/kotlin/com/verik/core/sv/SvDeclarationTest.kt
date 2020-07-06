package com.verik.core.sv

import com.verik.core.LinePos
import com.verik.core.SourceBuilder
import com.verik.core.assertStringEquals
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class SvDeclarationTest {

    @Test
    fun `continuous assignment`() {
        val declaration = SvContinuousAssignment(SvFunctionExpression(LinePos.ZERO, "bassign", SvFunctionType.OPERATOR, listOf(
                SvLiteralExpression(LinePos.ZERO, "x"),
                SvLiteralExpression(LinePos.ZERO, "y")
        )), LinePos.ZERO)
        val builder = SourceBuilder()
        declaration.build(builder)
        assertStringEquals("assign x = y;", builder)
    }
}