package com.verik.core.vk

sealed class VkDataType {

    companion object {

        operator fun invoke(expression: VkCallableExpression): VkDataType {
            val identifier = if (expression.target is VkIdentifierExpression) expression.target.identifier
            else throw VkParseException("type declaration expected", expression.linePos)

            val expressions = expression.args
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
                    if (parameters.isEmpty()) {
                        if (identifier.length <= 1) {
                            throw VkParseException("illegal identifier", expression.target.linePos)
                        }
                        if (identifier[0] != '_') {
                            throw VkParseException("identifier must begin with an underscore", expression.target.linePos)
                        }
                        VkNamedType(identifier)
                    }
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

data class VkNamedType(val identifier: String): VkDataType() {

    fun extract() = identifier.drop(1)
}
