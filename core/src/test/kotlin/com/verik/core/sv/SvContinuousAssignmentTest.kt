package com.verik.core.sv

import com.verik.core.LinePos
import com.verik.core.SourceBuilder
import com.verik.core.assertStringEquals
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class SvContinuousAssignmentTest {

    @Test
    fun `continuous assignment`() {
        val continuousAssignment = SvContinuousAssignment(SvOperatorExpression(LinePos.ZERO, SvOperatorType.BASSIGN, listOf(
                SvLiteralExpression(LinePos.ZERO, "x"),
                SvLiteralExpression(LinePos.ZERO, "y")
        )), LinePos.ZERO)
        val builder = SourceBuilder()
        continuousAssignment.build(builder)
        assertStringEquals("assign x = y;", builder)
    }
}