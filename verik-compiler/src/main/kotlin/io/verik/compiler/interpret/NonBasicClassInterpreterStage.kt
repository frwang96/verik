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

package io.verik.compiler.interpret

import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.sv.EEnum
import io.verik.compiler.ast.element.sv.ESvEnumEntry
import io.verik.compiler.ast.interfaces.ResizableDeclarationContainer
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

object NonBasicClassInterpreterStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val nonBasicClassInterpreterVisitor = NonBasicClassInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(nonBasicClassInterpreterVisitor)
        val enumEntryCollectorVisitor = EnumEntryCollectorVisitor()
        projectContext.project.accept(enumEntryCollectorVisitor)
        enumEntryCollectorVisitor.enumEntries.forEach {
            val parent = it.parent
            if (parent is ResizableDeclarationContainer)
                parent.insertChild(it)
            else
                Messages.INTERNAL_ERROR.on(it, "Count not insert $it into $parent")
        }
        referenceUpdater.flush()
    }

    private class NonBasicClassInterpreterVisitor(private val referenceUpdater: ReferenceUpdater) : TreeVisitor() {

        override fun visitKtBasicClass(basicClass: EKtBasicClass) {
            super.visitKtBasicClass(basicClass)
            if (EnumInterpreter.interpretEnum(basicClass, referenceUpdater))
                return
            if (StructInterpreter.interpretStruct(basicClass, referenceUpdater))
                return
            ComponentInterpreter.interpretComponent(basicClass, referenceUpdater)
        }
    }

    private class EnumEntryCollectorVisitor : TreeVisitor() {

        val enumEntries = ArrayList<ESvEnumEntry>()

        override fun visitEnum(enum: EEnum) {
            enum.enumEntries.forEach {
                it.parent = enum.parent
                enumEntries.add(it)
            }
        }
    }
}
