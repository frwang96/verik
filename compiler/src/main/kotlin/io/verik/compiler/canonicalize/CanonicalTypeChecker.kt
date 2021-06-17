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

package io.verik.compiler.canonicalize

import io.verik.compiler.ast.common.TreeVisitor
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.VkDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m

object CanonicalTypeChecker {

    fun check(projectContext: ProjectContext) {
        projectContext.vkFiles.forEach {
            it.accept(CanonicalTypeVisitor)
        }
    }

    private fun checkType(type: Type, declaration: VkDeclaration) {
        if (!type.isCanonicalType())
            m.error("Type has not been canonicalized: declaration $declaration type $type", declaration)
    }

    object CanonicalTypeVisitor : TreeVisitor() {

        override fun visitDeclaration(declaration: VkDeclaration) {
            checkType(declaration.type, declaration)
            super.visitDeclaration(declaration)
        }
    }
}