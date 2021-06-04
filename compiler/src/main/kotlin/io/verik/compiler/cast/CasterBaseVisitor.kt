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

import io.verik.compiler.ast.common.*
import io.verik.compiler.ast.element.*
import io.verik.compiler.core.CoreClass
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.getMessageLocation
import io.verik.compiler.main.m
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassOrAny
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isNullableAny
import org.jetbrains.kotlin.types.typeUtil.representativeUpperBound
import java.nio.file.Paths

class CasterBaseVisitor(
    projectContext: ProjectContext,
    private val declarationMap: DeclarationMap
): KtVisitor<VkElement, Unit>() {

    private val mainPath = projectContext.config.projectDir.resolve("src/main/kotlin")
    private val testPath = projectContext.config.projectDir.resolve("src/test/kotlin")
    private val bindingContext = projectContext.bindingContext
    private val expressionVisitor = CasterExpressionVisitor(projectContext, declarationMap)

    inline fun <reified T: VkElement> getElement(element: KtElement): T? {
        return element.accept(this, Unit).cast(element)
    }

    private fun getType(type: KotlinType, element: KtElement): Type {
        return TypeCaster.castType(declarationMap, type, element)
    }

    private fun getType(typeReference: KtTypeReference): Type {
        return TypeCaster.castType(bindingContext, declarationMap, typeReference)
    }

    override fun visitKtElement(element: KtElement, data: Unit?): VkElement? {
        m.error("Unrecognized element: ${element::class.simpleName}", element)
        return null
    }

    override fun visitKtFile(file: KtFile, data: Unit?): VkElement? {
        val location = file.getMessageLocation()
        val inputPath = Paths.get(file.virtualFilePath)
        val (sourceSetType, relativePath) = when {
            inputPath.startsWith(mainPath) -> Pair(SourceSetType.MAIN, mainPath.relativize(inputPath))
            inputPath.startsWith(testPath) -> Pair(SourceSetType.TEST, testPath.relativize(inputPath))
            else -> {
                m.error("Unable to identify as main or test source", file)
                return null
            }
        }
        val packageName = PackageName(file.packageFqName.toString())
        val declarations = file.declarations.mapNotNull { getElement<VkDeclaration>(it) }
        val importDirectives = file.importDirectives.mapNotNull { getElement<VkImportDirective>(it) }

        return VkFile(
            location,
            inputPath,
            relativePath,
            sourceSetType,
            packageName,
            ArrayList(declarations),
            importDirectives
        )
    }

    override fun visitImportDirective(importDirective: KtImportDirective, data: Unit?): VkElement {
        val location = importDirective.getMessageLocation()
        val (name, packageName) = if (importDirective.isAllUnder) {
            Pair(null, PackageName(importDirective.importedFqName!!.toString()))
        } else {
            Pair(
                Name(importDirective.importedName!!.toString()),
                PackageName(importDirective.importedFqName!!.parent().toString())
            )
        }
        return VkImportDirective(location, name, packageName)
    }

    override fun visitClassOrObject(classOrObject: KtClassOrObject, data: Unit?): VkElement? {
        val descriptor = bindingContext.getSliceContents(BindingContext.CLASS)[classOrObject]!!
        val type = getType(descriptor.defaultType, classOrObject)
        val supertype = getType(descriptor.getSuperClassOrAny().defaultType, classOrObject)
        val typeParameters = classOrObject.typeParameters.mapNotNull { getElement<VkTypeParameter>(it) }
        val body = classOrObject.body
        val declarations = body?.declarations
            ?.mapNotNull { getElement<VkDeclaration>(it) }
            ?: listOf()

        val baseClass = declarationMap[descriptor, classOrObject]
            .cast<VkBaseClass>(classOrObject)
            ?: return null

        baseClass.type = type
        baseClass.supertype = supertype
        typeParameters.forEach { it.parent = baseClass }
        baseClass.typeParameters = ArrayList(typeParameters)
        declarations.forEach { it.parent = baseClass }
        baseClass.declarations = ArrayList(declarations)
        return baseClass
    }

    override fun visitNamedFunction(function: KtNamedFunction, data: Unit?): VkElement? {
        val descriptor = bindingContext.getSliceContents(BindingContext.FUNCTION)[function]!!
        val type = getType(descriptor.returnType!!, function)
        val annotationTypes = descriptor.annotations.mapNotNull {
            FunctionAnnotationType(it.fqName, function)
        }
        val annotationType = when (annotationTypes.size) {
            0 -> null
            1 -> annotationTypes.first()
            else -> {
                val annotationTypesString = annotationTypes.joinToString{ it.name.toLowerCase() }
                m.error("Conflicting annotations: $annotationTypesString", function)
                null
            }
        }
        val bodyBlockExpression = function.bodyBlockExpression?.let {
            expressionVisitor.getElement<VkBlockExpression>(it)
        }

        val baseFunction = declarationMap[descriptor, function]
            .cast<VkBaseFunction>(function)
            ?: return null

        baseFunction.type = type
        baseFunction.annotationType = annotationType
        baseFunction.bodyBlockExpression = bodyBlockExpression
        return baseFunction
    }

    override fun visitProperty(property: KtProperty, data: Unit?): VkElement? {
        val descriptor = bindingContext.getSliceContents(BindingContext.VARIABLE)[property]!!
        val typeReference = property.typeReference
        val type = if (typeReference != null) {
            getType(typeReference)
        } else {
            getType(descriptor.type, property)
        }
        val initializer = property.initializer?.let {
            expressionVisitor.getElement<VkExpression>(it)
        }

        val baseProperty = declarationMap[descriptor, property]
            .cast<VkBaseProperty>(property)
            ?: return null

        baseProperty.type = type
        baseProperty.initializer = initializer
        return baseProperty
    }

    override fun visitTypeParameter(parameter: KtTypeParameter, data: Unit?): VkElement? {
        val descriptor = bindingContext.getSliceContents(BindingContext.TYPE_PARAMETER)[parameter]!!
        val upperBound = descriptor.representativeUpperBound
        val type = if (upperBound.isNullableAny()) {
            CoreClass.ANY.toNoArgumentsType()
        } else {
            getType(descriptor.representativeUpperBound, parameter)
        }

        val typeParameter = declarationMap[descriptor, parameter]
            .cast<VkTypeParameter>(parameter)
            ?: return null

        typeParameter.type = type
        return typeParameter
    }
}