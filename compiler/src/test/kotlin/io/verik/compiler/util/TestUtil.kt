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

package io.verik.compiler.util

import io.verik.compiler.ast.element.common.*
import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.common.ElementPrinter
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.TextFile
import org.junit.jupiter.api.Assertions.assertEquals

fun assertElementEquals(expected: String, actual: EElement) {
    val leftRoundBracketCount = expected.count { it == '(' }
    val rightRoundBracketCount = expected.count { it == ')' }
    val leftSquareBracketCount = expected.count { it == '[' }
    val rightSquareBracketCount = expected.count { it == ']' }
    assert(leftRoundBracketCount <= rightRoundBracketCount) { "Missing right round bracket" }
    assert(leftRoundBracketCount >= rightRoundBracketCount) { "Missing left round bracket" }
    assert(leftSquareBracketCount <= rightSquareBracketCount) { "Missing right square bracket" }
    assert(leftSquareBracketCount >= rightSquareBracketCount) { "Missing left square bracket" }

    val expectedBuilder = StringBuilder()
    expected.toCharArray().forEach {
        when (it) {
            in listOf(' ', '\n', '\t') -> {}
            ',' -> expectedBuilder.append(", ")
            else -> expectedBuilder.append(it)
        }
    }
    val expectedString = expectedBuilder.toString()
    val expectedStringArray = expectedString.toCharArray()

    val regexBuilder = StringBuilder()

    expectedStringArray.forEachIndexed { index, it ->
        when (it) {
            '*' -> {
                val previousIsBackTick = (index > 0) && (expectedStringArray[index - 1] == '`')
                val nextIsBackTick = (index < expectedStringArray.size - 1) && (expectedStringArray[index + 1] == '`')
                if (previousIsBackTick && nextIsBackTick)
                    regexBuilder.append("\\*")
                else
                    regexBuilder.append(".+")
            }
            in listOf('(', ')', '[', ']', '$', '?') -> regexBuilder.append("\\$it")
            else -> regexBuilder.append(it)
        }
    }
    val regexString = regexBuilder.toString()

    val actualString = ElementPrinter.dump(actual)
    assert(actualString.matches(Regex(regexString))) {
        "expected: <$expectedString> but was: <$actualString>"
    }
}

fun assertOutputTextEquals(expected: String, actual: TextFile) {
    val expectedLines = expected.lines()
        .dropLastWhile { it.isEmpty() }
    val actualLines = actual.content.lines()
        .let { it.subList(13, it.size) }
        .dropLastWhile { it.isEmpty() }

    assertEquals(expectedLines, actualLines)
}

fun ProjectContext.findDeclaration(name: String): EElement {
    val declarationVisitor = object : TreeVisitor() {
        val declarations = ArrayList<Declaration>()
        override fun visitElement(element: EElement) {
            super.visitElement(element)
            if (element is Declaration && element.name == name)
                declarations.add(element)
        }
    }
    project.accept(declarationVisitor)
    when (declarationVisitor.declarations.size) {
        0 -> throw IllegalArgumentException("Could not find declaration")
        1 -> {}
        else -> throw IllegalArgumentException("Could not find unique declaration")
    }
    return declarationVisitor.declarations[0] as EElement
}

fun ProjectContext.findExpression(name: String): EExpression {
    val expressionVisitor = object : TreeVisitor() {
        val expressions = ArrayList<EExpression>()
        override fun visitAbstractFunction(abstractFunction: EAbstractFunction) {
            super.visitAbstractFunction(abstractFunction)
            if (abstractFunction.name == name) {
                abstractFunction.body?.let {
                    if (it is EAbstractBlockExpression) {
                        if (it.statements.size == 1)
                            expressions.add(it.statements[0])
                        else
                            throw IllegalArgumentException("Function body block has more than one expression")
                    } else {
                        expressions.add(it)
                    }
                }
            }
        }
        override fun visitAbstractProperty(abstractProperty: EAbstractProperty) {
            super.visitAbstractProperty(abstractProperty)
            if (abstractProperty.name == name) {
                abstractProperty.initializer?.let { expressions.add(it) }
            }
        }
    }
    project.accept(expressionVisitor)
    when (expressionVisitor.expressions.size) {
        0 -> throw IllegalArgumentException("Could not find expression")
        1 -> {}
        else -> throw IllegalArgumentException("Could not find unique expression")
    }
    return expressionVisitor.expressions[0]
}