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

import io.verik.compiler.ast.common.Declaration
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.common.ETypeParameter
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.EKtFunction
import io.verik.compiler.ast.element.declaration.kt.EKtValueParameter
import io.verik.compiler.ast.element.declaration.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.ENothingExpression
import io.verik.compiler.ast.element.expression.common.EPropertyStatement
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.common.location
import io.verik.compiler.core.common.CoreDeclarationMap
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.NullDeclaration
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.backend.common.serialization.findPackage
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.impl.PropertyDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.TypeAliasConstructorDescriptorImpl
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.overriddenTreeAsSequence
import org.jetbrains.kotlin.serialization.deserialization.descriptors.DeserializedTypeAliasDescriptor
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
    val sliceSmartCast = bindingContext.getSliceContents(BindingContext.SMARTCAST)

    val smartCastExpressions = HashSet<EReferenceExpression>()

    private val declarationMap = HashMap<DeclarationDescriptor, Declaration>()
    private val casterVisitor = CasterVisitor(this)

    fun registerDeclaration(declarationDescriptor: DeclarationDescriptor, declaration: Declaration) {
        declarationMap[declarationDescriptor] = declaration
    }

    fun resolveDeclaration(declarationDescriptor: DeclarationDescriptor, element: KtElement): Declaration {
        val unwrappedDeclarationDescriptor = unwrapDeclarationDescriptor(declarationDescriptor)
        val declaration = declarationMap[unwrappedDeclarationDescriptor]
        if (declaration != null)
            return declaration
        when (unwrappedDeclarationDescriptor) {
            is SimpleFunctionDescriptorImpl -> {
                unwrappedDeclarationDescriptor.overriddenTreeAsSequence(true).forEach {
                    val overriddenDeclaration = declarationMap[it]
                    if (overriddenDeclaration != null)
                        return overriddenDeclaration
                }
            }
            is PropertyDescriptorImpl -> {
                unwrappedDeclarationDescriptor.overriddenTreeAsSequence(true).forEach {
                    val overriddenDeclaration = declarationMap[it]
                    if (overriddenDeclaration != null)
                        return overriddenDeclaration
                }
            }
        }
        val coreDeclaration = CoreDeclarationMap[unwrappedDeclarationDescriptor]
        return if (coreDeclaration != null) {
            coreDeclaration
        } else {
            Messages.UNSUPPORTED_DECLARATION.on(element, unwrappedDeclarationDescriptor.name.asString())
            NullDeclaration
        }
    }

    fun castDeclaration(declaration: KtDeclaration): EDeclaration? {
        return casterVisitor.getElement(declaration)
    }

    fun castTypeParameter(typeParameter: KtTypeParameter): ETypeParameter? {
        return casterVisitor.getElement(typeParameter)
    }

    fun castValueParameter(parameter: KtParameter): EKtValueParameter? {
        return casterVisitor.getElement(parameter)
    }

    fun castPrimaryConstructor(primaryConstructor: KtPrimaryConstructor): EPrimaryConstructor? {
        return casterVisitor.getElement(primaryConstructor)
    }

    fun castExpression(expression: KtExpression): EExpression {
        val location = expression.location()
        return when (val element = casterVisitor.getElement<EElement>(expression)) {
            is EKtClass -> {
                Messages.ILLEGAL_LOCAL_DECLARATION.on(element, element.name)
                ENothingExpression(location)
            }
            is EKtFunction -> {
                Messages.ILLEGAL_LOCAL_DECLARATION.on(element, element.name)
                ENothingExpression(location)
            }
            is EProperty -> {
                EPropertyStatement(location, element)
            }
            is EExpression -> element
            null -> ENothingExpression(location)
            else -> {
                Messages.INTERNAL_ERROR.on(location, "Expression expected but got: ${element::class.simpleName}")
            }
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

    private fun unwrapDeclarationDescriptor(declarationDescriptor: DeclarationDescriptor): DeclarationDescriptor {
        return when (declarationDescriptor) {
            is TypeAliasConstructorDescriptorImpl -> declarationDescriptor.underlyingConstructorDescriptor
            is DeserializedTypeAliasDescriptor -> {
                val packageQualifiedName = declarationDescriptor.findPackage().fqName.asString()
                if (packageQualifiedName == CorePackage.KT_COLLECTIONS.name) {
                    declarationDescriptor.classDescriptor!!
                } else declarationDescriptor
            }
            else -> declarationDescriptor
        }
    }
}
