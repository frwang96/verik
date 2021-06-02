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

import io.verik.compiler.ast.element.VkBaseClass
import io.verik.compiler.ast.element.VkModule
import io.verik.compiler.core.CoreClass
import io.verik.compiler.main.ProjectContext

object ClassInterpreter {

    fun interpret(projectContext: ProjectContext) {
        projectContext.vkFiles.forEach {
            it.declarations.forEach { declaration ->
                if (declaration is VkBaseClass) interpretBaseClass(declaration)
            }
        }
    }

    private fun interpretBaseClass(baseClass: VkBaseClass) {
        if (baseClass.type.isSubtypeOf(CoreClass.MODULE.getNoArgumentsType())) {
            baseClass.replace(VkModule(
                baseClass.location,
                baseClass.name,
                baseClass.type,
                baseClass.declarations
            ))
        }
    }
}