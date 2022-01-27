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

import io.verik.compiler.ast.element.common.EBlockExpression
import io.verik.compiler.ast.element.common.EEnumEntry
import io.verik.compiler.ast.element.common.EProperty
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.element.kt.EKtClass
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.element.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.kt.ETypeAlias
import io.verik.compiler.common.endLocation
import io.verik.compiler.common.location
import io.verik.compiler.core.common.NullDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.KtTypeAlias
import org.jetbrains.kotlin.psi.KtTypeParameter

object CastIndexerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val castContext = CastContext(projectContext.bindingContext)
        val castIndexerVisitor = CastIndexerVisitor(castContext)
        projectContext.getKtFiles().forEach { it.accept(castIndexerVisitor) }
        projectContext.castContext = castContext
    }

    class CastIndexerVisitor(private val castContext: CastContext) : KtTreeVisitorVoid() {

        override fun visitTypeAlias(alias: KtTypeAlias) {
            super.visitTypeAlias(alias)
            val descriptor = castContext.sliceTypeAlias[alias]!!
            val location = alias.nameIdentifier!!.location()
            val name = alias.name!!
            val indexedTypeAlias = ETypeAlias(
                location,
                name,
                NullDeclaration.toType(),
                ArrayList()
            )
            castContext.registerDeclaration(descriptor, indexedTypeAlias)
        }

        override fun visitTypeParameter(parameter: KtTypeParameter) {
            super.visitTypeParameter(parameter)
            val descriptor = castContext.sliceTypeParameter[parameter]!!
            val location = parameter.nameIdentifier!!.location()
            val name = parameter.name!!
            val indexedTypeParameter = ETypeParameter(
                location,
                name,
                NullDeclaration.toType()
            )
            castContext.registerDeclaration(descriptor, indexedTypeParameter)
        }

        override fun visitClassOrObject(classOrObject: KtClassOrObject) {
            super.visitClassOrObject(classOrObject)
            val descriptor = castContext.sliceClass[classOrObject]!!
            val location = classOrObject.nameIdentifier?.location()
                ?: classOrObject.getDeclarationKeyword()!!.location()
            val bodyStartLocation = classOrObject.body?.lBrace?.location()
                ?: classOrObject.endLocation()
            val bodyEndLocation = classOrObject.endLocation()
            val name = classOrObject.name!!

            if (classOrObject.hasPrimaryConstructor() && !classOrObject.hasExplicitPrimaryConstructor()) {
                val indexedPrimaryConstructor = EPrimaryConstructor(
                    location,
                    name,
                    NullDeclaration.toType(),
                    ArrayList(),
                    ArrayList()
                )
                castContext.registerDeclaration(descriptor.unsubstitutedPrimaryConstructor!!, indexedPrimaryConstructor)
            }

            val indexedClass = EKtClass(
                location = location,
                bodyStartLocation = bodyStartLocation,
                bodyEndLocation = bodyEndLocation,
                name = name,
                type = NullDeclaration.toType(),
                annotationEntries = listOf(),
                documentationLines = null,
                superType = NullDeclaration.toType(),
                declarations = ArrayList(),
                typeParameters = ArrayList(),
                isEnum = false,
                isAbstract = false,
                isObject = false,
                primaryConstructor = null,
                superTypeCallExpression = null
            )
            castContext.registerDeclaration(descriptor, indexedClass)
        }

        override fun visitNamedFunction(function: KtNamedFunction) {
            super.visitNamedFunction(function)
            val descriptor = castContext.sliceFunction[function]!!
            val location = function.nameIdentifier!!.location()
            val name = function.name!!
            val indexedFunction = EKtFunction(
                location = location,
                name = name,
                type = NullDeclaration.toType(),
                annotationEntries = listOf(),
                documentationLines = null,
                body = EBlockExpression.empty(location),
                valueParameters = ArrayList(),
                typeParameters = ArrayList(),
                isAbstract = false,
                isOverride = false
            )
            castContext.registerDeclaration(descriptor, indexedFunction)
        }

        override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
            super.visitPrimaryConstructor(constructor)
            val descriptor = castContext.sliceConstructor[constructor]!!
            val location = constructor.location()
            val name = constructor.name!!
            val indexedPrimaryConstructor = EPrimaryConstructor(
                location,
                name,
                NullDeclaration.toType(),
                ArrayList(),
                ArrayList()
            )
            castContext.registerDeclaration(descriptor, indexedPrimaryConstructor)
        }

        override fun visitProperty(property: KtProperty) {
            super.visitProperty(property)
            val descriptor = castContext.sliceVariable[property]!!
            val location = property.nameIdentifier!!.location()
            val endLocation = property.endLocation()
            val name = property.name!!
            val indexedProperty = EProperty(
                location = location,
                endLocation = endLocation,
                name = name,
                type = NullDeclaration.toType(),
                annotationEntries = listOf(),
                documentationLines = null,
                initializer = null,
                isMutable = false
            )
            castContext.registerDeclaration(descriptor, indexedProperty)
        }

        override fun visitEnumEntry(enumEntry: KtEnumEntry) {
            super.visitEnumEntry(enumEntry)
            val descriptor = castContext.sliceClass[enumEntry]!!
            val location = enumEntry.nameIdentifier!!.location()
            val name = enumEntry.name!!
            val indexedEnumEntry = EEnumEntry(
                location = location,
                name = name,
                type = NullDeclaration.toType(),
                annotationEntries = listOf(),
                documentationLines = null,
            )
            castContext.registerDeclaration(descriptor, indexedEnumEntry)
        }

        override fun visitParameter(parameter: KtParameter) {
            super.visitParameter(parameter)
            val descriptor = castContext.slicePrimaryConstructorParameter[parameter]
                ?: castContext.sliceValueParameter[parameter]!!
            val location = parameter.nameIdentifier!!.location()
            val name = parameter.name!!
            val indexedValueParameter = EKtValueParameter(
                location = location,
                name = name,
                type = NullDeclaration.toType(),
                annotationEntries = listOf(),
                isPrimaryConstructorProperty = false,
                isMutable = false
            )
            castContext.registerDeclaration(descriptor, indexedValueParameter)
        }

        override fun visitLambdaExpression(lambdaExpression: KtLambdaExpression) {
            super.visitLambdaExpression(lambdaExpression)
            val functionLiteral = lambdaExpression.functionLiteral
            val functionDescriptor = castContext.sliceFunction[functionLiteral]!!
            if (!functionLiteral.hasParameterSpecification() && functionDescriptor.valueParameters.isNotEmpty()) {
                val parameterDescriptor = functionDescriptor.valueParameters[0]
                val location = functionLiteral.location()
                val name = parameterDescriptor.name.toString()
                val indexedValueParameter = EKtValueParameter(
                    location = location,
                    name = name,
                    type = NullDeclaration.toType(),
                    annotationEntries = listOf(),
                    isPrimaryConstructorProperty = false,
                    isMutable = false
                )
                castContext.registerDeclaration(parameterDescriptor, indexedValueParameter)
            }
        }
    }
}