package com.verik.core.vk

import com.verik.core.kt.KtRule
import com.verik.core.kt.KtRuleType
import com.verik.core.kt.KtTokenType

sealed class VkDataType {

    companion object {

        operator fun invoke(expression: KtRule): VkDataType {
            val postfixUnaryExpression = expression.getDirectDescendantAs(KtRuleType.POSTFIX_UNARY_EXPRESSION,
                    VkParseException(expression.linePos, "only type declarations are permitted"))
            val simpleIdentifier = postfixUnaryExpression.getFirstAsRule(VkGrammarException())
                    .getDirectDescendantAs(KtRuleType.SIMPLE_IDENTIFIER, VkParseException(expression.linePos, "invalid type declaration"))
            val name = simpleIdentifier.getFirstAsTokenText(VkGrammarException())

            val postfixUnarySuffix = postfixUnaryExpression.getChildAs(KtRuleType.POSTFIX_UNARY_SUFFIX,
                    VkParseException(expression.linePos, "invocation expected"))
            val valueArguments = postfixUnarySuffix.getDirectDescendantAs(KtRuleType.VALUE_ARGUMENTS,
                    VkParseException(expression.linePos, "value arguments expected"))
            val parameters = valueArguments.getChildrenAs(KtRuleType.VALUE_ARGUMENT).map {
                it.getDirectDescendantAs(KtTokenType.INTEGER_LITERAL, VkParseException(it.linePos, "integer literal expected")).text.toInt()
            }

            return when (name) {
                "_bool" -> {
                    if (parameters.isEmpty()) VkBoolType
                    else throw VkParseException(expression.linePos, "invalid parameters to type _bool")
                }
                "_sint" -> {
                    if (parameters.size == 1 && parameters[0] > 0) VkSintType(parameters[0])
                    else throw VkParseException(expression.linePos, "invalid parameters to type _sint")
                }
                "_uint" -> {
                    if (parameters.size == 1 && parameters[0] > 0) VkUintType(parameters[0])
                    else throw VkParseException(expression.linePos, "invalid parameters to type _uint")
                }
                else -> throw VkParseException(expression.linePos, "type identifier not recognized")
            }
        }
    }
}

object VkUnitType: VkDataType()
object VkBoolType: VkDataType()
data class VkSintType(val len: Int): VkDataType()
data class VkUintType(val len: Int): VkDataType()
