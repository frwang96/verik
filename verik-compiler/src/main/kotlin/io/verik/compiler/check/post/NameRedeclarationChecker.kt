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

package io.verik.compiler.check.post

import io.verik.compiler.ast.element.common.EAbstractPackage
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m

object NameRedeclarationChecker : PostCheckerStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(NameRedeclarationVisitor)
    }

    class MemberSet {

        private val members = ArrayList<EElement>()
        private val names = HashSet<String>()
        private val duplicateNames = HashSet<String>()

        fun add(member: EElement) {
            if (member is Declaration) {
                members.add(member)
                if (member.name in names)
                    duplicateNames.add(member.name)
                else
                    names.add(member.name)
            }
        }

        fun checkDuplicates() {
            members.forEach {
                if (it is Declaration && it.name in duplicateNames) {
                    m.error("Name has already been declared: ${it.name}", it)
                }
            }
        }
    }

    object NameRedeclarationVisitor : TreeVisitor() {

        override fun visitAbstractPackage(abstractPackage: EAbstractPackage) {
            super.visitAbstractPackage(abstractPackage)
            val memberSet = MemberSet()
            abstractPackage.files.forEach { file ->
                file.members.forEach { memberSet.add(it) }
            }
            memberSet.checkDuplicates()
        }
    }
}