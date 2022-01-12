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

package io.verik.compiler.test

import io.verik.compiler.ast.element.common.EAbstractFunction
import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EProject
import io.verik.compiler.ast.element.common.EProperty
import io.verik.compiler.ast.element.kt.EKtConstructor
import io.verik.compiler.ast.element.kt.EPrimaryConstructor
import io.verik.compiler.common.TreeVisitor

fun EProject.findDeclaration(name: String): EDeclaration {
    val declarationVisitor = object : TreeVisitor() {
        val declarations = ArrayList<EDeclaration>()
        override fun visitElement(element: EElement) {
            super.visitElement(element)
            if (element is EDeclaration &&
                element !is EPrimaryConstructor &&
                element !is EKtConstructor &&
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
