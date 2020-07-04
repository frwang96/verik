package com.verik.core.sv

// Copyright (c) 2020 Francis Wang

sealed class SvDeclaration {

    abstract fun build(): String
}

data class SvContinuousAssignment(val expression: SvExpression): SvDeclaration() {

    override fun build(): String {
        return "assign ${expression.build()};"
    }
}

