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

package io.verik.importer.common

import io.verik.importer.antlr.SystemVerilogLexer

enum class FragmentType(val index: Int) {
    EOF(SystemVerilogLexer.EOF),
    SEMICOLON(SystemVerilogLexer.SEMICOLON),
    ENDMODULE(SystemVerilogLexer.ENDMODULE),
    MODULE(SystemVerilogLexer.MODULE),
    SimpleIdentifier(SystemVerilogLexer.SimpleIdentifier);

    companion object {

        private val typeMap = HashMap<Int, FragmentType>()

        init {
            values().forEach { typeMap[it.index] = it }
        }

        operator fun invoke(type: Int): FragmentType {
            return typeMap[type]
                ?: throw IllegalArgumentException(
                    "Unable to get fragment type: ${SystemVerilogLexer.VOCABULARY.getSymbolicName(type)}"
                )
        }
    }
}
