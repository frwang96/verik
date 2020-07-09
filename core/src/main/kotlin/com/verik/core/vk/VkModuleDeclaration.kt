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
        val svModuleType = moduleType.extract()
        val svConnections = connections.map { it.extract() }
        return SvModuleDeclaration(svModuleType, identifier, svConnections, linePos)
    }

    companion object {

        fun isModuleDeclaration(propertyDeclaration: VkPropertyDeclaration) =
                propertyDeclaration.annotations.any { it == VkPropertyAnnotation.COMP }

        operator fun invoke(propertyDeclaration: VkPropertyDeclaration): VkModuleDeclaration {
            if (propertyDeclaration.annotations.size != 1
                    || propertyDeclaration.annotations[0] != VkPropertyAnnotation.COMP) {
                throw VkParseException("illegal declaration annotations", propertyDeclaration.linePos)
            }

            return when(val expression = propertyDeclaration.expression) {
                is VkCallableExpression -> {
                    val dataType = VkDataType(expression)
                    val moduleType = if (dataType is VkNamedType) dataType
                    else throw VkParseException("module type expected", propertyDeclaration.linePos)

                    VkModuleDeclaration(moduleType, propertyDeclaration.identifier, listOf(), propertyDeclaration.linePos)
                }
                is VkOperatorExpression -> {
                    if (expression.type != VkOperatorType.WITH) {
                        throw VkParseException("module connection list expected", propertyDeclaration.linePos)
                    }
                    val dataType = expression.args[0].let {
                        if (it is VkCallableExpression) VkDataType(it)
                        else throw VkParseException("module type expected", propertyDeclaration.linePos)
                    }
                    val moduleType = if (dataType is VkNamedType) dataType
                    else throw VkParseException("module type expected", propertyDeclaration.linePos)

                    val lambdaExpression = expression.args[1].let {
                        if (it is VkLambdaExpression) it
                        else throw VkParseException("module connections expected", expression.linePos)
                    }
                    val connections = lambdaExpression.statements.map { getConnection(it) }

                    VkModuleDeclaration(moduleType, propertyDeclaration.identifier, connections, propertyDeclaration.linePos)
                }
                else -> throw VkParseException("module declaration expected", propertyDeclaration.linePos)
            }
        }

        private fun getConnection(statement: VkStatement): VkConnection {
            return when (val expression = statement.expression) {
                is VkOperatorExpression -> {
                    if (expression.type == VkOperatorType.CON) {
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