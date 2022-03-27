/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.ast.common.cast
import io.verik.compiler.ast.element.declaration.sv.EModuleInterface
import io.verik.compiler.ast.element.declaration.sv.EModulePort
import io.verik.compiler.ast.element.declaration.sv.EModulePortInstantiation
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object ModulePortParentResolverStage : ProjectStage() {

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
                Messages.ILLEGAL_MODULE_PORT_INSTANTIATION.on(modulePortInstantiation)
            }
        }
    }
}
