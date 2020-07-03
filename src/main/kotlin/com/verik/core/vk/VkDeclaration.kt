package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.KtRuleType
import com.verik.core.kt.KtToken
import com.verik.core.kt.KtTokenType
import com.verik.core.kt.KtTree

// Copyright (c) 2020 Francis Wang

sealed class VkDeclaration {

    companion object {

        operator fun invoke(declaration: KtTree): VkDeclaration {
            return when (declaration.first().getTypeAsRule(VkGrammarException())) {
                KtRuleType.CLASS_DECLARATION -> VkClassDeclaration(declaration.first())
                KtRuleType.FUNCTION_DECLARATION -> VkFunctionDeclaration(declaration.first())
                KtRuleType.PROPERTY_DECLARATION -> VkPropertyDeclaration(declaration.first())
                else -> throw VkGrammarException()
            }
        }

        fun <T> getAnnotations(declaration: KtTree, map: (KtTree) -> T): List<T> {
            val modifiersAndAnnotations = declaration.getChildrenAs(KtRuleType.MODIFIERS).flatMap { it.children }
            return modifiersAndAnnotations.filter { it.isType(KtRuleType.ANNOTATION) }.map(map)
        }

        fun <T> getModifiers(declaration: KtTree, map: (KtTree) -> T): List<T> {
            val modifiersAndAnnotations = declaration.getChildrenAs(KtRuleType.MODIFIERS).flatMap { it.children }
            return modifiersAndAnnotations.filter { it.isType(KtRuleType.MODIFIER) }.map(map)
        }
    }
}


data class VkClassDeclaration(val annotations: List<VkClassAnnotation>, val modifiers: List<VkClassModifier>,
                              val name: String, val delegationSpecifierName: String, val body: KtTree?,
                              val linePos: LinePos): VkDeclaration() {

    companion object {

        operator fun invoke(classDeclaration: KtTree): VkClassDeclaration {
            val annotations = getAnnotations(classDeclaration) { VkClassAnnotation(it) }
            val modifiers = getModifiers(classDeclaration) { VkClassModifier(it) }

            if (classDeclaration.containsType(KtTokenType.INTERFACE)) {
                throw VkParseException(classDeclaration.linePos, "class interfaces are not supported")
            }

            val simpleIdentifier = classDeclaration.getChildAs(KtRuleType.SIMPLE_IDENTIFIER, VkGrammarException())
            val name = (simpleIdentifier.first().node as KtToken).text
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
            val delegationSpecifierName = (delegationSpecifierSimpleIdentifier.first().node as KtToken).text

            val body = when {
                classDeclaration.containsType(KtRuleType.CLASS_BODY) -> {
                    classDeclaration.getChildAs(KtRuleType.CLASS_BODY, VkGrammarException())
                }
                classDeclaration.containsType(KtRuleType.ENUM_CLASS_BODY) -> {
                    classDeclaration.getChildAs(KtRuleType.ENUM_CLASS_BODY, VkGrammarException())
                }
                else -> null
            }

            return VkClassDeclaration(annotations, modifiers, name, delegationSpecifierName, body, classDeclaration.linePos)
        }
    }
}

data class VkFunctionDeclaration (val annotations: List<VkFunctionAnnotation>, val modifiers: List<VkFunctionModifier>,
                                  val name: String, val body: KtTree?, val linePos: LinePos): VkDeclaration() {

    companion object {

        operator fun invoke(functionDeclaration: KtTree): VkFunctionDeclaration {
            val annotations = getAnnotations(functionDeclaration) { VkFunctionAnnotation(it) }
            val modifiers = getModifiers(functionDeclaration) { VkFunctionModifier(it) }

            val simpleIdentifier = functionDeclaration.getChildAs(KtRuleType.SIMPLE_IDENTIFIER, VkGrammarException())
            val name = (simpleIdentifier.first().node as KtToken).text

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

            return VkFunctionDeclaration(annotations, modifiers, name, body, functionDeclaration.linePos)
        }
    }
}

data class VkPropertyDeclaration(val annotations: List<VkPropertyAnnotation>, val modifiers: List<VkPropertyModifier>,
                                 val name: String, val dataType: VkDataType, val linePos: LinePos): VkDeclaration() {

    companion object {

        operator fun invoke(propertyDeclaration: KtTree): VkPropertyDeclaration {
            val annotations = getAnnotations(propertyDeclaration) { VkPropertyAnnotation(it) }
            val modifiers = getModifiers(propertyDeclaration) { VkPropertyModifier(it) }

            val variableDeclaration = propertyDeclaration.getChildAs(KtRuleType.VARIABLE_DECLARATION, VkGrammarException())
            if (variableDeclaration.containsType(KtRuleType.TYPE)) {
                throw VkParseException(propertyDeclaration.linePos, "type declaration not permitted here")
            }
            val name = (variableDeclaration.first().first().node as KtToken).text
            if (name.isEmpty()) {
                throw VkParseException(variableDeclaration.linePos, "illegal name")
            }
            if (name[0] == '_') {
                throw VkParseException(variableDeclaration.linePos, "name must not begin with an underscore")
            }

            val expression = propertyDeclaration.getChildAs(KtRuleType.EXPRESSION,
                    VkParseException(propertyDeclaration.linePos, "type declaration expected"))
            val dataType = VkDataType.invoke(expression)

            return VkPropertyDeclaration(annotations, modifiers, name, dataType, propertyDeclaration.linePos)
        }
    }
}
