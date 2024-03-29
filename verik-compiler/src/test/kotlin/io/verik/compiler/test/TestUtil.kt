/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.test

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EProject
import io.verik.compiler.ast.element.declaration.common.EAbstractFunction
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.declaration.kt.ESecondaryConstructor
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.common.TreeVisitor

fun EProject.findDeclaration(name: String): EDeclaration {
    val declarationVisitor = object : TreeVisitor() {
        val declarations = ArrayList<EDeclaration>()
        override fun visitElement(element: EElement) {
            super.visitElement(element)
            if (element is EDeclaration &&
                element !is EPrimaryConstructor &&
                element !is ESecondaryConstructor &&
                element.name == name
            ) declarations.add(element)
        }
    }
    accept(declarationVisitor)
    when (declarationVisitor.declarations.size) {
        0 -> throw IllegalArgumentException("Could not find declaration")
        1 -> {}
        else -> throw IllegalArgumentException("Could not find unique declaration")
    }
    return declarationVisitor.declarations[0]
}

fun EProject.findExpression(name: String): EExpression {
    val expressionVisitor = object : TreeVisitor() {
        val expressions = ArrayList<EExpression>()
        override fun visitAbstractFunction(abstractFunction: EAbstractFunction) {
            super.visitAbstractFunction(abstractFunction)
            if (abstractFunction.name == name) {
                abstractFunction.body.let {
                    if (it.statements.size == 1)
                        expressions.add(it.statements[0])
                    else
                        throw IllegalArgumentException("Function body has more than one expression")
                }
            }
        }
        override fun visitProperty(property: EProperty) {
            super.visitProperty(property)
            if (property.name == name) {
                property.initializer?.let { expressions.add(it) }
            }
        }
    }
    accept(expressionVisitor)
    when (expressionVisitor.expressions.size) {
        0 -> throw IllegalArgumentException("Could not find expression")
        1 -> {}
        else -> throw IllegalArgumentException("Could not find unique expression")
    }
    return expressionVisitor.expressions[0]
}

fun EProject.findStatements(name: String): List<EExpression> {
    val statementVisitor = object : TreeVisitor() {
        val statements = ArrayList<List<EExpression>>()
        override fun visitAbstractFunction(abstractFunction: EAbstractFunction) {
            super.visitAbstractFunction(abstractFunction)
            if (abstractFunction.name == name) {
                statements.add(abstractFunction.body.statements)
            }
        }
    }
    accept(statementVisitor)
    when (statementVisitor.statements.size) {
        0 -> throw IllegalArgumentException("Could not find statements")
        1 -> {}
        else -> throw IllegalArgumentException("Could not find unique statements")
    }
    return statementVisitor.statements[0]
}
