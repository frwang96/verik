package com.verik.core.sv

// Copyright (c) 2020 Francis Wang

enum class SvFunctionType {
    REGULAR,
    OPERATOR
}

sealed class SvExpression {

    fun build(): String {
        return SvExpressionBuilder.build(this)
    }
}

data class SvFunctionExpression(val name: String, val functionType: SvFunctionType, val args: List<SvExpression>): SvExpression()
data class SvIdentifierExpression(val identifier: String): SvExpression()
data class SvLiteralExpression(val value: String): SvExpression()
