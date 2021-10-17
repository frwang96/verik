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

import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtEnumEntry
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.element.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.kt.ETypeAlias
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.location
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.KtTypeAlias
import org.jetbrains.kotlin.psi.KtTypeParameter

object DeclarationCastIndexerStage : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        val castContext = CastContext(projectContext.bindingContext)
        val declarationCastIndexerVisitor = DeclarationCastIndexerVisitor(castContext)
        projectContext.ktFiles.forEach { it.accept(declarationCastIndexerVisitor) }
        projectContext.castContext = castContext
    }

    class DeclarationCastIndexerVisitor(private val castContext: CastContext) : KtTreeVisitorVoid() {

        private val nameRegex = Regex("[_a-zA-Z][_a-zA-Z0-9]*")

        private fun checkDeclarationName(name: String, element: KtElement) {
            if (!name.matches(nameRegex))
                Messages.NAME_ILLEGAL.on(element, name)
        }

        override fun visitTypeAlias(alias: KtTypeAlias) {
            super.visitTypeAlias(alias)
            val descriptor = castContext.sliceTypeAlias[alias]!!
            val location = alias.nameIdentifier!!.location()
            val name = alias.name!!
            checkDeclarationName(name, alias)
            val indexedTypeAlias = ETypeAlias(location, name)
            castContext.addDeclaration(descriptor, indexedTypeAlias)
        }

        override fun visitTypeParameter(parameter: KtTypeParameter) {
            super.visitTypeParameter(parameter)
            val descriptor = castContext.sliceTypeParameter[parameter]!!
            val location = parameter.nameIdentifier!!.location()
            val name = parameter.name!!
            checkDeclarationName(name, parameter)
            val indexedTypeParameter = ETypeParameter(location, name)
            castContext.addDeclaration(descriptor, indexedTypeParameter)
        }

        override fun visitClassOrObject(classOrObject: KtClassOrObject) {
            super.visitClassOrObject(classOrObject)
            val descriptor = castContext.sliceClass[classOrObject]!!
            val location = classOrObject.nameIdentifier?.location()
                ?: classOrObject.getDeclarationKeyword()!!.location()
            val name = classOrObject.name!!
            checkDeclarationName(name, classOrObject)

            if (classOrObject.hasPrimaryConstructor() && !classOrObject.hasExplicitPrimaryConstructor()) {
                val indexedPrimaryConstructor = EPrimaryConstructor(location)
                castContext.addDeclaration(descriptor.unsubstitutedPrimaryConstructor!!, indexedPrimaryConstructor)
            }

            val indexedBasicClass = EKtBasicClass(location, name)
            castContext.addDeclaration(descriptor, indexedBasicClass)
        }

        override fun visitNamedFunction(function: KtNamedFunction) {
            super.visitNamedFunction(function)
            val descriptor = castContext.sliceFunction[function]!!
            val location = function.nameIdentifier!!.location()
            val name = function.name!!
            checkDeclarationName(name, function)
            val indexedFunction = EKtFunction(location, name)
            castContext.addDeclaration(descriptor, indexedFunction)
        }

        override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
            super.visitPrimaryConstructor(constructor)
            val descriptor = castContext.sliceConstructor[constructor]!!
            val location = constructor.location()
            val indexedPrimaryConstructor = EPrimaryConstructor(location)
            castContext.addDeclaration(descriptor, indexedPrimaryConstructor)
        }

        override fun visitProperty(property: KtProperty) {
            super.visitProperty(property)
            val descriptor = castContext.sliceVariable[property]!!
            val location = property.nameIdentifier!!.location()
            val name = property.name!!
            checkDeclarationName(name, property)
            val indexedProperty = EKtProperty(location, name)
            castContext.addDeclaration(descriptor, indexedProperty)
        }

        override fun visitEnumEntry(enumEntry: KtEnumEntry) {
            super.visitEnumEntry(enumEntry)
            val descriptor = castContext.sliceClass[enumEntry]!!
            val location = enumEntry.nameIdentifier!!.location()
            val name = enumEntry.name!!
            checkDeclarationName(name, enumEntry)
            val indexedEnumEntry = EKtEnumEntry(location, name)
            castContext.addDeclaration(descriptor, indexedEnumEntry)
        }

        override fun visitParameter(parameter: KtParameter) {
            super.visitParameter(parameter)
            val descriptor = castContext.slicePrimaryConstructorParameter[parameter]
                ?: castContext.sliceValueParameter[parameter]!!
            val location = parameter.nameIdentifier!!.location()
            val name = parameter.name!!
            checkDeclarationName(name, parameter)
            val indexedValueParameter = EKtValueParameter(location, name)
            castContext.addDeclaration(descriptor, indexedValueParameter)
        }

        override fun visitLambdaExpression(lambdaExpression: KtLambdaExpression) {
            super.visitLambdaExpression(lambdaExpression)
            val functionLiteral = lambdaExpression.functionLiteral
            val functionDescriptor = castContext.sliceFunction[functionLiteral]!!
            if (!functionLiteral.hasParameterSpecification() && functionDescriptor.valueParameters.isNotEmpty()) {
                val parameterDescriptor = functionDescriptor.valueParameters[0]
                val location = functionLiteral.location()
                val name = parameterDescriptor.name.toString()
                val indexedValueParameter = EKtValueParameter(location, name)
                castContext.addDeclaration(parameterDescriptor, indexedValueParameter)
            }
        }
    }
}
