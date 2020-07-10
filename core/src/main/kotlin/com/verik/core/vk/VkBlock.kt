package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.KtGrammarException
import com.verik.core.kt.KtRuleType
import com.verik.core.sv.SvBlock
import com.verik.core.sv.SvBlockType
import com.verik.core.sv.SvContinuousAssignment

// Copyright (c) 2020 Francis Wang


enum class VkBlockType {
    PUT,
    REG,
    DRIVE,
    INITIAL;

    fun extract(linePos: LinePos): SvBlockType {
        return when (this) {
            PUT -> throw VkParseException("put block not supported", linePos)
            REG -> throw VkParseException("reg block not supported", linePos)
            DRIVE -> throw VkParseException("drive block not supported", linePos)
            INITIAL ->SvBlockType.INITIAL
        }
    }

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
        val type: VkBlockType,
        val identifier: String,
        val statements: List<VkStatement>,
        val linePos: LinePos
) {

    fun extractContinuousAssignment(): SvContinuousAssignment? {
        return if (type == VkBlockType.PUT && statements.size == 1) {
            val statement = statements[0]
            if (statement.expression is VkOperatorExpression && statement.expression.type == VkOperatorType.PUT) {
                SvContinuousAssignment(statement.expression.extractExpression(), linePos)
            } else null
        } else null
    }

    fun extractBlock(): SvBlock {
        val svType = type.extract(linePos)
        val svStatements = statements.map { it.extract() }
        return SvBlock(svType, svStatements, linePos)
    }

    companion object {

        fun isBlock(functionDeclaration: VkFunctionDeclaration) = functionDeclaration.annotations.any {
            it in listOf(
                    VkFunctionAnnotation.PUT,
                    VkFunctionAnnotation.REG,
                    VkFunctionAnnotation.DRIVE,
                    VkFunctionAnnotation.INITIAL
            )
        }

        operator fun invoke(functionDeclaration: VkFunctionDeclaration): VkBlock {
            val blockType = VkBlockType(functionDeclaration.annotations, functionDeclaration.linePos)
            if (functionDeclaration.modifiers.isNotEmpty()) {
                throw VkParseException("function modifiers are not permitted here", functionDeclaration.linePos)
            }

            val statements = if (functionDeclaration.body != null) {
                val blockOrExpression = functionDeclaration.body.firstAsRule()
                when (blockOrExpression.type) {
                    KtRuleType.BLOCK -> {
                        blockOrExpression.firstAsRule().childrenAs(KtRuleType.STATEMENT).map { VkStatement(it) }
                    }
                    KtRuleType.EXPRESSION -> {
                        listOf(VkStatement(VkExpression(blockOrExpression), blockOrExpression.linePos))
                    }
                    else -> throw KtGrammarException("block or expression expected", blockOrExpression.linePos)
                }
            } else listOf()

            return VkBlock(blockType, functionDeclaration.identifier, statements, functionDeclaration.linePos)
        }
    }
}