/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.verik.compiler.cast

import io.verik.compiler.ast.common.QualifiedName
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.descriptor.CardinalDescriptor
import io.verik.compiler.ast.descriptor.CardinalFunctionDescriptor
import io.verik.compiler.ast.descriptor.CardinalLiteralDescriptor
import io.verik.compiler.ast.descriptor.ClassDescriptor
import io.verik.compiler.core.CoreClass
import io.verik.compiler.main.messageCollector
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.serialization.deserialization.descriptors.DeserializedTypeAliasDescriptor
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.isNullable
import org.jetbrains.kotlin.types.typeUtil.getImmediateSuperclassNotAny

object TypeUtil {

    fun getType(type: KotlinType, element: PsiElement): Type {
        if (type.isNullable())
            messageCollector.error("Nullable type not supported: $type", element)
        val classDescriptor = getClassDescriptor(type, element)
        val arguments = type.arguments.map { getType(it.type, element) }
        return Type(classDescriptor, ArrayList(arguments))
    }

    fun getType(bindingContext: BindingContext, typeReference: KtTypeReference): Type {
        val type = bindingContext.getSliceContents(BindingContext.TYPE)[typeReference]!!
        if (type.isNullable())
            messageCollector.error("Nullable type not supported: $type", typeReference)
        val classDescriptor = getClassDescriptor(type, typeReference)

        val userType = typeReference.typeElement as KtUserType
        val arguments = userType.typeArgumentsAsTypes.map { getType(bindingContext, it) }
        return if (classDescriptor == CoreClass.CARDINAL) {
            val cardinalDescriptor = getCardinalDescriptor(bindingContext, userType)
            Type(cardinalDescriptor, ArrayList(arguments))
        } else {
            Type(classDescriptor, ArrayList(arguments))
        }
    }

    private fun getClassDescriptor(type: KotlinType, element: PsiElement): ClassDescriptor {
        val qualifiedName = QualifiedName(type.getJetTypeFqName(false))
        val name = qualifiedName.toName()
        val superclassDescriptor = type.getImmediateSuperclassNotAny().let {
            if (it != null) getClassDescriptor(it, element)
            else CoreClass.ANY
        }
        return ClassDescriptor(name, qualifiedName, superclassDescriptor)
    }

    private fun getCardinalDescriptor(bindingContext: BindingContext, userType: KtUserType): CardinalDescriptor {
        val referenceExpression = userType.referenceExpression!!
        val referenceTarget = bindingContext
            .getSliceContents(BindingContext.REFERENCE_TARGET)[referenceExpression]!!
        return if (referenceTarget is DeserializedTypeAliasDescriptor) {
            val simpleType = referenceTarget.defaultType
            val qualifiedName = QualifiedName(simpleType.getJetTypeFqName(false))
            val name = qualifiedName.toName()
            val cardinal = name.name.toIntOrNull()
            if (cardinal != null) {
                if (cardinal < 1)
                    messageCollector.error("Cardinal must be a positive integer: $cardinal", referenceExpression)
                CardinalLiteralDescriptor(cardinal)
            } else {
                CardinalFunctionDescriptor(qualifiedName)
            }
        } else {
            messageCollector.error("Cardinal expression expected", userType)
            CardinalLiteralDescriptor(1)
        }
    }
}