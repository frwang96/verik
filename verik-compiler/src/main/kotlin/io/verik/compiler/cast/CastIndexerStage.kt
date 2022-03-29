/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.cast

import io.verik.compiler.ast.element.declaration.common.EEnumEntry
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.common.ETypeParameter
import io.verik.compiler.ast.element.declaration.kt.ECompanionObject
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.EKtFunction
import io.verik.compiler.ast.element.declaration.kt.EKtValueParameter
import io.verik.compiler.ast.element.declaration.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.declaration.kt.ESecondaryConstructor
import io.verik.compiler.ast.element.declaration.kt.ETypeAlias
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.common.endLocation
import io.verik.compiler.common.location
import io.verik.compiler.core.common.NullDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.KtTypeAlias
import org.jetbrains.kotlin.psi.KtTypeParameter

/**
 * Stage that indexes the declarations of the Kotlin AST to build a [CastContext].
 */
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

            if (classOrObject is KtObjectDeclaration && classOrObject.isCompanion()) {
                val indexedCompanionObject = ECompanionObject(
                    location,
                    NullDeclaration.toType(),
                    ArrayList()
                )
                castContext.registerDeclaration(descriptor, indexedCompanionObject)
                return
            }

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
                    null
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
                typeParameters = ArrayList(),
                declarations = ArrayList(),
                primaryConstructor = null,
                isEnum = false,
                isObject = false
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
                overriddenFunction = null
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
                null
            )
            castContext.registerDeclaration(descriptor, indexedPrimaryConstructor)
        }

        override fun visitSecondaryConstructor(constructor: KtSecondaryConstructor) {
            super.visitSecondaryConstructor(constructor)
            val descriptor = castContext.sliceConstructor[constructor]!!
            val location = constructor.location()
            val name = constructor.name!!
            val indexedSecondaryConstructor = ESecondaryConstructor(
                location = location,
                name = name,
                type = NullDeclaration.toType(),
                documentationLines = null,
                body = EBlockExpression.empty(location),
                valueParameters = ArrayList(),
                superTypeCallExpression = null
            )
            castContext.registerDeclaration(descriptor, indexedSecondaryConstructor)
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
                isMutable = false,
                isStatic = false
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
                expression = null
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
                expression = null,
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
                    expression = null,
                    isPrimaryConstructorProperty = false,
                    isMutable = false
                )
                castContext.registerDeclaration(parameterDescriptor, indexedValueParameter)
            }
        }
    }
}
