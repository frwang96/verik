package com.verik.core.vk

import com.verik.core.kt.KtRule
import com.verik.core.kt.KtRuleType
import com.verik.core.kt.KtTokenType

sealed class VkDataType {

    companion object {

        operator fun invoke(expression: KtRule): VkDataType {
            val postfixUnaryExpression = expression.getDirectDescendantAs(KtRuleType.POSTFIX_UNARY_EXPRESSION,
                    VkParseException("only type declarations are permitted", expression.linePos))
            val simpleIdentifier = postfixUnaryExpression.getFirstAsRule(VkGrammarException())
                    .getDirectDescendantAs(KtRuleType.SIMPLE_IDENTIFIER, VkParseException("invalid type declaration", expression.linePos))
            val name = simpleIdentifier.getFirstAsTokenText(VkGrammarException())

            val postfixUnarySuffix = postfixUnaryExpression.getChildAs(KtRuleType.POSTFIX_UNARY_SUFFIX,
                    VkParseException("invocation expected", expression.linePos))
            val valueArguments = postfixUnarySuffix.getDirectDescendantAs(KtRuleType.VALUE_ARGUMENTS,
                    VkParseException("value arguments expected", expression.linePos))
            val parameters = valueArguments.getChildrenAs(KtRuleType.VALUE_ARGUMENT).map {
                it.getDirectDescendantAs(KtTokenType.INTEGER_LITERAL, VkParseException("integer literal expected", it.linePos)).text.toInt()
            }

            return when (name) {
                "_bool" -> {
                    if (parameters.isEmpty()) VkBoolType
                    else throw VkParseException("invalid parameters to type _bool", expression.linePos)
                }
                "_sint" -> {
                    if (parameters.size == 1 && parameters[0] > 0) VkSintType(parameters[0])
                    else throw VkParseException("invalid parameters to type _sint", expression.linePos)
                }
                "_uint" -> {
                    if (parameters.size == 1 && parameters[0] > 0) VkUintType(parameters[0])
                    else throw VkParseException("invalid parameters to type _uint", expression.linePos)
                }
                else -> throw VkParseException("type identifier not recognized", expression.linePos)
            }
        }
    }
}

object VkUnitType: VkDataType()
object VkBoolType: VkDataType()
data class VkSintType(val len: Int): VkDataType()
data class VkUintType(val len: Int): VkDataType()
