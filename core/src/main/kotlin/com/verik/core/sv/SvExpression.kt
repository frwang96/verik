package com.verik.core.sv

import com.verik.core.LinePos

// Copyright (c) 2020 Francis Wang

sealed class SvExpression(open val linePos: LinePos) {

    fun build(): String {
        return SvExpressionBuilder.build(this)
    }
}

data class SvCallableExpression(
        override val linePos: LinePos,
        val target: SvExpression,
        val args: List<SvExpression>
): SvExpression(linePos)

data class SvOperatorExpression(
        override val linePos: LinePos,
        val type: SvOperatorType,
        val args: List<SvExpression>
): SvExpression(linePos)

data class SvIdentifierExpression(
        override val linePos: LinePos,
        val identifier: String
): SvExpression(linePos)

data class SvLiteralExpression(
        override val linePos: LinePos,
        val value: String
): SvExpression(linePos)
