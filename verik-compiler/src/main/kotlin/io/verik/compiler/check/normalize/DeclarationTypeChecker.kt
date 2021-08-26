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

package io.verik.compiler.check.normalize

import io.verik.compiler.ast.element.common.EAbstractClass
import io.verik.compiler.ast.element.common.EAbstractEnumEntry
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.sv.EEnum
import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

object DeclarationTypeChecker : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        val declarationTypeVisitor = DeclarationTypeVisitor()
        projectContext.project.accept(declarationTypeVisitor)
    }

    class DeclarationTypeVisitor : TreeVisitor() {

        override fun visitFile(file: EFile) {
            super.visitFile(file)
            file.members.forEach {
                if (it !is Declaration)
                    Messages.INTERNAL_ERROR.on(it, "Declaration expected but got: $it")
            }
        }

        override fun visitAbstractClass(abstractClass: EAbstractClass) {
            super.visitAbstractClass(abstractClass)
            abstractClass.members.forEach {
                if (it !is Declaration)
                    Messages.INTERNAL_ERROR.on(it, "Declaration expected but got: $it")
            }
        }

        override fun visitEnum(enum: EEnum) {
            super.visitEnum(enum)
            enum.entryReferences.forEach {
                if (it !is EAbstractEnumEntry)
                    Messages.INTERNAL_ERROR.on(if (it is EElement) it else enum, "Enum entry expected but got: $it")
            }
        }
    }
}
