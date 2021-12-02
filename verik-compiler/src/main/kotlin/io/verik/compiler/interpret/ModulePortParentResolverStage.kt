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

import io.verik.compiler.ast.element.sv.EModuleInterface
import io.verik.compiler.ast.element.sv.EModulePort
import io.verik.compiler.ast.element.sv.EModulePortInstantiation
import io.verik.compiler.ast.interfaces.cast
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object ModulePortParentResolverStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val modulePortParentResolverVisitor = ModulePortParentResolverVisitor()
        projectContext.project.accept(modulePortParentResolverVisitor)
        val multipleParentModulePorts = modulePortParentResolverVisitor.multipleParentModulePorts
        if (multipleParentModulePorts.isNotEmpty()) {
            val multipleParentVisitor = object : TreeVisitor() {
                override fun visitModulePortInstantiation(modulePortInstantiation: EModulePortInstantiation) {
                    super.visitModulePortInstantiation(modulePortInstantiation)
                    val parent = modulePortInstantiation.parent
                    if (parent !is EModuleInterface)
                        return
                    if (modulePortInstantiation.type.reference in multipleParentModulePorts) {
                        Messages.MODULE_PORT_MULTIPLE_PARENTS.on(modulePortInstantiation, parent.name)
                    }
                }
            }
            projectContext.project.accept(multipleParentVisitor)
        }
    }

    private class ModulePortParentResolverVisitor : TreeVisitor() {

        val multipleParentModulePorts = HashSet<EModulePort>()

        override fun visitModulePortInstantiation(modulePortInstantiation: EModulePortInstantiation) {
            super.visitModulePortInstantiation(modulePortInstantiation)
            val parent = modulePortInstantiation.parent
            if (parent is EModuleInterface) {
                val modulePort = modulePortInstantiation.type.reference.cast<EModulePort>(modulePortInstantiation)
                if (modulePort.parentModuleInterface != null && modulePort.parentModuleInterface != parent)
                    multipleParentModulePorts.add(modulePort)
                modulePort.parentModuleInterface = parent
            } else {
                Messages.MODULE_PORT_INSTANTIATION_OUT_OF_CONTEXT.on(modulePortInstantiation)
            }
        }
    }
}
