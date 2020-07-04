package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.KtRule
import com.verik.core.kt.KtRuleType
import com.verik.core.kt.KtTokenType

// Copyright (c) 2020 Francis Wang

sealed class VkDeclaration(open val name: String, open val linePos: LinePos) {

    companion object {

        operator fun invoke(declaration: KtRule): VkDeclaration {
            val child = declaration.getFirstAsRule(VkGrammarException())
            return when (child.type) {
                KtRuleType.CLASS_DECLARATION -> VkClassDeclaration(child)
                KtRuleType.FUNCTION_DECLARATION -> VkFunctionDeclaration(child)
                KtRuleType.PROPERTY_DECLARATION -> VkPropertyDeclaration(child)
                else -> throw VkGrammarException()
            }
        }

        fun <T> getAnnotations(declaration: KtRule, map: (KtRule) -> T): List<T> {
            return declaration.getChildrenAs(KtRuleType.MODIFIERS).flatMap { it.getChildrenAs(KtRuleType.ANNOTATION) }.map(map)
        }

        fun <T> getModifiers(declaration: KtRule, map: (KtRule) -> T): List<T> {
            return declaration.getChildrenAs(KtRuleType.MODIFIERS).flatMap { it.getChildrenAs(KtRuleType.MODIFIER) }.map(map)
        }
    }
}


data class VkClassDeclaration(
        override val name: String,
        override val linePos: LinePos,
        val annotations: List<VkClassAnnotation>,
        val modifiers: List<VkClassModifier>,
        val delegationSpecifierName: String,
        val body: KtRule?
): VkDeclaration(name, linePos) {

    companion object {

        operator fun invoke(classDeclaration: KtRule): VkClassDeclaration {
            val annotations = getAnnotations(classDeclaration) { VkClassAnnotation(it) }
            val modifiers = getModifiers(classDeclaration) { VkClassModifier(it) }

            if (classDeclaration.containsType(KtTokenType.INTERFACE)) {
                throw VkParseException(classDeclaration.linePos, "class interfaces are not supported")
            }

            val simpleIdentifier = classDeclaration.getChildAs(KtRuleType.SIMPLE_IDENTIFIER, VkGrammarException())
            val name = simpleIdentifier.getFirstAsTokenText(VkGrammarException())
            if (name.length <= 1) {
                throw VkParseException(simpleIdentifier.linePos, "illegal name")
            }
            if (name[0] != '_') {
                throw VkParseException(simpleIdentifier.linePos, "name must begin with an underscore")
            }

            if (classDeclaration.containsType(KtRuleType.TYPE_PARAMETERS)) {
                throw VkParseException(classDeclaration.linePos, "type parameters are not supported")
            }
            if (classDeclaration.containsType(KtRuleType.PRIMARY_CONSTRUCTOR)) {
                throw VkParseException(classDeclaration.linePos, "constructor is not supported")
            }

            val delegationSpecifiers = classDeclaration.getChildAs(KtRuleType.DELEGATION_SPECIFIERS,
                    VkParseException(classDeclaration.linePos, "delegation specifier required"))
            val delegationSpecifierSimpleIdentifier = delegationSpecifiers.getDirectDescendantAs(KtRuleType.SIMPLE_IDENTIFIER,
                    VkParseException(classDeclaration.linePos, "delegation specifier not supported"))
            val delegationSpecifierName = delegationSpecifierSimpleIdentifier.getFirstAsTokenText(VkGrammarException())

            val body = when {
                classDeclaration.containsType(KtRuleType.CLASS_BODY) -> {
                    classDeclaration.getChildAs(KtRuleType.CLASS_BODY, VkGrammarException())
                }
                classDeclaration.containsType(KtRuleType.ENUM_CLASS_BODY) -> {
                    classDeclaration.getChildAs(KtRuleType.ENUM_CLASS_BODY, VkGrammarException())
                }
                else -> null
            }

            return VkClassDeclaration(name, classDeclaration.linePos, annotations, modifiers, delegationSpecifierName, body)
        }
    }
}

data class VkFunctionDeclaration (
        override val name: String,
        override val linePos: LinePos,
        val annotations: List<VkFunctionAnnotation>,
        val modifiers: List<VkFunctionModifier>,
        val body: KtRule?
): VkDeclaration(name, linePos) {

    companion object {

        operator fun invoke(functionDeclaration: KtRule): VkFunctionDeclaration {
            val annotations = getAnnotations(functionDeclaration) { VkFunctionAnnotation(it) }
            val modifiers = getModifiers(functionDeclaration) { VkFunctionModifier(it) }

            val simpleIdentifier = functionDeclaration.getChildAs(KtRuleType.SIMPLE_IDENTIFIER, VkGrammarException())
            val name = simpleIdentifier.getFirstAsTokenText(VkGrammarException())

            val functionValueParameters = functionDeclaration.getChildAs(KtRuleType.FUNCTION_VALUE_PARAMETERS, VkGrammarException())
            if (functionValueParameters.children.isNotEmpty()) {
                throw VkParseException(functionValueParameters.linePos, "function value parameters not supported")
            }

            if (functionDeclaration.containsType(KtRuleType.TYPE)) {
                throw VkParseException(functionValueParameters.linePos, "function return type not supported")
            }

            val body = if (functionDeclaration.containsType(KtRuleType.FUNCTION_BODY)) {
                functionDeclaration.getChildAs(KtRuleType.FUNCTION_BODY, VkGrammarException())
            } else null

            return VkFunctionDeclaration(name, functionDeclaration.linePos, annotations, modifiers, body)
        }
    }
}

data class VkPropertyDeclaration(
        override val name: String,
        override val linePos: LinePos,
        val annotations: List<VkPropertyAnnotation>,
        val modifiers: List<VkPropertyModifier>,
        val dataType: VkDataType
): VkDeclaration(name, linePos) {

    companion object {

        operator fun invoke(propertyDeclaration: KtRule): VkPropertyDeclaration {
            val annotations = getAnnotations(propertyDeclaration) { VkPropertyAnnotation(it) }
            val modifiers = getModifiers(propertyDeclaration) { VkPropertyModifier(it) }

            val variableDeclaration = propertyDeclaration.getChildAs(KtRuleType.VARIABLE_DECLARATION, VkGrammarException())
            if (variableDeclaration.containsType(KtRuleType.TYPE)) {
                throw VkParseException(propertyDeclaration.linePos, "type declaration not permitted here")
            }
            val name = variableDeclaration.getFirstAsRule(VkGrammarException()).getFirstAsTokenText(VkGrammarException())
            if (name.isEmpty()) {
                throw VkParseException(variableDeclaration.linePos, "illegal name")
            }
            if (name[0] == '_') {
                throw VkParseException(variableDeclaration.linePos, "name must not begin with an underscore")
            }

            val expression = propertyDeclaration.getChildAs(KtRuleType.EXPRESSION,
                    VkParseException(propertyDeclaration.linePos, "type declaration expected"))
            val dataType = VkDataType.invoke(expression)

            return VkPropertyDeclaration(name, propertyDeclaration.linePos, annotations, modifiers, dataType)
        }
    }
}
