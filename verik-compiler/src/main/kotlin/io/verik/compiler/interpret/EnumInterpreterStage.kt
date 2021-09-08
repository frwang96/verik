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

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtEnumEntry
import io.verik.compiler.ast.element.sv.EEnum
import io.verik.compiler.ast.element.sv.ESvEnumEntry
import io.verik.compiler.ast.interfaces.ResizableElementContainer
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

object EnumInterpreterStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val enumInterpreterVisitor = EnumInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(enumInterpreterVisitor)
        enumInterpreterVisitor.insertionEntries.forEach {
            if (it.parent is ResizableElementContainer)
                it.parent.insertChild(it.enumEntry)
            else
                Messages.INTERNAL_ERROR.on(it.enumEntry, "Count not insert ${it.enumEntry} into ${it.parent}")
        }
        referenceUpdater.flush()
    }

    data class InsertionEntry(
        val enumEntry: ESvEnumEntry,
        val parent: EElement
    )

    class EnumInterpreterVisitor(private val referenceUpdater: ReferenceUpdater) : TreeVisitor() {

        val insertionEntries = ArrayList<InsertionEntry>()

        override fun visitKtBasicClass(basicClass: EKtBasicClass) {
            super.visitKtBasicClass(basicClass)
            if (basicClass.isEnum) {
                val entryReferences = basicClass.members.mapNotNull { it.cast<ESvEnumEntry>() }
                entryReferences.forEach {
                    insertionEntries.add(InsertionEntry(it, basicClass.parentNotNull()))
                }
                val enum = EEnum(
                    basicClass.location,
                    basicClass.name,
                    entryReferences
                )
                referenceUpdater.replace(basicClass, enum)
            }
        }

        override fun visitKtEnumEntry(enumEntry: EKtEnumEntry) {
            super.visitKtEnumEntry(enumEntry)
            referenceUpdater.replace(
                enumEntry,
                ESvEnumEntry(enumEntry.location, enumEntry.name, enumEntry.type)
            )
        }
    }
}
