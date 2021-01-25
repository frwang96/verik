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

package verikc.sv.check

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.lang.util.LangIdentifierUtil

class SvScopeTable {

    private val identifierSet = HashSet<String>()

    fun add(identifier: String, line: Line) {
        if (LangIdentifierUtil.isReservedKeyword(identifier))
            throw LineException("identifier $identifier is reserved in SystemVerilog", line)
        if (identifier in identifierSet)
            throw LineException("identifier $identifier has already been defined in the scope", line)
        identifierSet.add(identifier)
    }
}