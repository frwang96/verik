package com.verik.core.sv

import com.verik.core.LinePos

// Copyright (c) 2020 Francis Wang

enum class SvFunctionType {
    REGULAR,
    OPERATOR
}

sealed class SvExpression(open val linePos: LinePos) {

    fun build(): String {
        return SvExpressionBuilder.build(this)
    }
}

data class SvFunctionExpression(
        override val linePos: LinePos,
        val identifier: String,
        val functionType: SvFunctionType,
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
