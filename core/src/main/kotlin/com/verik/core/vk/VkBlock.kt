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
            PUT -> SvBlockType.ALWAYS_COMB
            REG -> SvBlockType.ALWAYS_FF
            DRIVE -> throw VkParseException("drive block not supported", linePos)
            INITIAL -> SvBlockType.INITIAL
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
        val sensitivityEntries: List<VkSensitivityEntry>,
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
        val svSensitivityEntries = sensitivityEntries.map { it.extract() }
        val svStatements = statements.map { it.extract() }
        return SvBlock(svType, svSensitivityEntries, svStatements, linePos)
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
                    else -> throw KtGrammarException("block expected", blockOrExpression.linePos)
                }
            } else listOf()

            return if (blockType == VkBlockType.REG) {
                parseRegBlock(statements, functionDeclaration.linePos)
            } else {
                VkBlock(blockType, listOf(), statements, functionDeclaration.linePos)
            }
        }

        private fun parseRegBlock(statements: List<VkStatement>, linePos: LinePos): VkBlock {
            if (statements.size != 1) {
                throw VkParseException("on expression expected", linePos)
            }
            val expression = statements[0].expression
            return if (expression is VkCallableExpression
                    && expression.target is VkIdentifierExpression
                    && expression.target.identifier == "on") {
                if (expression.args.size < 2) {
                    throw VkParseException("sensitivity entries expected", linePos)
                }
                val sensitivityEntries = expression.args.dropLast(1).map { VkSensitivityEntry(it) }
                val lambdaStatements = expression.args.last().let {
                    if (it is VkLambdaExpression) {
                        it.statements
                    } else throw VkParseException("lambda expression expected", linePos)
                }
                VkBlock(VkBlockType.REG, sensitivityEntries, lambdaStatements, linePos)
            } else throw VkParseException("on expression expected", linePos)
        }
    }
}