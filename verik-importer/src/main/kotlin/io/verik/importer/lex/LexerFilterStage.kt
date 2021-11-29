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

package io.verik.importer.lex

import io.verik.importer.antlr.SystemVerilogLexer
import io.verik.importer.common.ImporterStage
import io.verik.importer.main.ImporterContext
import io.verik.importer.message.Messages

object LexerFilterStage : ImporterStage() {

    private val fragmentPairMap = HashMap<Int, Int>()
    private val fragmentPairSet = HashSet<Int>()

    init {
        fragmentPairMap[SystemVerilogLexer.LPAREN_STAR] = SystemVerilogLexer.RPAREN_STAR

        fragmentPairMap.keys.forEach { fragmentPairSet.add(it) }
        fragmentPairMap.values.forEach { fragmentPairSet.add(it) }
    }

    override fun process(importerContext: ImporterContext) {
        val fragmentPairStack = ArrayList<LexerFragment>()
        val lexerFragments = ArrayList<LexerFragment>()
        importerContext.lexerFragments.forEach {
            if (it.type in fragmentPairSet) {
                if (it.type in fragmentPairMap) {
                    fragmentPairStack.add(it)
                } else {
                    val fragmentPairFragment = fragmentPairStack.lastOrNull()
                    if (fragmentPairFragment != null && it.type == fragmentPairMap[fragmentPairFragment.type]) {
                        fragmentPairStack.removeLast()
                    } else {
                        val location = importerContext.lexerCharStream.getLocation(it)
                        Messages.MISMATCHED_TOKEN.on(location, SystemVerilogLexer.VOCABULARY.getDisplayName(it.type))
                    }
                }
            } else {
                if (fragmentPairStack.isEmpty())
                    lexerFragments.add(it)
            }
        }
        if (fragmentPairStack.isNotEmpty()) {
            fragmentPairStack.forEach {
                val location = importerContext.lexerCharStream.getLocation(it)
                Messages.MISMATCHED_TOKEN.on(location, SystemVerilogLexer.VOCABULARY.getDisplayName(it.type))
            }
            lexerFragments.add(importerContext.lexerFragments.last())
        }
        importerContext.lexerFragments = lexerFragments
    }
}
