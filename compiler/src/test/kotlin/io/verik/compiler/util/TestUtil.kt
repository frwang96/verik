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

fun assertElementEquals(expected: String, actual: VkElement) {
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

    val regexBuilder = StringBuilder()
    expectedString.toCharArray().forEach {
        when (it) {
            '*' -> regexBuilder.append(".+")
            in listOf('(', ')', '[', ']', '$') -> regexBuilder.append("\\$it")
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

fun ProjectContext.findDeclaration(nameString: String): VkDeclaration {
    val declarationVisitor = object : TreeVisitor() {
        val declarations = ArrayList<VkDeclaration>()
        override fun visitDeclaration(declaration: VkDeclaration) {
            super.visitDeclaration(declaration)
            if (declaration.name == Name(nameString)) declarations.add(declaration)
        }
    }
    vkFiles.forEach {
        it.accept(declarationVisitor)
    }
    when (declarationVisitor.declarations.size) {
        0 -> throw IllegalArgumentException("Could not find declaration")
        1 -> {}
        else -> throw IllegalArgumentException("Could not find unique declaration")
    }
    return declarationVisitor.declarations[0]
}

fun ProjectContext.findExpression(nameString: String): VkExpression {
    val expressionVisitor = object : TreeVisitor() {
        val expressions = ArrayList<VkExpression>()
        override fun visitBaseFunction(baseFunction: VkBaseFunction) {
            super.visitBaseFunction(baseFunction)
            if (baseFunction.name == Name(nameString)) {
                baseFunction.bodyBlockExpression?.let { expressions.add(it) }
            }
        }

        override fun visitBaseProperty(baseProperty: VkBaseProperty) {
            super.visitBaseProperty(baseProperty)
            if (baseProperty.name == Name(nameString)) {
                baseProperty.initializer?.let { expressions.add(it) }
            }
        }
    }
    vkFiles.forEach {
        it.accept(expressionVisitor)
    }
    when (expressionVisitor.expressions.size) {
        0 -> throw IllegalArgumentException("Could not find expression")
        1 -> {}
        else -> throw IllegalArgumentException("Could not find unique expression")
    }
    return expressionVisitor.expressions[0]
}