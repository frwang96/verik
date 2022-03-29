/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.common

import io.verik.importer.ast.element.declaration.EEnum
import io.verik.importer.ast.element.declaration.EModule
import io.verik.importer.ast.element.declaration.EProperty
import io.verik.importer.ast.element.declaration.EStruct
import io.verik.importer.ast.element.declaration.ESvClass
import io.verik.importer.ast.element.declaration.ESvConstructor
import io.verik.importer.ast.element.declaration.ESvFunction
import io.verik.importer.ast.element.declaration.ESvPackage
import io.verik.importer.ast.element.declaration.ETask
import io.verik.importer.ast.element.declaration.ETypeAlias
import io.verik.importer.common.TreeVisitor
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

/**
 * Stage that counts the declarations in the AST.
 */
object DeclarationCounterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val declarationCounterVisitor = DeclarationCounterVisitor()
        projectContext.project.accept(declarationCounterVisitor)

        val counts = ArrayList<Pair<String, Int>>()
        counts.add(Pair("packages", declarationCounterVisitor.packageCount))
        counts.add(Pair("classes", declarationCounterVisitor.classCount))
        counts.add(Pair("modules", declarationCounterVisitor.moduleCount))
        counts.add(Pair("structs", declarationCounterVisitor.structCount))
        counts.add(Pair("enums", declarationCounterVisitor.enumCount))
        counts.add(Pair("typeAliases", declarationCounterVisitor.typeAliasCount))
        counts.add(Pair("functions", declarationCounterVisitor.functionCount))
        counts.add(Pair("tasks", declarationCounterVisitor.taskCount))
        counts.add(Pair("constructors", declarationCounterVisitor.constructorCount))
        counts.add(Pair("properties", declarationCounterVisitor.propertyCount))
        projectContext.report.counts = counts.filter { it.second != 0 }
    }

    private class DeclarationCounterVisitor : TreeVisitor() {

        var packageCount = 0
        var classCount = 0
        var moduleCount = 0
        var structCount = 0
        var enumCount = 0
        var typeAliasCount = 0
        var functionCount = 0
        var taskCount = 0
        var constructorCount = 0
        var propertyCount = 0

        override fun visitSvPackage(pkg: ESvPackage) {
            super.visitSvPackage(pkg)
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

        override fun visitStruct(struct: EStruct) {
            super.visitStruct(struct)
            structCount++
        }

        override fun visitEnum(enum: EEnum) {
            super.visitEnum(enum)
            enumCount++
        }

        override fun visitTypeAlias(typeAlias: ETypeAlias) {
            super.visitTypeAlias(typeAlias)
            typeAliasCount++
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

        override fun visitProperty(property: EProperty) {
            super.visitProperty(property)
            propertyCount++
        }
    }
}
