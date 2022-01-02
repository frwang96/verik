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

package io.verik.compiler.check.pre

import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

object PreNameCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.getKtFiles().forEach {
            it.accept(PreNameCheckerVisitor)
        }
    }

    private object PreNameCheckerVisitor : KtTreeVisitorVoid() {

        private val nameRegex = Regex("[_a-zA-Z][_a-zA-Z0-9]*")

        override fun visitDeclaration(dcl: KtDeclaration) {
            super.visitDeclaration(dcl)
            val name = dcl.name!!
            if (!name.matches(nameRegex)) {
                Messages.ILLEGAL_NAME.on(dcl, name)
            } else if (name.startsWith("__")) {
                Messages.RESERVED_NAME.on(dcl, name)
            }
        }
    }
}
