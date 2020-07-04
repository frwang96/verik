package com.verik.core.sv

import com.verik.core.assertStringEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class SvExpressionBuilderTest {

    @Nested
    inner class Assignment() {

        @Test
        fun `blocking assignment`() {
            val expression = SvFunctionExpression("bassign", SvFunctionType.OPERATOR, listOf(
                    SvLiteralExpression("x"),
                    SvLiteralExpression("y")
            ))
            assertStringEquals("x = y", expression.build())
        }
    }

    @Nested
    inner class Arithmetic() {

        @Test
        fun `add expression`() {
            val expression = SvFunctionExpression("add", SvFunctionType.OPERATOR, listOf(
                    SvIdentifierExpression("x"),
                    SvIdentifierExpression("y")
            ))
            assertStringEquals("x + y", expression.build())
        }

        @Test
        fun `precedence ordered`() {
            val expression = SvFunctionExpression("add", SvFunctionType.OPERATOR, listOf(
                    SvIdentifierExpression("x"),
                    SvFunctionExpression("mul", SvFunctionType.OPERATOR, listOf(
                            SvIdentifierExpression("y"),
                            SvIdentifierExpression("z")
                    ))
            ))
            assertStringEquals("x + y * z", expression.build())
        }

        @Test
        fun `precedence not ordered`() {
            val expression = SvFunctionExpression("mul", SvFunctionType.OPERATOR, listOf(
                    SvIdentifierExpression("x"),
                    SvFunctionExpression("add", SvFunctionType.OPERATOR, listOf(
                            SvIdentifierExpression("y"),
                            SvIdentifierExpression("z")
                    ))
            ))
            assertStringEquals("x * (y + z)", expression.build())
        }

        @Test
        fun `precedence left to right`() {
            val expression = SvFunctionExpression("add", SvFunctionType.OPERATOR, listOf(
                    SvFunctionExpression("sub", SvFunctionType.OPERATOR, listOf(
                            SvIdentifierExpression("x"),
                            SvIdentifierExpression("y")
                    )),
                    SvIdentifierExpression("z")
            ))
            assertStringEquals("x - y + z", expression.build())
        }

        @Test
        fun `precedence right to left`() {
            val expression = SvFunctionExpression("sub", SvFunctionType.OPERATOR, listOf(
                    SvIdentifierExpression("x"),
                    SvFunctionExpression("add", SvFunctionType.OPERATOR, listOf(
                            SvIdentifierExpression("y"),
                            SvIdentifierExpression("z")
                    ))
            ))
            assertStringEquals("x - (y + z)", expression.build())
        }
    }

    @Nested
    inner class Identifier() {

        @Test
        fun `simple identifier`() {
            val expression = SvIdentifierExpression("x")
            assertStringEquals("x", expression.build())
        }
    }

    @Nested
    inner class Literal() {

        @Test
        fun `simple literal`() {
            val expression = SvLiteralExpression("0")
            assertStringEquals("0", expression.build())
        }
    }
}