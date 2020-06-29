package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.KtRuleType
import com.verik.core.kt.KtToken
import com.verik.core.kt.KtTree

// Copyright (c) 2020 Francis Wang

data class VkPropertyDeclaration(val annotations: List<VkPropertyAnnotation>, val modifiers: List<VkPropertyModifier>,
                                 val name: String, val dataType: VkDataType, val linePos: LinePos) {
    companion object {
        operator fun invoke(declaration: KtTree): VkPropertyDeclaration {
            val propertyDeclaration = declaration.getChildAs(KtRuleType.PROPERTY_DECLARATION,
                    VkParseException(declaration.linePos, "property declaration expected"))
            val modifiersAndAnnotations = propertyDeclaration.getChildrenAs(KtRuleType.MODIFIERS).flatMap { it.children }
            val annotations = modifiersAndAnnotations
                    .filter { it.isType(KtRuleType.ANNOTATION) }
                    .map { VkPropertyAnnotation(it) }
            val modifiers = modifiersAndAnnotations
                    .filter { it.isType(KtRuleType.MODIFIER) }
                    .map { VkPropertyModifier(it) }

            val variableDeclaration = propertyDeclaration.getChildAs(KtRuleType.VARIABLE_DECLARATION, VkGrammarException())
            val simpleIdentifier = variableDeclaration.getDirectDescendantAs(KtRuleType.SIMPLE_IDENTIFIER,
                    VkParseException(propertyDeclaration.linePos, "property identifier expected"))
            val name = (simpleIdentifier.first().node as KtToken).text

            val expression = propertyDeclaration.getChildAs(KtRuleType.EXPRESSION,
                    VkParseException(propertyDeclaration.linePos, "type declaration expected"))
            val dataType = VkDataType.invoke(expression)

            return VkPropertyDeclaration(annotations, modifiers, name, dataType, propertyDeclaration.linePos)
        }

        fun isPropertyDeclaration(declaration: KtTree): Boolean {
            return declaration.first().isType(KtRuleType.PROPERTY_DECLARATION)
        }
    }
}