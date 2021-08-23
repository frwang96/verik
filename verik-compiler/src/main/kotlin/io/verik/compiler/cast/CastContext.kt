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
import io.verik.compiler.main.m
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
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
    val sliceVariable = bindingContext.getSliceContents(BindingContext.VARIABLE)
    val sliceTypeParameter = bindingContext.getSliceContents(BindingContext.TYPE_PARAMETER)
    val sliceReferenceTarget = bindingContext.getSliceContents(BindingContext.REFERENCE_TARGET)
    val sliceType = bindingContext.getSliceContents(BindingContext.TYPE)
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
            ?: CoreDeclarationMap[this, declarationDescriptor, element]
        return if (declaration == null) {
            m.error("Could not identify declaration: ${declarationDescriptor.name}", element)
            NullDeclaration
        } else declaration
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
