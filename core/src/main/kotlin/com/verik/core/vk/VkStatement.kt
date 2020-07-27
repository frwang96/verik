package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.LinePosException
import com.verik.core.kt.KtRule
import com.verik.core.kt.KtRuleType
import com.verik.core.sv.SvStatement

// Copyright (c) 2020 Francis Wang

data class VkStatement(val expression: VkExpression, val linePos: LinePos) {

    fun extract(): SvStatement {
        return expression.extractStatement()
    }

    companion object {

        operator fun invoke(statement: KtRule): VkStatement {
            val child = statement.firstAsRule()
            return when (child.type) {
                 KtRuleType.DECLARATION -> {
                     throw LinePosException("declaration statements not supported", statement.linePos)
                 }
                 KtRuleType.LOOP_STATEMENT -> {
                     throw LinePosException("loop statements not supported", statement.linePos)
                 }
                 KtRuleType.EXPRESSION -> {
                     VkStatement(VkExpression(child), statement.linePos)
                 }
                 else -> throw LinePosException("declaration loop or expression expected", statement.linePos)
             }
        }
    }
}