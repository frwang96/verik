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
import io.verik.compiler.ast.element.kt.EImportDirective
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.interfaces.cast
import io.verik.compiler.ast.property.FunctionAnnotationType
import io.verik.compiler.ast.property.Name
import io.verik.compiler.ast.property.SourceSetType
import io.verik.compiler.common.PackageDeclaration
import io.verik.compiler.common.location
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassOrAny
import org.jetbrains.kotlin.types.typeUtil.isNullableAny
import org.jetbrains.kotlin.types.typeUtil.representativeUpperBound
import java.nio.file.Paths

class BaseCasterVisitor(
    projectContext: ProjectContext,
    private val declarationMap: DeclarationMap
) : KtVisitor<EElement, Unit>() {

    private val mainPath = projectContext.config.projectDir.resolve("src/main/kotlin")
    private val testPath = projectContext.config.projectDir.resolve("src/test/kotlin")
    private val bindingContext = projectContext.bindingContext
    private val typeCaster = declarationMap.typeCaster
    private val expressionCasterVisitor = ExpressionCasterVisitor(projectContext, declarationMap)

    inline fun <reified T : EElement> getElement(element: KtElement): T? {
        return element.accept(this, Unit).cast()
    }

    override fun visitKtElement(element: KtElement, data: Unit?): EElement {
        m.error("Unrecognized element: $element", element)
        val location = element.location()
        return ENullElement(location)
    }

    override fun visitKtFile(file: KtFile, data: Unit?): EElement {
        val location = file.location()
        val inputPath = Paths.get(file.virtualFilePath)
        val (sourceSetType, relativePath) = when {
            inputPath.startsWith(mainPath) -> Pair(SourceSetType.MAIN, mainPath.relativize(inputPath))
            inputPath.startsWith(testPath) -> Pair(SourceSetType.TEST, testPath.relativize(inputPath))
            else -> {
                m.error("Unable to identify as main or test source", file)
                return ENullElement(location)
            }
        }
        val packageDeclaration = PackageDeclaration(Name(file.packageFqName.toString()))
        val members = file.declarations.mapNotNull { getElement(it) }
        val importDirectives = file.importDirectives.mapNotNull { getElement<EImportDirective>(it) }

        return EFile(
            location,
            inputPath,
            null,
            relativePath,
            sourceSetType,
            null,
            packageDeclaration,
            ArrayList(members),
            importDirectives
        )
    }

    override fun visitImportDirective(importDirective: KtImportDirective, data: Unit?): EElement {
        val location = importDirective.location()
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
        return EImportDirective(location, name, packageDeclaration)
    }

    override fun visitClassOrObject(classOrObject: KtClassOrObject, data: Unit?): EElement {
        val location = classOrObject.location()
        val descriptor = bindingContext.getSliceContents(BindingContext.CLASS)[classOrObject]!!
        val basicClass = declarationMap[descriptor, classOrObject]
            .cast<EKtBasicClass>(classOrObject)
            ?: return ENullExpression(location)

        val supertype = typeCaster.cast(descriptor.getSuperClassOrAny().defaultType, classOrObject)
        val typeParameters = classOrObject.typeParameters.mapNotNull { getElement<ETypeParameter>(it) }
        val members = classOrObject.declarations.mapNotNull { getElement(it) }

        basicClass.supertype = supertype
        typeParameters.forEach { it.parent = basicClass }
        basicClass.typeParameters = ArrayList(typeParameters)
        members.forEach { it.parent = basicClass }
        basicClass.members = ArrayList(members)
        return basicClass
    }

    override fun visitNamedFunction(function: KtNamedFunction, data: Unit?): EElement {
        val location = function.location()
        val descriptor = bindingContext.getSliceContents(BindingContext.FUNCTION)[function]!!
        val ktFunction = declarationMap[descriptor, function]
            .cast<EKtFunction>(function)
            ?: return ENullExpression(location)

        val returnType = typeCaster.cast(descriptor.returnType!!, function)
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
        val body = function.bodyBlockExpression?.let {
            expressionCasterVisitor.getElement<EExpression>(it)
        }
        body?.parent = ktFunction

        ktFunction.returnType = returnType
        ktFunction.body = body
        ktFunction.annotationType = annotationType
        return ktFunction
    }

    override fun visitProperty(property: KtProperty, data: Unit?): EElement {
        val location = property.location()
        val descriptor = bindingContext.getSliceContents(BindingContext.VARIABLE)[property]!!
        val ktProperty = declarationMap[descriptor, property]
            .cast<EKtProperty>(property)
            ?: return ENullExpression(location)

        val typeReference = property.typeReference
        val type = if (typeReference != null) typeCaster.cast(typeReference)
        else typeCaster.cast(descriptor.type, property)

        val initializer = property.initializer?.let {
            expressionCasterVisitor.getExpression(it)
        }
        initializer?.parent = ktProperty

        ktProperty.type = type
        ktProperty.initializer = initializer
        return ktProperty
    }

    override fun visitTypeParameter(parameter: KtTypeParameter, data: Unit?): EElement {
        val location = parameter.location()
        val descriptor = bindingContext.getSliceContents(BindingContext.TYPE_PARAMETER)[parameter]!!
        val typeParameter = declarationMap[descriptor, parameter]
            .cast<ETypeParameter>(parameter)
            ?: return ENullExpression(location)

        val upperBound = descriptor.representativeUpperBound
        val typeConstraint = if (upperBound.isNullableAny()) Core.Kt.ANY.toType()
        else typeCaster.cast(descriptor.representativeUpperBound, parameter)

        typeParameter.typeConstraint = typeConstraint
        return typeParameter
    }
}