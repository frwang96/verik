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

import io.verik.compiler.ast.element.common.EAbstractClass
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.common.MemberReplacer
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext

object MemberInterpreter : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        val memberReplacer = MemberReplacer(projectContext)
        val memberVisitor = MemberVisitor(memberReplacer)
        projectContext.files.forEach {
            it.accept(memberVisitor)
        }
        memberReplacer.updateReferences()
    }

    class MemberVisitor(private val memberReplacer: MemberReplacer) : TreeVisitor() {

        override fun visitFile(file: EFile) {
            file.members.forEach { interpretMember(it) }
            super.visitFile(file)
        }

        override fun visitAbstractClass(abstractClass: EAbstractClass) {
            abstractClass.members.forEach { interpretMember(it) }
            super.visitAbstractClass(abstractClass)
        }

        private fun interpretMember(member: EElement) {
            when (member) {
                is EKtBasicClass -> memberReplacer.replace(member, ClassInterpreter.interpret(member))
                is EKtFunction -> memberReplacer.replace(member, FunctionInterpreter.interpret(member))
                is EKtProperty -> memberReplacer.replace(member, PropertyInterpreter.interpret(member))
            }
        }
    }
}