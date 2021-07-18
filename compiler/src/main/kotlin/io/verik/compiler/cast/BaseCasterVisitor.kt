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
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.interfaces.cast
import io.verik.compiler.ast.property.FunctionAnnotationType
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
    private val castContext: CastContext
) : KtVisitor<EElement, Unit>() {

    private val mainDir = projectContext.config.mainDir
    private val expressionCasterVisitor = ExpressionCasterVisitor(castContext)

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
        val relativePath = when {
            inputPath.startsWith(mainDir) -> mainDir.relativize(inputPath)
            else -> {
                m.error("File should be located under main source directory", file)
                return ENullElement(location)
            }
        }
        val packageDeclaration = PackageDeclaration(file.packageFqName.asString())
        val members = file.declarations.mapNotNull { getElement(it) }

        return EFile(
            location,
            inputPath,
            null,
            relativePath,
            null,
            packageDeclaration,
            ArrayList(members)
        )
    }

    override fun visitClassOrObject(classOrObject: KtClassOrObject, data: Unit?): EElement {
        val location = classOrObject.location()
        val descriptor = castContext.bindingContext.getSliceContents(BindingContext.CLASS)[classOrObject]!!
        val basicClass = castContext.getDeclaration(descriptor, classOrObject)
            .cast<EKtBasicClass>(classOrObject)
            ?: return ENullExpression(location)

        val supertype = castContext.castType(descriptor.getSuperClassOrAny().defaultType, classOrObject)
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
        val descriptor = castContext.bindingContext.getSliceContents(BindingContext.FUNCTION)[function]!!
        val ktFunction = castContext.getDeclaration(descriptor, function)
            .cast<EKtFunction>(function)
            ?: return ENullExpression(location)

        val returnType = castContext.castType(descriptor.returnType!!, function)
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
        val descriptor = castContext.bindingContext.getSliceContents(BindingContext.VARIABLE)[property]!!
        val ktProperty = castContext.getDeclaration(descriptor, property)
            .cast<EKtProperty>(property)
            ?: return ENullExpression(location)

        val typeReference = property.typeReference
        val type = if (typeReference != null) castContext.castType(typeReference)
        else castContext.castType(descriptor.type, property)

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
        val descriptor = castContext.bindingContext.getSliceContents(BindingContext.TYPE_PARAMETER)[parameter]!!
        val typeParameter = castContext.getDeclaration(descriptor, parameter)
            .cast<ETypeParameter>(parameter)
            ?: return ENullExpression(location)

        val upperBound = descriptor.representativeUpperBound
        val typeConstraint = if (upperBound.isNullableAny()) Core.Kt.ANY.toType()
        else castContext.castType(descriptor.representativeUpperBound, parameter)

        typeParameter.typeConstraint = typeConstraint
        return typeParameter
    }
}