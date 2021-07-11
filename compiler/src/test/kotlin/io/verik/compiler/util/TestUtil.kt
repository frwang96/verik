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
import io.verik.compiler.ast.property.Name
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
        .let { it.subList(8, it.size) }
        .let { if (it[0].startsWith("`timescale")) it.drop(2) else it }
        .dropLastWhile { it.isEmpty() }

    assertEquals(expectedLines, actualLines)
}

fun ProjectContext.findDeclaration(nameString: String): EDeclaration {
    val declarationVisitor = object : TreeVisitor() {
        val declarations = ArrayList<EDeclaration>()
        override fun visitDeclaration(declaration: EDeclaration) {
            super.visitDeclaration(declaration)
            if (declaration.name == Name(nameString)) declarations.add(declaration)
        }
    }
    files.forEach {
        it.accept(declarationVisitor)
    }
    when (declarationVisitor.declarations.size) {
        0 -> throw IllegalArgumentException("Could not find declaration")
        1 -> {}
        else -> throw IllegalArgumentException("Could not find unique declaration")
    }
    return declarationVisitor.declarations[0]
}

fun ProjectContext.findExpression(nameString: String): EExpression {
    val expressionVisitor = object : TreeVisitor() {
        val expressions = ArrayList<EExpression>()
        override fun visitAbstractFunction(abstractFunction: EAbstractFunction) {
            super.visitAbstractFunction(abstractFunction)
            if (abstractFunction.name == Name(nameString)) {
                abstractFunction.bodyBlockExpression?.let { expressions.add(it) }
            }
        }

        override fun visitAbstractProperty(abstractProperty: EAbstractProperty) {
            super.visitAbstractProperty(abstractProperty)
            if (abstractProperty.name == Name(nameString)) {
                abstractProperty.initializer?.let { expressions.add(it) }
            }
        }
    }
    files.forEach {
        it.accept(expressionVisitor)
    }
    when (expressionVisitor.expressions.size) {
        0 -> throw IllegalArgumentException("Could not find expression")
        1 -> {}
        else -> throw IllegalArgumentException("Could not find unique expression")
    }
    return expressionVisitor.expressions[0]
}