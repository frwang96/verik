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
import io.verik.compiler.ast.element.common.EValueParameter
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtEnumEntry
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.ETypeAlias
import io.verik.compiler.common.NullDeclaration
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.location
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.KtTypeAlias
import org.jetbrains.kotlin.psi.KtTypeParameter

object IndexerStage : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        val castContext = CastContext(projectContext.bindingContext)
        val indexerVisitor = IndexerVisitor(castContext)
        projectContext.ktFiles.forEach { it.accept(indexerVisitor) }
        projectContext.castContext = castContext
    }

    class IndexerVisitor(private val castContext: CastContext) : KtTreeVisitorVoid() {

        private val nameRegex = Regex("[_a-zA-Z][_a-zA-Z0-9]*")

        private fun checkDeclarationName(name: String, element: KtElement) {
            if (!name.matches(nameRegex))
                Messages.NAME_ILLEGAL.on(element, name)
        }

        override fun visitClassOrObject(classOrObject: KtClassOrObject) {
            super.visitClassOrObject(classOrObject)
            val descriptor = castContext.sliceClass[classOrObject]!!
            val location = classOrObject.nameIdentifier?.location()
                ?: classOrObject.getDeclarationKeyword()!!.location()
            val name = classOrObject.name!!
            checkDeclarationName(name, classOrObject)
            val basicClass = EKtBasicClass(
                location,
                name,
                NullDeclaration.toType(),
                arrayListOf(),
                arrayListOf(),
                listOf(),
                false,
                arrayListOf()
            )
            castContext.addDeclaration(descriptor, basicClass)
        }

        override fun visitEnumEntry(enumEntry: KtEnumEntry) {
            super.visitEnumEntry(enumEntry)
            val descriptor = castContext.sliceClass[enumEntry]!!
            val location = enumEntry.nameIdentifier!!.location()
            val name = enumEntry.name!!
            checkDeclarationName(name, enumEntry)
            val ktEnumEntry = EKtEnumEntry(location, name, NullDeclaration.toType(), listOf())
            castContext.addDeclaration(descriptor, ktEnumEntry)
        }

        override fun visitNamedFunction(function: KtNamedFunction) {
            super.visitNamedFunction(function)
            val descriptor = castContext.sliceFunction[function]!!
            val location = function.nameIdentifier!!.location()
            val name = function.name!!
            checkDeclarationName(name, function)
            val ktFunction = EKtFunction(location, name, arrayListOf(), NullDeclaration.toType(), null, listOf())
            castContext.addDeclaration(descriptor, ktFunction)
        }

        override fun visitProperty(property: KtProperty) {
            super.visitProperty(property)
            val descriptor = castContext.sliceVariable[property]!!
            val location = property.nameIdentifier!!.location()
            val name = property.name!!
            checkDeclarationName(name, property)
            val ktProperty = EKtProperty(location, name, NullDeclaration.toType(), null, listOf())
            castContext.addDeclaration(descriptor, ktProperty)
        }

        override fun visitTypeAlias(alias: KtTypeAlias) {
            super.visitTypeAlias(alias)
            val descriptor = castContext.sliceTypeAlias[alias]!!
            val location = alias.nameIdentifier!!.location()
            val name = alias.name!!
            checkDeclarationName(name, alias)
            val typeAlias = ETypeAlias(location, name, NullDeclaration.toType())
            castContext.addDeclaration(descriptor, typeAlias)
        }

        override fun visitTypeParameter(parameter: KtTypeParameter) {
            super.visitTypeParameter(parameter)
            val descriptor = castContext.sliceTypeParameter[parameter]!!
            val location = parameter.nameIdentifier!!.location()
            val name = parameter.name!!
            checkDeclarationName(name, parameter)
            val typeParameter = ETypeParameter(location, name, NullDeclaration.toType())
            castContext.addDeclaration(descriptor, typeParameter)
        }

        override fun visitParameter(parameter: KtParameter) {
            super.visitParameter(parameter)
            val descriptor = castContext.sliceValueParameter[parameter]!!
            val location = parameter.nameIdentifier!!.location()
            val name = parameter.name!!
            checkDeclarationName(name, parameter)
            val valueParameter = EValueParameter(location, name, listOf(), NullDeclaration.toType())
            castContext.addDeclaration(descriptor, valueParameter)
        }
    }
}
