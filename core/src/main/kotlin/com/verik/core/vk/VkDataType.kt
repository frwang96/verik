package com.verik.core.vk

sealed class VkDataType {

    companion object {

        operator fun invoke(expression: VkExpression): VkDataType {
            val functionExpression = if (expression is VkFunctionExpression) expression
            else throw VkParseException("type declaration expected", expression.linePos)

            if (functionExpression.identifier != "invoke") {
                throw VkParseException("type declaration expected", expression.linePos)
            }

            val target = functionExpression.args[0]
            val identifier = if (target is VkIdentifierExpression) target.identifier
            else throw VkParseException("type declaration expected", expression.linePos)

            val expressions = functionExpression.args.drop(1)
            val parameters = expressions.map {
                if (it is VkLiteralExpression) it.value.toInt()
                else throw VkParseException("only integer literals are supported", it.linePos)
            }

            return when (identifier) {
                "_bool" -> {
                    if (parameters.isEmpty()) VkBoolType
                    else throw VkParseException("type _bool does not take parameters", expression.linePos)
                }
                "_sint" -> {
                    if (parameters.size == 1) VkSintType(parameters[0])
                    else throw VkParseException("type _sint takes one parameter", expression.linePos)
                }
                "_uint" -> {
                    if (parameters.size == 1) VkUintType(parameters[0])
                    else throw VkParseException("type _uint takes one parameter", expression.linePos)
                }
                else -> {
                    if (parameters.isEmpty()) VkNamedType(identifier)
                    else throw VkParseException("parameters to named types are not supported", expression.linePos)
                }
            }
        }
    }
}

object VkUnitType: VkDataType()
object VkBoolType: VkDataType()
data class VkSintType(val len: Int): VkDataType()
data class VkUintType(val len: Int): VkDataType()
data class VkNamedType(val identifier: String): VkDataType()
