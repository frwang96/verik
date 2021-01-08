/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.tx.build

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.sv.ast.SvProperty
import verikc.sv.ast.SvTypeExtracted

object TxBuilderTypeExtracted {

    fun buildAlignedLine(property: SvProperty, vararg tokens: String): TxAlignedLine {
        return TxAlignedLine(
            property.line,
            listOf(*tokens) + listOf(
                property.typeExtracted.identifier,
                property.typeExtracted.packed,
                property.identifier,
                property.typeExtracted.unpacked
            )
        )
    }

    fun buildWithoutPackedUnpacked(property: SvProperty): String {
        if (property.typeExtracted.packed != "")
            throw LineException("packed dimension not permitted for property", property.line)
        if (property.typeExtracted.unpacked != "")
            throw LineException("unpacked dimension not permitted for property", property.line)
        return property.typeExtracted.identifier + " " + property.identifier
    }

    fun buildWithoutUnpacked(typeExtracted: SvTypeExtracted, identifier: String, line: Line): String {
        if (typeExtracted.unpacked != "")
            throw LineException("unpacked dimension not permitted for return type", line)
        var string = typeExtracted.identifier
        if (typeExtracted.packed != "") string += " " + typeExtracted.packed
        string += " $identifier"
        return string
    }
}
