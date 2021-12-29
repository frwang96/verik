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

package io.verik.importer.preprocess

import io.verik.importer.message.Messages
import org.antlr.v4.runtime.tree.TerminalNode

class PreprocessContext {

    private val macros = HashMap<String, Macro>()
    private val enableStack = ArrayList<Boolean>()

    fun setMacro(name: String, macro: Macro) {
        macros[name] = macro
    }

    fun getMacro(name: String): Macro? {
        return macros[name]
    }

    fun pushEnable(enable: Boolean) {
        enableStack.add(enable)
    }

    fun popEnable(terminalNode: TerminalNode): Boolean {
        val enable = isEnable()
        if (enableStack.isEmpty()) {
            Messages.UNMATCHED_ENDIF.on(terminalNode)
        } else {
            enableStack.removeLast()
        }
        return enable
    }

    fun isEnable(): Boolean {
        return enableStack.lastOrNull() ?: true
    }
}
