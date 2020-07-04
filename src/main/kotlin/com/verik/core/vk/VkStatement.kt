package com.verik.core.vk

import com.verik.core.kt.KtRule
import com.verik.core.kt.KtRuleType

// Copyright (c) 2020 Francis Wang

data class VkStatement(val expression: VkExpression) {

    companion object {

        operator fun invoke(statement: KtRule): VkStatement {
            val child = statement.getFirstAsRule(VkGrammarException())
            return when (child.type) {
                 KtRuleType.DECLARATION -> {
                     throw VkParseException(statement.linePos, "declaration statements not supported")
                 }
                 KtRuleType.LOOP_STATEMENT -> {
                     throw VkParseException(statement.linePos, "loop statements not supported")
                 }
                 KtRuleType.EXPRESSION -> {
                     VkStatement(VkExpression(child))
                 }
                 else -> throw VkGrammarException()
             }
        }
    }
}