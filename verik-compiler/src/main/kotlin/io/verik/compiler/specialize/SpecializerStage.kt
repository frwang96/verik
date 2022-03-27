/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.specialize

import io.verik.compiler.ast.element.declaration.common.EAbstractClass
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.kt.ECompanionObject
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.core.common.Core
import io.verik.compiler.evaluate.CardinalTypeEvaluatorSubstage
import io.verik.compiler.evaluate.ConstantPropagatorSubstage
import io.verik.compiler.evaluate.ExpressionEvaluatorSubstage
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.backend.common.pop

object SpecializerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val entryPoints = EntryPointIndexer.getEntryPoints(projectContext)
        val typeParameterBindings = ArrayList(entryPoints.map { TypeParameterBinding(it, listOf()) })
        val specializeContext = SpecializeContext()
        while (typeParameterBindings.isNotEmpty()) {
            val typeParameterBinding = typeParameterBindings.pop()
            if (!specializeContext.contains(typeParameterBinding)) {
                typeParameterBindings.addAll(specialize(typeParameterBinding, specializeContext))
            }
        }

        projectContext.project.files().forEach { file ->
            val declarations = file.declarations.flatMap { getSpecializedDeclarations(it, specializeContext) }
            declarations.forEach { it.parent = file }
            file.declarations = ArrayList(declarations)
        }
        projectContext.specializeContext = specializeContext
    }

    private fun specialize(
        typeParameterBinding: TypeParameterBinding,
        specializeContext: SpecializeContext
    ): List<TypeParameterBinding> {
        val declaration = SpecializerCopier.copy(
            typeParameterBinding.declaration,
            typeParameterBinding.typeArguments,
            specializeContext
        )
        ConstantPropagatorSubstage.process(declaration, typeParameterBinding)
        TypeParameterSubstitutorSubstage.process(declaration, typeParameterBinding)
        CardinalTypeEvaluatorSubstage.process(declaration, typeParameterBinding)
        ExpressionEvaluatorSubstage.process(declaration, typeParameterBinding)
        OptionalReducerSubstage.process(declaration, typeParameterBinding)
        return SpecializerIndexer.index(declaration)
    }

    private fun getSpecializedDeclarations(
        declaration: EDeclaration,
        specializeContext: SpecializeContext
    ): List<EDeclaration> {
        val typeParameterBindings = specializeContext.getTypeParameterBindings(declaration)
        val declarations = typeParameterBindings.map { it.declaration }
        return when (declaration) {
            is EKtClass -> {
                val classDeclarations = declaration.declarations
                    .filterIsInstance<EAbstractClass>()
                    .flatMap { getSpecializedDeclarations(it, specializeContext) }
                if (classDeclarations.isEmpty()) return declarations

                val classWithoutTypeArguments = typeParameterBindings
                    .find { it.typeArguments.isEmpty() }
                    ?.let { it.declaration as EKtClass }
                if (classWithoutTypeArguments != null) {
                    if (declarations.size != 1) {
                        Messages.INTERNAL_ERROR.on(declaration, "Unexpected class with type arguments")
                    }
                    classDeclarations.forEach { it.parent = classWithoutTypeArguments }
                    classWithoutTypeArguments.declarations.addAll(classDeclarations)
                    listOf(classWithoutTypeArguments)
                } else {
                    val cls = EKtClass(
                        location = declaration.location,
                        bodyStartLocation = declaration.location,
                        bodyEndLocation = declaration.location,
                        name = declaration.name,
                        type = declaration.toType(),
                        annotationEntries = listOf(),
                        documentationLines = null,
                        superType = Core.Vk.C_Class.toType(),
                        typeParameters = ArrayList(),
                        declarations = ArrayList(classDeclarations),
                        primaryConstructor = null,
                        isEnum = false,
                        isObject = false
                    )
                    specializeContext.register(declaration, listOf(), cls)
                    listOf(cls) + declarations
                }
            }
            is ECompanionObject -> {
                val companionObjectDeclarations = declaration.declarations
                    .flatMap { getSpecializedDeclarations(it, specializeContext) }
                if (companionObjectDeclarations.isEmpty()) return declarations

                val companionObjectOrNull = typeParameterBindings
                    .find { it.typeArguments.isEmpty() }
                    ?.let { it.declaration as ECompanionObject }
                if (companionObjectOrNull != null) {
                    if (declarations.size != 1) {
                        Messages.INTERNAL_ERROR.on(declaration, "Unexpected companion object with type arguments")
                    }
                    companionObjectDeclarations.forEach { it.parent = companionObjectOrNull }
                    companionObjectOrNull.declarations.addAll(companionObjectDeclarations)
                    listOf(companionObjectOrNull)
                } else {
                    val companionObject = ECompanionObject(
                        declaration.location,
                        declaration.type.copy(),
                        ArrayList(companionObjectDeclarations)
                    )
                    specializeContext.register(declaration, listOf(), companionObject)
                    listOf(companionObject)
                }
            }
            else -> declarations
        }
    }
}
