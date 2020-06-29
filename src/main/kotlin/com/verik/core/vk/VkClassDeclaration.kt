package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.KtRuleType
import com.verik.core.kt.KtToken
import com.verik.core.kt.KtTokenType
import com.verik.core.kt.KtTree

// Copyright (c) 2020 Francis Wang

data class VkClassDeclaration(val annotations: List<VkClassAnnotation>, val modifiers: List<VkClassModifier>,
                              val name: String, val delegationSpecifierName: String, val body: KtTree?, val linePos: LinePos) {
    companion object {
        operator fun invoke(topLevelObject: KtTree): VkClassDeclaration {
            val classDeclaration = topLevelObject.getDirectDescendantAs(KtRuleType.CLASS_DECLARATION,
                    VkParseException(topLevelObject.linePos, "class declaration expected"))
            val modifiersAndAnnotations = classDeclaration.getChildrenAs(KtRuleType.MODIFIERS).flatMap { it.children }
            val annotations = modifiersAndAnnotations
                    .filter { it.isType(KtRuleType.ANNOTATION) }
                    .map { VkClassAnnotation(it) }
            val modifiers = modifiersAndAnnotations
                    .filter { it.isType(KtRuleType.MODIFIER) }
                    .map { VkClassModifier(it) }

            if (classDeclaration.containsType(KtTokenType.INTERFACE)) {
                throw VkParseException(classDeclaration.linePos, "class interfaces are not supported")
            }

            val simpleIdentifier = classDeclaration.getChildAs(KtRuleType.SIMPLE_IDENTIFIER, VkGrammarException())
            val name = (simpleIdentifier.first().node as KtToken).text

            if (classDeclaration.containsType(KtRuleType.TYPE_PARAMETERS)) {
                throw VkParseException(topLevelObject.linePos, "type parameters are not supported")
            }
            if (classDeclaration.containsType(KtRuleType.PRIMARY_CONSTRUCTOR)) {
                throw VkParseException(topLevelObject.linePos, "constructor is not supported")
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

        fun isClassDeclaration(topLevelObject: KtTree): Boolean {
            val declaration = topLevelObject.getChildAs(KtRuleType.DECLARATION, VkGrammarException())
            return declaration.first().isType(KtRuleType.CLASS_DECLARATION)
        }
    }
}