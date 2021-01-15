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

package verikc.sv.ast

import verikc.base.ast.Line
import verikc.ps.ast.PsCls
import verikc.sv.extract.SvIdentifierExtractorUtil

data class SvCls(
    val line: Line,
    val identifier: String
) {

    constructor(cls: PsCls): this(
        cls.line,
        SvIdentifierExtractorUtil.identifierWithoutUnderscore(cls.identifier, cls.line)
    )
}
