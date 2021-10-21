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

import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.NullDeclaration
import io.verik.compiler.core.common.CoreDeclarationMap
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType

class CastContext(
    bindingContext: BindingContext
) {

    val sliceClass = bindingContext.getSliceContents(BindingContext.CLASS)
    val sliceFunction = bindingContext.getSliceContents(BindingContext.FUNCTION)
    val sliceConstructor = bindingContext.getSliceContents(BindingContext.CONSTRUCTOR)
    val sliceVariable = bindingContext.getSliceContents(BindingContext.VARIABLE)
    val sliceTypeAlias = bindingContext.getSliceContents(BindingContext.TYPE_ALIAS)
    val sliceTypeParameter = bindingContext.getSliceContents(BindingContext.TYPE_PARAMETER)
    val sliceValueParameter = bindingContext.getSliceContents(BindingContext.VALUE_PARAMETER)
    val slicePrimaryConstructorParameter = bindingContext.getSliceContents(BindingContext.PRIMARY_CONSTRUCTOR_PARAMETER)
    val sliceAnnotation = bindingContext.getSliceContents(BindingContext.ANNOTATION)
    val sliceReferenceTarget = bindingContext.getSliceContents(BindingContext.REFERENCE_TARGET)
    val sliceType = bindingContext.getSliceContents(BindingContext.TYPE)
    val sliceAbbreviatedType = bindingContext.getSliceContents(BindingContext.ABBREVIATED_TYPE)
    val sliceExpressionTypeInfo = bindingContext.getSliceContents(BindingContext.EXPRESSION_TYPE_INFO)
    val sliceCall = bindingContext.getSliceContents(BindingContext.CALL)
    val sliceResolvedCall = bindingContext.getSliceContents(BindingContext.RESOLVED_CALL)

    val casterVisitor = CasterVisitor(this)

    private val declarationMap = HashMap<DeclarationDescriptor, Declaration>()

    fun addDeclaration(declarationDescriptor: DeclarationDescriptor, declaration: Declaration) {
        declarationMap[declarationDescriptor] = declaration
    }

    fun getDeclaration(declarationDescriptor: DeclarationDescriptor, element: KtElement): Declaration {
        val declaration = declarationMap[declarationDescriptor]
        if (declaration != null)
            return declaration
        if (declarationDescriptor is SimpleFunctionDescriptorImpl) {
            declarationDescriptor.overriddenDescriptors.forEach {
                val overriddenDeclaration = declarationMap[it]
                if (overriddenDeclaration != null)
                    return overriddenDeclaration
            }
        }
        val coreDeclaration = CoreDeclarationMap[this, declarationDescriptor, element]
        return if (coreDeclaration != null) {
            coreDeclaration
        } else {
            Messages.INTERNAL_ERROR.on(element, "Could not identify declaration: ${declarationDescriptor.name}")
            NullDeclaration
        }
    }

    fun castType(expression: KtExpression): Type {
        return TypeCaster.cast(this, expression)
    }

    fun castType(type: KotlinType, element: KtElement): Type {
        return TypeCaster.cast(this, type, element)
    }

    fun castType(typeReference: KtTypeReference): Type {
        return TypeCaster.cast(this, typeReference)
    }
}
