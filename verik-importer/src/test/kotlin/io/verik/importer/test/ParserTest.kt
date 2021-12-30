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

package io.verik.importer.test

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.antlr.SystemVerilogParserBaseVisitor
import io.verik.importer.main.StageSequencer
import io.verik.importer.main.StageType
import org.antlr.v4.runtime.tree.RuleNode

abstract class ParserTest : BaseTest() {

    fun driveParserTest(rules: List<Int>, content: String) {
        val projectContext = getProjectContext(content)
        val stageSequence = StageSequencer.getStageSequence()
        stageSequence.processUntil(projectContext, StageType.PARSE)
        val parseTreeIndexerVisitor = ParseTreeIndexerVisitor()
        projectContext.parseTree.accept(parseTreeIndexerVisitor)
        rules.forEach {
            assert(it in parseTreeIndexerVisitor.ruleSet) {
                "Rule not found: ${SystemVerilogParser.ruleNames[it]}"
            }
        }
    }

    private class ParseTreeIndexerVisitor : SystemVerilogParserBaseVisitor<Unit>() {

        val ruleSet = HashSet<Int>(0)

        override fun visitChildren(node: RuleNode?) {
            super.visitChildren(node)
            if (node != null) {
                ruleSet.add(node.ruleContext.ruleIndex)
            }
        }
    }
}
