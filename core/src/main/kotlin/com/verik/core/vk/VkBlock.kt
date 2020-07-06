package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.KtGrammarException
import com.verik.core.kt.KtRuleType
import com.verik.core.sv.SvContinuousAssignment

// Copyright (c) 2020 Francis Wang


enum class VkBlockType {
    PUT,
    REG,
    DRIVE,
    INITIAL;

    companion object {

        operator fun invoke(annotations: List<VkFunctionAnnotation>, linePos: LinePos): VkBlockType {
            return if (annotations.size == 1) {
                when (annotations[0]) {
                    VkFunctionAnnotation.PUT -> PUT
                    VkFunctionAnnotation.REG -> REG
                    VkFunctionAnnotation.DRIVE -> DRIVE
                    VkFunctionAnnotation.INITIAL -> INITIAL
                    else -> throw VkParseException("illegal block type", linePos)
                }
            } else throw VkParseException("illegal block type", linePos)
        }
    }
}

data class VkBlock(
        val blockType: VkBlockType,
        val name: String,
        val statements: List<VkStatement>,
        val linePos: LinePos
) {

    fun extractContinuousAssignment(): SvContinuousAssignment? {
        return if (blockType == VkBlockType.PUT && statements.size == 1) {
            SvContinuousAssignment(statements[0].expression.extract(), linePos)
        } else null
    }

    companion object {

        operator fun invoke(functionDeclaration: VkFunctionDeclaration): VkBlock {
            val blockType = VkBlockType(functionDeclaration.annotations, functionDeclaration.linePos)
            if (functionDeclaration.modifiers.isNotEmpty()) {
                throw VkParseException("function modifiers are not permitted here", functionDeclaration.linePos)
            }

            val statements = if (functionDeclaration.body != null) {
                val blockOrExpression = functionDeclaration.body.getFirstAsRule()
                when (blockOrExpression.type) {
                    KtRuleType.BLOCK -> {
                        blockOrExpression.getFirstAsRule().getChildrenAs(KtRuleType.STATEMENT).map { VkStatement(it) }
                    }
                    KtRuleType.EXPRESSION -> {
                        listOf(VkStatement(VkExpression(blockOrExpression)))
                    }
                    else -> throw KtGrammarException("block or expression expected", blockOrExpression.linePos)
                }
            } else listOf()

            return VkBlock(blockType, functionDeclaration.name, statements, functionDeclaration.linePos)
        }

        fun isBlock(functionDeclaration: VkFunctionDeclaration) = functionDeclaration.annotations.any {
            it in listOf(
                    VkFunctionAnnotation.PUT,
                    VkFunctionAnnotation.REG,
                    VkFunctionAnnotation.DRIVE,
                    VkFunctionAnnotation.INITIAL
            )
        }
    }
}