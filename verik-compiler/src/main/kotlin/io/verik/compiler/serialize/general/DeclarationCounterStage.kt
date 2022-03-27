/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.serialize.general

import io.verik.compiler.ast.element.declaration.common.EPackage
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.sv.EAlwaysComBlock
import io.verik.compiler.ast.element.declaration.sv.EAlwaysSeqBlock
import io.verik.compiler.ast.element.declaration.sv.EComponentInstantiation
import io.verik.compiler.ast.element.declaration.sv.EConstraint
import io.verik.compiler.ast.element.declaration.sv.EEnum
import io.verik.compiler.ast.element.declaration.sv.EInitialBlock
import io.verik.compiler.ast.element.declaration.sv.EModule
import io.verik.compiler.ast.element.declaration.sv.EModuleInterface
import io.verik.compiler.ast.element.declaration.sv.EStruct
import io.verik.compiler.ast.element.declaration.sv.ESvClass
import io.verik.compiler.ast.element.declaration.sv.ESvConstructor
import io.verik.compiler.ast.element.declaration.sv.ESvFunction
import io.verik.compiler.ast.element.declaration.sv.ETask
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object DeclarationCounterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val declarationCounterVisitor = DeclarationCounterVisitor()
        projectContext.project.regularPackages().forEach {
            it.accept(declarationCounterVisitor)
        }

        val counts = ArrayList<Pair<String, Int>>()
        counts.add(Pair("packages", declarationCounterVisitor.packageCount))
        counts.add(Pair("classes", declarationCounterVisitor.classCount))
        counts.add(Pair("modules", declarationCounterVisitor.moduleCount))
        counts.add(Pair("moduleInterfaces", declarationCounterVisitor.moduleInterfaceCount))
        counts.add(Pair("enums", declarationCounterVisitor.enumCount))
        counts.add(Pair("structs", declarationCounterVisitor.structCount))
        counts.add(Pair("functions", declarationCounterVisitor.functionCount))
        counts.add(Pair("tasks", declarationCounterVisitor.taskCount))
        counts.add(Pair("constructors", declarationCounterVisitor.constructorCount))
        counts.add(Pair("initialBlocks", declarationCounterVisitor.initialBlockCount))
        counts.add(Pair("alwaysComBlocks", declarationCounterVisitor.alwaysComBlockCount))
        counts.add(Pair("alwaysSeqBlocks", declarationCounterVisitor.alwaysSeqBlockCount))
        counts.add(Pair("properties", declarationCounterVisitor.propertyCount))
        counts.add(Pair("componentInstantiations", declarationCounterVisitor.componentInstantiationCount))
        counts.add(Pair("constraints", declarationCounterVisitor.constraintCount))
        projectContext.report.counts = counts.filter { it.second != 0 }
    }

    class DeclarationCounterVisitor : TreeVisitor() {

        var packageCount = 0
        var classCount = 0
        var moduleCount = 0
        var moduleInterfaceCount = 0
        var enumCount = 0
        var structCount = 0
        var functionCount = 0
        var taskCount = 0
        var constructorCount = 0
        var initialBlockCount = 0
        var alwaysComBlockCount = 0
        var alwaysSeqBlockCount = 0
        var propertyCount = 0
        var componentInstantiationCount = 0
        var constraintCount = 0

        override fun visitPackage(pkg: EPackage) {
            super.visitPackage(pkg)
            packageCount++
        }

        override fun visitSvClass(cls: ESvClass) {
            super.visitSvClass(cls)
            classCount++
        }

        override fun visitModule(module: EModule) {
            super.visitModule(module)
            moduleCount++
        }

        override fun visitModuleInterface(moduleInterface: EModuleInterface) {
            super.visitModuleInterface(moduleInterface)
            moduleInterfaceCount++
        }

        override fun visitEnum(enum: EEnum) {
            super.visitEnum(enum)
            enumCount++
        }

        override fun visitStruct(struct: EStruct) {
            super.visitStruct(struct)
            structCount++
        }

        override fun visitSvFunction(function: ESvFunction) {
            super.visitSvFunction(function)
            functionCount++
        }

        override fun visitTask(task: ETask) {
            super.visitTask(task)
            taskCount++
        }

        override fun visitSvConstructor(constructor: ESvConstructor) {
            super.visitSvConstructor(constructor)
            constructorCount++
        }

        override fun visitInitialBlock(initialBlock: EInitialBlock) {
            super.visitInitialBlock(initialBlock)
            initialBlockCount++
        }

        override fun visitAlwaysComBlock(alwaysComBlock: EAlwaysComBlock) {
            super.visitAlwaysComBlock(alwaysComBlock)
            alwaysComBlockCount++
        }

        override fun visitAlwaysSeqBlock(alwaysSeqBlock: EAlwaysSeqBlock) {
            super.visitAlwaysSeqBlock(alwaysSeqBlock)
            alwaysSeqBlockCount++
        }

        override fun visitProperty(property: EProperty) {
            super.visitProperty(property)
            propertyCount++
        }

        override fun visitComponentInstantiation(componentInstantiation: EComponentInstantiation) {
            super.visitComponentInstantiation(componentInstantiation)
            componentInstantiationCount++
        }

        override fun visitConstraint(constraint: EConstraint) {
            super.visitConstraint(constraint)
            constraintCount++
        }
    }
}
