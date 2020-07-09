package com.verik.core.sv

import com.verik.core.LinePos
import com.verik.core.assertStringEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class SvExpressionBuilderTest {

    @Nested
    inner class Callable {

        @Test
        fun `simple callable`() {
            val expression = SvCallableExpression(LinePos.ZERO, SvIdentifierExpression(LinePos.ZERO, "\$finish"), listOf())
            assertStringEquals("\$finish", expression.build())
        }

        @Test
        fun `callable with arguments`() {
            val expression = SvCallableExpression(LinePos.ZERO, SvIdentifierExpression(LinePos.ZERO, "\$finish"),
                    listOf(SvLiteralExpression(LinePos.ZERO, "0")))
            assertStringEquals("\$finish(0)", expression.build())
        }
    }

    @Nested
    inner class Assignment {

        @Test
        fun `blocking assignment`() {
            val expression = SvOperatorExpression(LinePos.ZERO, SvOperatorType.BASSIGN, listOf(
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
            val expression = SvOperatorExpression(LinePos.ZERO, SvOperatorType.ADD, listOf(
                    SvIdentifierExpression(LinePos.ZERO, "x"),
                    SvIdentifierExpression(LinePos.ZERO, "y")
            ))
            assertStringEquals("x + y", expression.build())
        }

        @Test
        fun `precedence ordered`() {
            val expression = SvOperatorExpression(LinePos.ZERO, SvOperatorType.ADD, listOf(
                    SvIdentifierExpression(LinePos.ZERO, "x"),
                    SvOperatorExpression(LinePos.ZERO, SvOperatorType.MUL, listOf(
                            SvIdentifierExpression(LinePos.ZERO, "y"),
                            SvIdentifierExpression(LinePos.ZERO, "z")
                    ))
            ))
            assertStringEquals("x + y * z", expression.build())
        }

        @Test
        fun `precedence not ordered`() {
            val expression = SvOperatorExpression(LinePos.ZERO, SvOperatorType.MUL, listOf(
                    SvIdentifierExpression(LinePos.ZERO, "x"),
                    SvOperatorExpression(LinePos.ZERO, SvOperatorType.ADD, listOf(
                            SvIdentifierExpression(LinePos.ZERO, "y"),
                            SvIdentifierExpression(LinePos.ZERO, "z")
                    ))
            ))
            assertStringEquals("x * (y + z)", expression.build())
        }

        @Test
        fun `precedence left to right`() {
            val expression = SvOperatorExpression(LinePos.ZERO, SvOperatorType.ADD, listOf(
                    SvOperatorExpression(LinePos.ZERO, SvOperatorType.SUB, listOf(
                            SvIdentifierExpression(LinePos.ZERO, "x"),
                            SvIdentifierExpression(LinePos.ZERO, "y")
                    )),
                    SvIdentifierExpression(LinePos.ZERO, "z")
            ))
            assertStringEquals("x - y + z", expression.build())
        }

        @Test
        fun `precedence right to left`() {
            val expression = SvOperatorExpression(LinePos.ZERO, SvOperatorType.SUB, listOf(
                    SvIdentifierExpression(LinePos.ZERO, "x"),
                    SvOperatorExpression(LinePos.ZERO, SvOperatorType.ADD, listOf(
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