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

package io.verik.compiler.transform.post

import io.verik.compiler.ast.element.common.EBasicPackage
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

object PackageNameTransformerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(PackageNameTransformerVisitor)
    }

    private object PackageNameTransformerVisitor : TreeVisitor() {

        override fun visitBasicPackage(basicPackage: EBasicPackage) {
            val name = if (basicPackage.name == "") {
                Messages.INTERNAL_ERROR.on(basicPackage, "Package name cannot be empty")
                "pkg"
            } else {
                val names = basicPackage.name.split(".")
                names.joinToString(separator = "_", postfix = "_pkg")
            }
            basicPackage.name = name
        }
    }
}
