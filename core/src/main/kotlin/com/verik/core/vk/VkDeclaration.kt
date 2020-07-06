package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.KtGrammarException
import com.verik.core.kt.KtRule
import com.verik.core.kt.KtRuleType
import com.verik.core.kt.KtTokenType

// Copyright (c) 2020 Francis Wang

sealed class VkDeclaration(open val name: String, open val linePos: LinePos) {

    companion object {

        operator fun invoke(declaration: KtRule): VkDeclaration {
            val child = declaration.getFirstAsRule()
            return when (child.type) {
                KtRuleType.CLASS_DECLARATION -> VkClassDeclaration(child)
                KtRuleType.FUNCTION_DECLARATION -> VkFunctionDeclaration(child)
                KtRuleType.PROPERTY_DECLARATION -> VkPropertyDeclaration(child)
                else -> throw KtGrammarException("class funtion or property expected", declaration.linePos)
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

            val classOrInterface = when {
                classDeclaration.containsType(KtTokenType.CLASS) -> classDeclaration.getChildAs(KtTokenType.CLASS)
                classDeclaration.containsType(KtTokenType.INTERFACE) -> classDeclaration.getChildAs(KtTokenType.INTERFACE)
                else -> throw KtGrammarException("class or interface expected", classDeclaration.linePos)
            }
            if (classOrInterface.type == KtTokenType.INTERFACE) {
                throw VkParseException("class interfaces are not supported", classDeclaration.linePos)
            }

            val simpleIdentifier = classDeclaration.getChildAs(KtRuleType.SIMPLE_IDENTIFIER)
            val name = simpleIdentifier.getFirstAsTokenText()
            if (name.length <= 1) {
                throw VkParseException("illegal name", simpleIdentifier.linePos)
            }
            if (name[0] != '_') {
                throw VkParseException("name must begin with an underscore", simpleIdentifier.linePos)
            }

            if (classDeclaration.containsType(KtRuleType.TYPE_PARAMETERS)) {
                throw VkParseException("type parameters are not supported", classDeclaration.linePos)
            }
            if (classDeclaration.containsType(KtRuleType.PRIMARY_CONSTRUCTOR)) {
                throw VkParseException("constructor is not supported", classDeclaration.linePos)
            }

            if (!classDeclaration.containsType(KtRuleType.DELEGATION_SPECIFIERS)) {
                throw VkParseException("delegation specifier required", classDeclaration.linePos)
            }
            val delegationSpecifiers = classDeclaration.getChildAs(KtRuleType.DELEGATION_SPECIFIERS)
            val delegationSpecifierSimpleIdentifier = delegationSpecifiers.getDirectDescendantAs(KtRuleType.SIMPLE_IDENTIFIER,
                    VkParseException("delegation specifier not supported", classDeclaration.linePos))
            val delegationSpecifierName = delegationSpecifierSimpleIdentifier.getFirstAsTokenText()

            val body = when {
                classDeclaration.containsType(KtRuleType.CLASS_BODY) -> {
                    classDeclaration.getChildAs(KtRuleType.CLASS_BODY)
                }
                classDeclaration.containsType(KtRuleType.ENUM_CLASS_BODY) -> {
                    classDeclaration.getChildAs(KtRuleType.ENUM_CLASS_BODY)
                }
                else -> null
            }

            return VkClassDeclaration(name, classOrInterface.linePos, annotations, modifiers, delegationSpecifierName, body)
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

            val function = functionDeclaration.getChildAs(KtTokenType.FUN)

            val simpleIdentifier = functionDeclaration.getChildAs(KtRuleType.SIMPLE_IDENTIFIER)
            val name = simpleIdentifier.getFirstAsTokenText()

            val functionValueParameters = functionDeclaration.getChildAs(KtRuleType.FUNCTION_VALUE_PARAMETERS)
            if (functionValueParameters.children.isNotEmpty()) {
                throw VkParseException("function value parameters not supported", functionValueParameters.linePos)
            }

            if (functionDeclaration.containsType(KtRuleType.TYPE)) {
                throw VkParseException("function return type not supported", functionValueParameters.linePos)
            }

            val body = if (functionDeclaration.containsType(KtRuleType.FUNCTION_BODY)) {
                functionDeclaration.getChildAs(KtRuleType.FUNCTION_BODY)
            } else null

            return VkFunctionDeclaration(name, function.linePos, annotations, modifiers, body)
        }
    }
}

data class VkPropertyDeclaration(
        override val name: String,
        override val linePos: LinePos,
        val annotations: List<VkPropertyAnnotation>,
        val dataType: VkDataType
): VkDeclaration(name, linePos) {

    companion object {

        operator fun invoke(propertyDeclaration: KtRule): VkPropertyDeclaration {
            val annotations = getAnnotations(propertyDeclaration) { VkPropertyAnnotation(it) }
            getModifiers(propertyDeclaration) {
                throw VkParseException("illegal property modifier", it.linePos)
            }

            val value = propertyDeclaration.getChildAs(KtTokenType.VAL)

            val variableDeclaration = propertyDeclaration.getChildAs(KtRuleType.VARIABLE_DECLARATION)
            if (variableDeclaration.containsType(KtRuleType.TYPE)) {
                throw VkParseException("type declaration not permitted here", propertyDeclaration.linePos)
            }
            val name = variableDeclaration.getFirstAsRule().getFirstAsTokenText()
            if (name.isEmpty()) {
                throw VkParseException("illegal name", variableDeclaration.linePos)
            }
            if (name[0] == '_') {
                throw VkParseException("name must not begin with an underscore", variableDeclaration.linePos)
            }

            if (!propertyDeclaration.containsType(KtRuleType.EXPRESSION)) {
                throw VkParseException("type declaration expected", propertyDeclaration.linePos)
            }
            val expression = propertyDeclaration.getChildAs(KtRuleType.EXPRESSION)
            val dataType = VkDataType.invoke(expression)

            return VkPropertyDeclaration(name, value.linePos, annotations, dataType)
        }
    }
}
