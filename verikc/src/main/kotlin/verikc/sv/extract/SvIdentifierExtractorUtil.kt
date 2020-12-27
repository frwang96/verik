/*
 * Copyright 2020 Francis Wang
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

package verikc.sv.extract

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.ps.ast.PsDeclaration

object SvIdentifierExtractorUtil {

    fun identifierWithoutUnderscore(declaration: PsDeclaration): String {
        return identifierWithoutUnderscore(declaration.identifier, declaration.line)
    }

    fun identifierWithoutUnderscore(identifier: String, line: Line): String {
        if (identifier.getOrNull(0) != '_')
            throw LineException("expected identifier to begin with an underscore", line)
        return identifier.substring(1)
    }

    fun enumPropertyIdentifier(enumIdentifier: String, enumPropertyIdentifier: String, line: Line): String {
        val prefix = identifierWithoutUnderscore(enumIdentifier, line).toUpperCase()
        return prefix + "_" + enumPropertyIdentifier
    }
}