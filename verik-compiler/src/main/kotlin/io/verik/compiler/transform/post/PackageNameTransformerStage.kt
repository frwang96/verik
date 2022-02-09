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

import io.verik.compiler.ast.element.declaration.common.EPackage
import io.verik.compiler.ast.property.PackageType
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object PackageNameTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(PackageNameTransformerVisitor)
    }

    private object PackageNameTransformerVisitor : TreeVisitor() {

        override fun visitPackage(`package`: EPackage) {
            if (`package`.packageType == PackageType.REGULAR_NON_ROOT) {
                if (!`package`.name.matches(Regex("[a-zA-Z][a-zA-Z\\d]*(\\.[a-zA-Z][a-zA-Z\\d]*)*")))
                    Messages.INTERNAL_ERROR.on(`package`, "Unable to transform package name: ${`package`.name}")
                val names = `package`.name.split(".")
                val name = names.joinToString(separator = "_", postfix = "_pkg")
                `package`.name = name
            }
        }
    }
}
