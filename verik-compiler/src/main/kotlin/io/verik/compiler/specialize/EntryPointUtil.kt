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

package io.verik.compiler.specialize

import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.interfaces.Annotated
import io.verik.compiler.ast.interfaces.TypeParameterized
import io.verik.compiler.core.common.Annotations
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.backend.common.push

object EntryPointUtil {

    fun getEntryPoints(projectContext: ProjectContext): List<EDeclaration> {
        val entryPoints = ArrayList<EDeclaration>()
        if (projectContext.config.enableDeadCodeElimination) {
            projectContext.project.files().forEach { file ->
                file.declarations.forEach {
                    if (it is Annotated && it.hasAnnotation(Annotations.TOP)) {
                        if (it is TypeParameterized && it.typeParameters.isNotEmpty()) {
                            Messages.TYPE_PARAMETERS_ON_TOP.on(it)
                        } else {
                            entryPoints.add(it)
                            if (it is EKtBasicClass)
                                entryPoints.addAll(getKtBasicClassEntryPoints(it, true))
                        }
                    }
                }
            }
            if (entryPoints.isEmpty())
                Messages.NO_TOP_DECLARATIONS.on(projectContext.project)
        } else {
            projectContext.project.files().forEach { file ->
                file.declarations.forEach {
                    if (it !is TypeParameterized || it.typeParameters.isEmpty()) {
                        entryPoints.add(it)
                        if (it is EKtBasicClass)
                            entryPoints.addAll(getKtBasicClassEntryPoints(it, false))
                    }
                }
            }
        }
        return entryPoints
    }

    fun getKtBasicClassEntryPoints(
        basicClass: EKtBasicClass,
        enableDeadCodeElimination: Boolean
    ): List<EDeclaration> {
        val entryPoints = ArrayList<EDeclaration>()
        if (enableDeadCodeElimination) {
            basicClass.declarations.forEach {
                when (it) {
                    is EKtFunction -> {
                        if (it.typeParameters.isEmpty()) {
                            if (it.hasAnnotation(Annotations.COM) ||
                                it.hasAnnotation(Annotations.SEQ) ||
                                it.hasAnnotation(Annotations.RUN)
                            ) {
                                entryPoints.push(it)
                            }
                        }
                    }
                    is EKtProperty -> {
                        if (it.hasAnnotation(Annotations.MAKE)) {
                            entryPoints.push(it)
                        }
                    }
                }
            }
        } else {
            basicClass.declarations.forEach {
                if (it !is TypeParameterized || it.typeParameters.isEmpty())
                    entryPoints.push(it)
            }
        }
        return entryPoints
    }
}
