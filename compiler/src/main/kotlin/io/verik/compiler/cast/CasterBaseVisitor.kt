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
import io.verik.compiler.main.getSourceLocation
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
) : KtVisitor<VkElement, Unit>() {

    private val mainPath = projectContext.config.projectDir.resolve("src/main/kotlin")
    private val testPath = projectContext.config.projectDir.resolve("src/test/kotlin")
    private val bindingContext = projectContext.bindingContext
    private val expressionVisitor = CasterExpressionVisitor(projectContext, declarationMap)

    inline fun <reified T : VkElement> getElement(element: KtElement): T? {
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
        val location = file.getSourceLocation()
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
            null,
            relativePath,
            sourceSetType,
            null,
            packageName,
            ArrayList(declarations),
            importDirectives
        )
    }

    override fun visitImportDirective(importDirective: KtImportDirective, data: Unit?): VkElement {
        val location = importDirective.getSourceLocation()
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
        val ktClass = declarationMap[descriptor, classOrObject]
            .cast<VkKtClass>(classOrObject)
            ?: return null

        val type = getType(descriptor.defaultType, classOrObject)
        val supertype = getType(descriptor.getSuperClassOrAny().defaultType, classOrObject)
        val typeParameters = classOrObject.typeParameters.mapNotNull { getElement<VkTypeParameter>(it) }
        val body = classOrObject.body
        val declarations = body?.declarations
            ?.mapNotNull { getElement<VkDeclaration>(it) }
            ?: listOf()

        ktClass.type = type
        ktClass.supertype = supertype
        typeParameters.forEach { it.parent = ktClass }
        ktClass.typeParameters = ArrayList(typeParameters)
        declarations.forEach { it.parent = ktClass }
        ktClass.declarations = ArrayList(declarations)
        return ktClass
    }

    override fun visitNamedFunction(function: KtNamedFunction, data: Unit?): VkElement? {
        val descriptor = bindingContext.getSliceContents(BindingContext.FUNCTION)[function]!!
        val ktFunction = declarationMap[descriptor, function]
            .cast<VkKtFunction>(function)
            ?: return null

        val type = getType(descriptor.returnType!!, function)
        val annotationTypes = descriptor.annotations.mapNotNull {
            FunctionAnnotationType(it.fqName, function)
        }
        val annotationType = when (annotationTypes.size) {
            0 -> null
            1 -> annotationTypes.first()
            else -> {
                val annotationTypesString = annotationTypes.joinToString { it.toString() }
                m.error("Conflicting annotations: $annotationTypesString", function)
                null
            }
        }
        val bodyBlockExpression = function.bodyBlockExpression?.let {
            expressionVisitor.getElement<VkBlockExpression>(it)
        }
        bodyBlockExpression?.parent = ktFunction

        ktFunction.type = type
        ktFunction.bodyBlockExpression = bodyBlockExpression
        ktFunction.annotationType = annotationType
        return ktFunction
    }

    override fun visitProperty(property: KtProperty, data: Unit?): VkElement? {
        val descriptor = bindingContext.getSliceContents(BindingContext.VARIABLE)[property]!!
        val ktProperty = declarationMap[descriptor, property]
            .cast<VkKtProperty>(property)
            ?: return null

        val typeReference = property.typeReference
        val type = if (typeReference != null) {
            getType(typeReference)
        } else {
            getType(descriptor.type, property)
        }
        val initializer = property.initializer?.let {
            expressionVisitor.getElement<VkExpression>(it)
        }
        initializer?.parent = ktProperty

        ktProperty.type = type
        ktProperty.initializer = initializer
        return ktProperty
    }

    override fun visitTypeParameter(parameter: KtTypeParameter, data: Unit?): VkElement? {
        val descriptor = bindingContext.getSliceContents(BindingContext.TYPE_PARAMETER)[parameter]!!
        val typeParameter = declarationMap[descriptor, parameter]
            .cast<VkTypeParameter>(parameter)
            ?: return null

        val upperBound = descriptor.representativeUpperBound
        val type = if (upperBound.isNullableAny()) {
            CoreClass.Kotlin.ANY.toNoArgumentsType()
        } else {
            getType(descriptor.representativeUpperBound, parameter)
        }

        typeParameter.type = type
        return typeParameter
    }
}