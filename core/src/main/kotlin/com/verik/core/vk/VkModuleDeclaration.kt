package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.sv.SvConnection
import com.verik.core.sv.SvModuleDeclaration

// Copyright (c) 2020 Francis Wang

data class VkConnection(
        val identifier: String,
        val expression: VkExpression,
        val linePos: LinePos
) {

    fun extract(): SvConnection {
        return SvConnection(identifier, expression.extract(), linePos)
    }
}

data class VkModuleDeclaration(
        val moduleType: VkNamedType,
        val identifier: String,
        val connections: List<VkConnection>,
        val linePos: LinePos
) {

    fun extract(): SvModuleDeclaration {
        return SvModuleDeclaration(moduleType.extract(), identifier, connections.map { it.extract() }, linePos)
    }

    companion object {

        fun isModuleDeclaration(propertyDeclaration: VkPropertyDeclaration) =
                propertyDeclaration.annotations.any { it == VkPropertyAnnotation.COMP }

        operator fun invoke(propertyDeclaration: VkPropertyDeclaration): VkModuleDeclaration {
            if (propertyDeclaration.annotations.size != 1
                    || propertyDeclaration.annotations[0] != VkPropertyAnnotation.COMP) {
                throw VkParseException("illegal declaration annotations", propertyDeclaration.linePos)
            }

            val functionExpression = if (propertyDeclaration.expression is VkFunctionExpression) propertyDeclaration.expression
            else throw VkParseException("module declaration expected", propertyDeclaration.expression.linePos)

            return when {
                functionExpression.isOperator("invoke") -> {
                    val dataType = VkDataType(propertyDeclaration.expression)
                    val moduleType = if (dataType is VkNamedType) dataType
                    else throw VkParseException("module type expected", functionExpression.linePos)

                    VkModuleDeclaration(moduleType, propertyDeclaration.identifier, listOf(), propertyDeclaration.linePos)
                }
                functionExpression.isOperator("with") -> {
                    val dataType = VkDataType(functionExpression.args[0])
                    val moduleType = if (dataType is VkNamedType) dataType
                    else throw VkParseException("module type expected", functionExpression.linePos)

                    val expression = functionExpression.args[1]
                    val lambdaExpression = if (expression is VkLambdaExpression) expression
                    else throw VkParseException("module connections expected", expression.linePos)
                    val connections = lambdaExpression.statements.map { getConnection(it) }

                    VkModuleDeclaration(moduleType, propertyDeclaration.identifier, connections, propertyDeclaration.linePos)
                }
                else -> throw VkParseException("module declaration expected", functionExpression.linePos)
            }
        }

        private fun getConnection(statement: VkStatement): VkConnection {
            return when (val expression = statement.expression) {
                is VkFunctionExpression -> {
                    if (expression.isOperator("con")) {
                        val target = expression.args[0]
                        val identifier = if (target is VkNavigationExpression
                                && target.target is VkIdentifierExpression
                                && target.target.identifier == "it") {
                                target.identifier
                        } else throw VkParseException("module connection target expected", target.linePos)
                        VkConnection(identifier, expression.args[1], expression.linePos)
                    }
                    else throw VkParseException("module connection expected", statement.linePos)
                }
                is VkIdentifierExpression -> VkConnection(expression.identifier, expression, expression.linePos)
                else -> throw VkParseException("module connection expected", statement.linePos)
            }
        }
    }
}