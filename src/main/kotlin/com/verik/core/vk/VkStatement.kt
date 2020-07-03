package com.verik.core.vk

import com.verik.core.kt.KtRuleType
import com.verik.core.kt.KtTree

// Copyright (c) 2020 Francis Wang

data class VkStatement(val expression: VkExpression) {

    companion object {

        operator fun invoke(statement: KtTree) {
            when (statement.first().getTypeAsRule(VkGrammarException())) {
                KtRuleType.DECLARATION -> {
                    throw VkParseException(statement.linePos, "declaration statements not supported")
                }
                KtRuleType.LOOP_STATEMENT -> {
                    throw VkParseException(statement.linePos, "loop statements not supported")
                }
                KtRuleType.EXPRESSION -> {
                    VkExpression(statement.first())
                }
                else -> throw VkGrammarException()
            }
        }
    }
}