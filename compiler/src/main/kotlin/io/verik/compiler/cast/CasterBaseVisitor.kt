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

import io.verik.compiler.ast.element.common.*
import io.verik.compiler.ast.element.kt.KBasicClass
import io.verik.compiler.ast.element.kt.KFunction
import io.verik.compiler.ast.element.kt.KImportDirective
import io.verik.compiler.ast.element.kt.KProperty
import io.verik.compiler.ast.interfaces.cast
import io.verik.compiler.ast.property.FunctionAnnotationType
import io.verik.compiler.ast.property.Name
import io.verik.compiler.ast.property.SourceSetType
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.PackageDeclaration
import io.verik.compiler.common.getSourceLocation
import io.verik.compiler.core.CoreClass
import io.verik.compiler.main.ProjectContext
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
) : KtVisitor<CElement, Unit>() {

    private val mainPath = projectContext.config.projectDir.resolve("src/main/kotlin")
    private val testPath = projectContext.config.projectDir.resolve("src/test/kotlin")
    private val bindingContext = projectContext.bindingContext
    private val expressionVisitor = CasterExpressionVisitor(projectContext, declarationMap)

    inline fun <reified T : CElement> getElement(element: KtElement): T? {
        return element.accept(this, Unit).cast(element)
    }

    private fun getType(type: KotlinType, element: KtElement): Type {
        return TypeCaster.castFromType(declarationMap, type, element)
    }

    private fun getType(typeReference: KtTypeReference): Type {
        return TypeCaster.castFromTypeReference(bindingContext, declarationMap, typeReference)
    }

    override fun visitKtElement(element: KtElement, data: Unit?): CElement? {
        m.error("Unrecognized element: ${element::class.simpleName}", element)
        return null
    }

    override fun visitKtFile(file: KtFile, data: Unit?): CElement? {
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
        val packageDeclaration = PackageDeclaration(Name(file.packageFqName.toString()))
        val declarations = file.declarations.mapNotNull { getElement<CDeclaration>(it) }
        val importDirectives = file.importDirectives.mapNotNull { getElement<KImportDirective>(it) }

        return CFile(
            location,
            inputPath,
            null,
            relativePath,
            sourceSetType,
            null,
            packageDeclaration,
            ArrayList(declarations),
            importDirectives
        )
    }

    override fun visitImportDirective(importDirective: KtImportDirective, data: Unit?): CElement {
        val location = importDirective.getSourceLocation()
        val (name, packageDeclaration) = if (importDirective.isAllUnder) {
            Pair(
                null,
                PackageDeclaration(Name(importDirective.importedFqName!!.toString()))
            )
        } else {
            Pair(
                Name(importDirective.importedName!!.toString()),
                PackageDeclaration(Name(importDirective.importedFqName!!.parent().toString()))
            )
        }
        return KImportDirective(location, name, packageDeclaration)
    }

    override fun visitClassOrObject(classOrObject: KtClassOrObject, data: Unit?): CElement? {
        val descriptor = bindingContext.getSliceContents(BindingContext.CLASS)[classOrObject]!!
        val basicClass = declarationMap[descriptor, classOrObject]
            .cast<KBasicClass>(classOrObject)
            ?: return null

        val type = getType(descriptor.defaultType, classOrObject)
        val supertype = getType(descriptor.getSuperClassOrAny().defaultType, classOrObject)
        val typeParameters = classOrObject.typeParameters.mapNotNull { getElement<CTypeParameter>(it) }
        val body = classOrObject.body
        val declarations = body?.declarations
            ?.mapNotNull { getElement<CDeclaration>(it) }
            ?: listOf()

        basicClass.type = type
        basicClass.supertype = supertype
        typeParameters.forEach { it.parent = basicClass }
        basicClass.typeParameters = ArrayList(typeParameters)
        declarations.forEach { it.parent = basicClass }
        basicClass.declarations = ArrayList(declarations)
        return basicClass
    }

    override fun visitNamedFunction(function: KtNamedFunction, data: Unit?): CElement? {
        val descriptor = bindingContext.getSliceContents(BindingContext.FUNCTION)[function]!!
        val kFunction = declarationMap[descriptor, function]
            .cast<KFunction>(function)
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
            expressionVisitor.getElement<CBlockExpression>(it)
        }
        bodyBlockExpression?.parent = kFunction

        kFunction.type = type
        kFunction.bodyBlockExpression = bodyBlockExpression
        kFunction.annotationType = annotationType
        return kFunction
    }

    override fun visitProperty(property: KtProperty, data: Unit?): CElement? {
        val descriptor = bindingContext.getSliceContents(BindingContext.VARIABLE)[property]!!
        val kProperty = declarationMap[descriptor, property]
            .cast<KProperty>(property)
            ?: return null

        val typeReference = property.typeReference
        val type = if (typeReference != null) {
            getType(typeReference)
        } else {
            getType(descriptor.type, property)
        }
        val initializer = property.initializer?.let {
            expressionVisitor.getElement<CExpression>(it)
        }
        initializer?.parent = kProperty

        kProperty.type = type
        kProperty.initializer = initializer
        return kProperty
    }

    override fun visitTypeParameter(parameter: KtTypeParameter, data: Unit?): CElement? {
        val descriptor = bindingContext.getSliceContents(BindingContext.TYPE_PARAMETER)[parameter]!!
        val typeParameter = declarationMap[descriptor, parameter]
            .cast<CTypeParameter>(parameter)
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