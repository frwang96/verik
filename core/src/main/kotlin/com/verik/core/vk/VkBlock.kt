package com.verik.core.vk

import com.verik.core.LinePos
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
                    else -> throw VkParseException(linePos, "illegal block type")
                }
            } else throw VkParseException(linePos, "illegal block type")
        }
    }
}

data class VkBlock(val blockType: VkBlockType, val name: String, val body: List<VkStatement>, val linePos: LinePos) {

    fun extractContinuousAssignment(): SvContinuousAssignment? {
        return if (blockType == VkBlockType.PUT && body.size == 1) {
            SvContinuousAssignment(body[0].expression.extract(), linePos.line)
        } else null
    }

    companion object {

        operator fun invoke(functionDeclaration: VkFunctionDeclaration): VkBlock {
            val blockType = VkBlockType(functionDeclaration.annotations, functionDeclaration.linePos)
            if (functionDeclaration.modifiers.isNotEmpty()) {
                throw VkParseException(functionDeclaration.linePos, "function modifiers are not permitted here")
            }

            val body = if (functionDeclaration.body != null) {
                val blockOrExpression = functionDeclaration.body.getFirstAsRule(VkGrammarException())
                when (blockOrExpression.type) {
                    KtRuleType.BLOCK -> {
                        blockOrExpression.getFirstAsRule(VkGrammarException()).getChildrenAs(KtRuleType.STATEMENT).map { VkStatement(it) }
                    }
                    KtRuleType.EXPRESSION -> {
                        listOf(VkStatement(VkExpression(blockOrExpression)))
                    }
                    else -> throw VkGrammarException()
                }
            } else listOf()

            return VkBlock(blockType, functionDeclaration.name, body, functionDeclaration.linePos)
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