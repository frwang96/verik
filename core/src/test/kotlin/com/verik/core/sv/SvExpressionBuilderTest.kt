package com.verik.core.sv

import com.verik.core.LinePos
import com.verik.core.assertStringEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class SvExpressionBuilderTest {

    @Nested
    inner class Assignment {

        @Test
        fun `blocking assignment`() {
            val expression = SvFunctionExpression(LinePos.ZERO, "bassign", SvFunctionType.OPERATOR, listOf(
                    SvLiteralExpression(LinePos.ZERO, "x"),
                    SvLiteralExpression(LinePos.ZERO, "y")
            ))
            assertStringEquals("x = y", expression.build())
        }
    }

    @Nested
    inner class Arithmetic {

        @Test
        fun `add expression`() {
            val expression = SvFunctionExpression(LinePos.ZERO, "add", SvFunctionType.OPERATOR, listOf(
                    SvIdentifierExpression(LinePos.ZERO, "x"),
                    SvIdentifierExpression(LinePos.ZERO, "y")
            ))
            assertStringEquals("x + y", expression.build())
        }

        @Test
        fun `precedence ordered`() {
            val expression = SvFunctionExpression(LinePos.ZERO, "add", SvFunctionType.OPERATOR, listOf(
                    SvIdentifierExpression(LinePos.ZERO, "x"),
                    SvFunctionExpression(LinePos.ZERO, "mul", SvFunctionType.OPERATOR, listOf(
                            SvIdentifierExpression(LinePos.ZERO, "y"),
                            SvIdentifierExpression(LinePos.ZERO, "z")
                    ))
            ))
            assertStringEquals("x + y * z", expression.build())
        }

        @Test
        fun `precedence not ordered`() {
            val expression = SvFunctionExpression(LinePos.ZERO, "mul", SvFunctionType.OPERATOR, listOf(
                    SvIdentifierExpression(LinePos.ZERO, "x"),
                    SvFunctionExpression(LinePos.ZERO, "add", SvFunctionType.OPERATOR, listOf(
                            SvIdentifierExpression(LinePos.ZERO, "y"),
                            SvIdentifierExpression(LinePos.ZERO, "z")
                    ))
            ))
            assertStringEquals("x * (y + z)", expression.build())
        }

        @Test
        fun `precedence left to right`() {
            val expression = SvFunctionExpression(LinePos.ZERO, "add", SvFunctionType.OPERATOR, listOf(
                    SvFunctionExpression(LinePos.ZERO, "sub", SvFunctionType.OPERATOR, listOf(
                            SvIdentifierExpression(LinePos.ZERO, "x"),
                            SvIdentifierExpression(LinePos.ZERO, "y")
                    )),
                    SvIdentifierExpression(LinePos.ZERO, "z")
            ))
            assertStringEquals("x - y + z", expression.build())
        }

        @Test
        fun `precedence right to left`() {
            val expression = SvFunctionExpression(LinePos.ZERO, "sub", SvFunctionType.OPERATOR, listOf(
                    SvIdentifierExpression(LinePos.ZERO, "x"),
                    SvFunctionExpression(LinePos.ZERO, "add", SvFunctionType.OPERATOR, listOf(
                            SvIdentifierExpression(LinePos.ZERO, "y"),
                            SvIdentifierExpression(LinePos.ZERO, "z")
                    ))
            ))
            assertStringEquals("x - (y + z)", expression.build())
        }
    }

    @Nested
    inner class Identifier {

        @Test
        fun `simple identifier`() {
            val expression = SvIdentifierExpression(LinePos.ZERO, "x")
            assertStringEquals("x", expression.build())
        }
    }

    @Nested
    inner class Literal {

        @Test
        fun `simple literal`() {
            val expression = SvLiteralExpression(LinePos.ZERO, "0")
            assertStringEquals("0", expression.build())
        }
    }
}